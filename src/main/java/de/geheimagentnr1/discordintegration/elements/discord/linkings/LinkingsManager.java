package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import com.mantledillusion.essentials.json.patch.PatchUtil;
import com.mantledillusion.essentials.json.patch.model.Patch;
import com.mojang.authlib.GameProfile;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linking;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linkings;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.MinecraftGameProfile;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


@Log4j2
public class LinkingsManager {
	
	
	public static boolean isEnabled() {
		
		return DiscordManager.isInitialized() && ServerConfig.WHITELIST_CONFIG.isEnabled();
	}
	
	private static void sendLinkingCreatedMessage( Linking linking ) {
		
		log.info(
			"Created Linking between Discord account \"{}\" and Minecraft account \"{}\"",
			linking.getDiscordName(),
			linking.getMinecraftGameProfile().getName()
		);
		if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getLinkingCreated().isEnabled() ) {
			ManagementManager.sendLinkingMessage(
				linking.getDiscordName(),
				linking.getMinecraftGameProfile().getName(),
				ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getLinkingCreated().getMessage()
			);
		}
	}
	
	private static void sendLinkingRemovedMessage( Linking linking ) {
		
		log.info(
			"Removed Linking between Discord account \"{}\" and Minecraft account \"{}\"",
			linking.getDiscordName(),
			linking.getMinecraftGameProfile().getName()
		);
		if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getLinkingRemoved().isEnabled() ) {
			ManagementManager.sendLinkingMessage(
				linking.getDiscordName(),
				linking.getMinecraftGameProfile().getName(),
				ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getLinkingRemoved().getMessage()
			);
		}
	}
	
	//package-private
	static void updateWhitelist( Consumer<Throwable> errorHandler ) throws IOException {
		
		updateWhitelist( errorHandler, false );
	}
	
	public static void updateWhitelist( Consumer<Throwable> errorHandler, boolean forceMessageUpdate )
		throws IOException {
		
		if( isEnabled() ) {
			updateWhitelist( List.of(), errorHandler, forceMessageUpdate );
		}
	}
	
	private static synchronized void updateWhitelist(
		List<Linking> removedLinkings,
		Consumer<Throwable> errorHandler ) throws IOException {
		
		updateWhitelist( removedLinkings, errorHandler, false );
	}
	
	private static synchronized void updateWhitelist(
		List<Linking> removedLinkings,
		Consumer<Throwable> errorHandler,
		boolean forceMessageUpdate ) throws IOException {
		
		Linkings linkings = LinkingsFileManager.load();
		List<MinecraftGameProfile> removedMinecraftGameProfiles = removedLinkings.stream()
			.map( Linking::getMinecraftGameProfile )
			.toList();
		List<MinecraftGameProfile> activateList = new ArrayList<>();
		List<MinecraftGameProfile> deactivateList = new ArrayList<>( removedMinecraftGameProfiles );
		
		for( Linking linking : linkings.getLinkings() ) {
			Member member = DiscordManager.getMember( linking.getDiscordMemberId() );
			MinecraftGameProfile minecraftGameProfile = linking.getMinecraftGameProfile();
			
			if( member == null ) {
				linkings.remove( linking );
				LinkingsFileManager.save( linkings );
				sendLinkingRemovedMessage( linking );
				LinkingsManagementMessageManager.deleteMessage( linking );
			} else {
				PatchUtil.Snapshot snapshot = PatchUtil.take( linking );
				boolean hasRole = hasCorrectRole( member );
				if( !ServerConfig.WHITELIST_CONFIG.useSingleLinkingManagement() || linking.isActive() ) {
					if( hasRole ) {
						activateList.add( minecraftGameProfile );
					} else {
						deactivateList.add( minecraftGameProfile );
					}
				} else {
					deactivateList.add( minecraftGameProfile );
				}
				linking.setHasRole( hasRole );
				linking.setDiscordName( member.getUser().getName() );
				boolean hasChanged = !snapshot.peek().isEmpty();
				if( hasChanged || forceMessageUpdate ) {
					LinkingsManagementMessageManager.sendOrEditMessage(
						member,
						linking,
						hasChanged,
						messageId -> {
							try {
								linking.setMessageId( messageId );
								updateLinking( linking, snapshot.capture(), errorHandler, false );
							} catch( Throwable throwable ) {
								errorHandler.accept( throwable );
							}
						}
					);
				}
			}
		}
		deactivateList.stream()
			.filter( minecraftGameProfile -> !activateList.contains( minecraftGameProfile ) )
			.forEach( WhitelistManager::removeFromWhitelist );
		activateList.forEach( WhitelistManager::addToWhitelist );
	}
	
	//package-private
	static boolean hasCorrectRole( Member member ) {
		
		return !ServerConfig.WHITELIST_CONFIG.useRole() ||
			DiscordManager.hasCorrectRole( member, ServerConfig.WHITELIST_CONFIG.getRoleId() );
	}
	
	//package-private
	static boolean isCorrectRole( Role role ) {
		
		return ServerConfig.WHITELIST_CONFIG.useRole() &&
			ServerConfig.WHITELIST_CONFIG.getRoleId() == role.getIdLong();
	}
	
	private static void updateLinking(
		Linking linking,
		List<Patch> patches,
		Consumer<Throwable> errorHandler )
		throws IOException {
		
		updateLinking( linking, patches, errorHandler, true );
	}
	
	private static synchronized void updateLinking(
		Linking linking,
		List<Patch> patches,
		Consumer<Throwable> errorHandler,
		boolean updateWhitelist )
		throws IOException {
		
		Linkings linkings = LinkingsFileManager.load();
		Optional<Linking> foundLinkingOptional = linkings.findLinking( linking );
		if( foundLinkingOptional.isPresent() ) {
			Linking foundLinking = foundLinkingOptional.get();
			Linking.applyPatches( linking, foundLinking, patches );
		} else {
			linkings.add( linking );
		}
		LinkingsFileManager.save( linkings );
		if( updateWhitelist ) {
			updateWhitelist( errorHandler );
		}
	}
	
	public static synchronized void createLinking(
		Member member,
		GameProfile gameProfile,
		Consumer<Boolean> successHandler,
		Consumer<Throwable> errorHandler ) throws IOException {
		
		if( isEnabled() ) {
			Linking linking = Linking.builder()
				.discordMemberId( member.getIdLong() )
				.discordName( member.getUser().getName() )
				.hasRole( hasCorrectRole( member ) )
				.active( !ServerConfig.WHITELIST_CONFIG.useSingleLinkingManagement() )
				.minecraftGameProfile( new MinecraftGameProfile( gameProfile ) )
				.build();
			
			Linkings linkings = LinkingsFileManager.load();
			Optional<Linking> foundLinkingOptional = linkings.findLinking( linking );
			
			if( foundLinkingOptional.isPresent() ) {
				Linking foundLinking = foundLinkingOptional.get();
				PatchUtil.Snapshot snapshot = PatchUtil.take( foundLinking );
				foundLinking.setDiscordName( member.getUser().getName() );
				foundLinking.getMinecraftGameProfile().setName( gameProfile.getName() );
				boolean hasChanged = !snapshot.peek().isEmpty();
				LinkingsManagementMessageManager.sendOrEditMessage(
					member,
					foundLinking,
					hasChanged,
					newMessageId -> {
						foundLinking.setMessageId( newMessageId );
						try {
							updateLinking( foundLinking, snapshot.capture(), errorHandler );
							sendLinkingCreatedMessage( foundLinking );
							successHandler.accept( false );
						} catch( Throwable exception ) {
							errorHandler.accept( exception );
						}
					}
				);
			} else {
				LinkingsManagementMessageManager.sendOrEditMessage(
					member,
					linking,
					true,
					messageId -> {
						linking.setMessageId( messageId );
						try {
							updateLinking( linking, List.of(), errorHandler );
							sendLinkingCreatedMessage( linking );
							successHandler.accept( true );
						} catch( Throwable exception ) {
							errorHandler.accept( exception );
						}
					}
				);
			}
		}
	}
	
	public static synchronized void removeLinking(
		Member member,
		GameProfile gameProfile,
		Consumer<Throwable> errorHandler )
		throws IOException {
		
		if( isEnabled() ) {
			Linkings linkings = LinkingsFileManager.load();
			
			Optional<Linking> foundLinking = linkings.findLinking(
				Linking.builder()
					.discordMemberId( member.getIdLong() )
					.minecraftGameProfile( new MinecraftGameProfile( gameProfile ) )
					.build()
			);
			if( foundLinking.isPresent() ) {
				Linking removedLinking = foundLinking.get();
				linkings.remove( removedLinking );
				LinkingsFileManager.save( linkings );
				sendLinkingRemovedMessage( removedLinking );
				updateWhitelist( List.of( removedLinking ), errorHandler );
				LinkingsManagementMessageManager.deleteMessage( removedLinking );
			}
		}
	}
	
	//package-private
	static synchronized void resendMessage( long messageId, Consumer<Throwable> errorHandler ) throws IOException {
		
		if( isEnabled() ) {
			
			Linkings linkings = LinkingsFileManager.load();
			Optional<Linking> foundLinkingOptional = linkings.findLinking( messageId );
			
			if( foundLinkingOptional.isPresent() ) {
				Linking foundLinking = foundLinkingOptional.get();
				PatchUtil.Snapshot snapshot = PatchUtil.take( foundLinking );
				
				updateLinking( foundLinking, snapshot, errorHandler, false );
			}
		}
	}
	
	//package-private
	static synchronized void removeLinkings( Member member, Consumer<Throwable> errorHandler ) throws IOException {
		
		if( isEnabled() ) {
			Linkings linkings = LinkingsFileManager.load();
			
			List<Linking> removedLinkings = linkings.findLinkings( member.getIdLong() );
			if( linkings.remove( member.getIdLong() ) ) {
				removedLinkings.forEach( linkings::remove );
				LinkingsFileManager.save( linkings );
				removedLinkings.forEach( LinkingsManager::sendLinkingRemovedMessage );
				updateWhitelist( removedLinkings, errorHandler );
				removedLinkings.forEach( LinkingsManagementMessageManager::deleteMessage );
			}
		}
	}
	
	//package-private
	static synchronized void changeActiveStateOfLinking(
		long messageId,
		boolean shouldActive,
		Consumer<Throwable> errorHandler )
		throws IOException {
		
		if( isEnabled() ) {
			
			Linkings linkings = LinkingsFileManager.load();
			Optional<Linking> foundLinkingOptional = linkings.findLinking( messageId );
			
			if( foundLinkingOptional.isPresent() ) {
				Linking foundLinking = foundLinkingOptional.get();
				PatchUtil.Snapshot snapshot = PatchUtil.take( foundLinking );
				foundLinking.setActive( shouldActive );
				updateLinking( foundLinking, snapshot, errorHandler, true );
			}
		}
	}
	
	private static synchronized void updateLinking(
		Linking linking,
		PatchUtil.Snapshot snapshot,
		Consumer<Throwable> errorHandler,
		boolean updateWhitelist )
		throws IOException {
		
		Member member = DiscordManager.getMember( linking.getDiscordMemberId() );
		
		if( member == null ) {
			updateWhitelist( errorHandler );
		} else {
			linking.setDiscordName( member.getUser().getName() );
			boolean hasChanged = !snapshot.peek().isEmpty();
			LinkingsManagementMessageManager.sendOrEditMessage(
				member,
				linking,
				hasChanged,
				newMessageId -> {
					linking.setMessageId( newMessageId );
					try {
						updateLinking( linking, snapshot.capture(), errorHandler, updateWhitelist );
					} catch( Throwable exception ) {
						errorHandler.accept( exception );
					}
				}
			);
		}
	}
}
