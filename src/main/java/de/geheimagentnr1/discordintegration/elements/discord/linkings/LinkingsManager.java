package de.geheimagentnr1.discordintegration.elements.discord.linkings;

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
	
	public static void updateWhitelist( Consumer<Throwable> errorHandler ) throws IOException {
		
		if( isEnabled() ) {
			updateWhitelist( LinkingsFileManager.load(), errorHandler );
		}
	}
	
	private static void updateWhitelist(
		Linkings linkings,
		Consumer<Throwable> errorHandler )
		throws IOException {
		
		updateWhitelist( linkings, List.of(), errorHandler );
	}
	
	private static void updateWhitelist(
		Linkings linkings,
		List<Linking> removedLinkings,
		Consumer<Throwable> errorHandler )
		throws IOException {
		
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
				if( !ServerConfig.WHITELIST_CONFIG.useSingleLinkingManagement() || linking.isActive() ) {
					if( hasCorrectRole( member ) ) {
						activateList.add( minecraftGameProfile );
					} else {
						deactivateList.add( minecraftGameProfile );
					}
				} else {
					deactivateList.add( minecraftGameProfile );
				}
				linking.setDiscordName( member.getUser().getName() );
				LinkingsManagementMessageManager.sendOrEditMessage(
					member,
					linking,
					messageId -> {
						try {
							linking.setMessageId( messageId );
							LinkingsFileManager.save( linkings );
						} catch( Throwable throwable ) {
							errorHandler.accept( throwable );
						}
					}
				);
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
		Consumer<Throwable> errorHandler ) throws IOException {
		
		Linkings linkings = LinkingsFileManager.load();
		Optional<Linking> foundLinkingOptional = linkings.findLinking( linking );
		if( foundLinkingOptional.isPresent() ) {
			Linking foundLinking = foundLinkingOptional.get();
			foundLinking.setDiscordName( linking.getDiscordName() );
			foundLinking.setActive( linking.isActive() );
			foundLinking.setMessageId( linking.getMessageId() );
			foundLinking.getMinecraftGameProfile().setName( linking.getMinecraftGameProfile().getName() );
		} else {
			linkings.add( linking );
		}
		LinkingsFileManager.save( linkings );
		updateWhitelist( linkings, errorHandler );
	}
	
	public static void createLinking(
		Member member,
		GameProfile gameProfile,
		Consumer<Boolean> successHandler,
		Consumer<Throwable> errorHandler ) throws IOException {
		
		if( isEnabled() ) {
			Linking linking = Linking.builder()
				.discordMemberId( member.getIdLong() )
				.discordName( member.getUser().getName() )
				.active( !ServerConfig.WHITELIST_CONFIG.useSingleLinkingManagement() )
				.minecraftGameProfile( new MinecraftGameProfile( gameProfile ) )
				.build();
			
			Linkings linkings = LinkingsFileManager.load();
			
			if( linkings.findLinking( linking ).isPresent() ) {
				successHandler.accept( false );
			} else {
				LinkingsManagementMessageManager.sendOrEditMessage(
					member,
					linking,
					messageId -> {
						linking.setMessageId( messageId );
						try {
							updateLinking( linking, errorHandler );
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
	
	public static void removeLinking(
		Member member,
		GameProfile gameProfile,
		Consumer<Throwable> errorHandler ) throws IOException {
		
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
				updateWhitelist( linkings, List.of( removedLinking ), errorHandler );
				LinkingsManagementMessageManager.deleteMessage( removedLinking );
			}
		}
	}
	
	//package-private
	static void resendMessage( long messageId, Consumer<Throwable> errorHandler )
		throws IOException {
		
		if( isEnabled() ) {
			
			Linkings linkings = LinkingsFileManager.load();
			Optional<Linking> foundLinkingOptional = linkings.findLinking( messageId );
			
			if( foundLinkingOptional.isPresent() ) {
				Linking foundLinking = foundLinkingOptional.get();
				Member member = DiscordManager.getMember( foundLinking.getDiscordMemberId() );
				
				if( member == null ) {
					updateWhitelist( linkings, errorHandler );
				} else {
					foundLinking.setDiscordName( member.getUser().getName() );
					LinkingsManagementMessageManager.sendOrEditMessage(
						member,
						foundLinking,
						newMessageId -> {
							foundLinking.setMessageId( newMessageId );
							try {
								updateLinking( foundLinking, errorHandler );
							} catch( Throwable exception ) {
								errorHandler.accept( exception );
							}
						}
					);
				}
			}
		}
	}
	
	//package-private
	static void removeLinkings( Member member, Consumer<Throwable> errorHandler ) throws IOException {
		
		if( isEnabled() ) {
			Linkings linkings = LinkingsFileManager.load();
			
			List<Linking> removedLinkings = linkings.findLinkings( member.getIdLong() );
			if( linkings.remove( member.getIdLong() ) ) {
				removedLinkings.forEach( linkings::remove );
				LinkingsFileManager.save( linkings );
				removedLinkings.forEach( LinkingsManager::sendLinkingRemovedMessage );
				updateWhitelist( linkings, removedLinkings, errorHandler );
				removedLinkings.forEach( LinkingsManagementMessageManager::deleteMessage );
			}
		}
	}
	
	//package-private
	static void changeActiveStateOfLinking(
		long messageId,
		boolean shouldActive,
		Consumer<Throwable> errorHandler ) throws IOException {
		
		if( isEnabled() ) {
			
			Linkings linkings = LinkingsFileManager.load();
			Optional<Linking> foundLinkingOptional = linkings.findLinking( messageId );
			
			if( foundLinkingOptional.isPresent() ) {
				Linking foundLinking = foundLinkingOptional.get();
				foundLinking.setActive( shouldActive );
				Member member = DiscordManager.getMember( foundLinking.getDiscordMemberId() );
				
				if( member == null ) {
					updateWhitelist( linkings, errorHandler );
				} else {
					foundLinking.setDiscordName( member.getUser().getName() );
					LinkingsManagementMessageManager.sendOrEditMessage(
						member,
						foundLinking,
						newMessageId -> {
							foundLinking.setMessageId( newMessageId );
							try {
								updateLinking( foundLinking, errorHandler );
							} catch( Throwable exception ) {
								errorHandler.accept( exception );
							}
						}
					);
				}
			}
		}
	}
}
