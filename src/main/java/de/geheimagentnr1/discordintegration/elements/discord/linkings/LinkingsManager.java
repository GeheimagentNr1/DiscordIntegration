package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import com.mantledillusion.essentials.json.patch.PatchUtil;
import com.mantledillusion.essentials.json.patch.model.Patch;
import com.mojang.authlib.GameProfile;
import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.elements.discord.AbstractDiscordIntegrationServiceProvider;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linking;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linkings;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.MinecraftGameProfile;
import de.geheimagentnr1.minecraft_forge_api.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;


@Getter( AccessLevel.PROTECTED )
@Log4j2
@RequiredArgsConstructor
public class LinkingsManager extends AbstractDiscordIntegrationServiceProvider {
	
	
	@NotNull
	private final DiscordIntegration discordIntegration;
	
	public boolean isEnabled() {
		
		return discordManager().isInitialized() && serverConfig().getWhitelistConfig().isEnabled();
	}
	
	private boolean hasSingleLinkingManagementRole( @NotNull Member member ) {
		
		return discordManager().hasCorrectRole(
			member,
			serverConfig().getWhitelistConfig().getSingleLinkingManagementRoleId()
		);
	}
	
	private void sendLinkingCreatedMessage( @NotNull Linking linking ) {
		
		log.info(
			"Created Linking between Discord account \"{}\" and Minecraft account \"{}\"",
			linking.getDiscordUsername(),
			linking.getMinecraftGameProfile().getName()
		);
		if( serverConfig().getManagementConfig().getManagementMessagesConfig().getLinkingCreated().isEnabled() ) {
			managementManager().sendMessage(
				MessageUtil.replaceParameters(
					serverConfig().getManagementConfig().getManagementMessagesConfig().getLinkingCreated().getMessage(),
					Map.of(
						"username", linking.getDiscordUsername(),
						"nickname", linking.getDiscordNickname(),
						"player", linking.getMinecraftGameProfile().getName()
					)
				)
			);
		}
	}
	
	private void sendLinkingRemovedMessage( @NotNull Linking linking ) {
		
		log.info(
			"Removed Linking between Discord account \"{}\" and Minecraft account \"{}\"",
			linking.getDiscordUsername(),
			linking.getMinecraftGameProfile().getName()
		);
		if( serverConfig().getManagementConfig().getManagementMessagesConfig().getLinkingRemoved().isEnabled() ) {
			managementManager().sendMessage(
				MessageUtil.replaceParameters(
					serverConfig().getManagementConfig().getManagementMessagesConfig().getLinkingRemoved().getMessage(),
					Map.of(
						"username", linking.getDiscordUsername(),
						"nickname", linking.getDiscordNickname(),
						"player", linking.getMinecraftGameProfile().getName()
					)
				)
			);
		}
	}
	
	private void sendFinishedWhitelistUpdateWithForcedMessageUpdate( boolean forceMessageUpdate ) {
		
		if( forceMessageUpdate ) {
			log.info( "Finished check of Discord whitelist with forced message update" );
			if( serverConfig().getManagementConfig().getManagementMessagesConfig()
				.getWhitelistUpdateWithForcedMessageUpdateFinished()
				.isEnabled() ) {
				managementManager().sendMessage(
					serverConfig().getManagementConfig().getManagementMessagesConfig()
						.getWhitelistUpdateWithForcedMessageUpdateFinished()
						.getMessage()
				);
			}
		}
	}
	
	//package-private
	void updateWhitelist( @NotNull Consumer<Throwable> errorHandler ) throws IOException {
		
		updateWhitelist( errorHandler, false );
	}
	
	public void updateWhitelist( @NotNull Consumer<Throwable> errorHandler, boolean forceMessageUpdate )
		throws IOException {
		
		if( isEnabled() ) {
			if( forceMessageUpdate ) {
				log.info( "Start check of Discord whitelist with forced message update" );
				if( serverConfig().getManagementConfig().getManagementMessagesConfig()
					.getWhitelistUpdateWithForcedMessageUpdateStart()
					.isEnabled() ) {
					managementManager().sendMessage(
						serverConfig().getManagementConfig().getManagementMessagesConfig()
							.getWhitelistUpdateWithForcedMessageUpdateStart()
							.getMessage()
					);
				}
			}
			updateWhitelist( List.of(), errorHandler, forceMessageUpdate );
		}
	}
	
	private synchronized void updateWhitelist(
		@NotNull List<Linking> removedLinkings,
		@NotNull Consumer<Throwable> errorHandler ) throws IOException {
		
		updateWhitelist( removedLinkings, errorHandler, false );
	}
	
	private synchronized void updateWhitelist(
		@NotNull List<Linking> removedLinkings,
		@NotNull Consumer<Throwable> errorHandler,
		boolean forceMessageUpdate ) throws IOException {
		
		Linkings linkings = linkingsFileManager().load();
		List<MinecraftGameProfile> removedMinecraftGameProfiles = removedLinkings.stream()
			.map( Linking::getMinecraftGameProfile )
			.toList();
		List<MinecraftGameProfile> activateList = new ArrayList<>();
		List<MinecraftGameProfile> deactivateList = new ArrayList<>( removedMinecraftGameProfiles );
		
		int linkingCount = linkings.getLinkings().size();
		int linkingCounter = 0;
		for( Linking linking : linkings.getLinkings() ) {
			linkingCounter++;
			Member member = discordManager().getMember( linking.getDiscordMemberId() );
			MinecraftGameProfile minecraftGameProfile = linking.getMinecraftGameProfile();
			
			if( member == null ) {
				linkings.remove( linking );
				linkingsFileManager().save( linkings );
				sendLinkingRemovedMessage( linking );
				linkingsManagementMessageManager().deleteMessage( linking );
			} else {
				PatchUtil.Snapshot snapshot = PatchUtil.take( linking );
				boolean hasRole = hasCorrectRole( member );
				if( !serverConfig().getWhitelistConfig().useSingleLinkingManagement() || linking.isActive() ) {
					if( hasRole ) {
						activateList.add( minecraftGameProfile );
					} else {
						deactivateList.add( minecraftGameProfile );
					}
				} else {
					deactivateList.add( minecraftGameProfile );
				}
				linking.setHasRole( hasRole );
				linking.setDiscordUsername( discordManager().getMemberAsTag( member ) );
				linking.setDiscordNickname( member.getEffectiveName() );
				boolean hasChanged = !snapshot.peek().isEmpty();
				if( hasChanged || forceMessageUpdate ) {
					int finalLinkingCounter = linkingCounter;
					linkingsManagementMessageManager().sendOrEditMessage(
						member,
						linking,
						hasChanged,
						messageId -> {
							try {
								linking.setMessageId( messageId );
								updateLinking( linking, snapshot.capture(), errorHandler, false );
								if( forceMessageUpdate ) {
									log.info(
										"Checked message of linking {} of {} linkings.",
										finalLinkingCounter,
										linkingCount
									);
									if( finalLinkingCounter == linkingCount ) {
										sendFinishedWhitelistUpdateWithForcedMessageUpdate( true );
									}
								}
							} catch( Throwable throwable ) {
								errorHandler.accept( throwable );
							}
						}
					);
				}
			}
		}
		if( linkingCount == 0 ) {
			sendFinishedWhitelistUpdateWithForcedMessageUpdate( forceMessageUpdate );
		}
		deactivateList.stream()
			.filter( minecraftGameProfile -> !activateList.contains( minecraftGameProfile ) )
			.forEach( whitelistManager()::removeFromWhitelist );
		activateList.forEach( whitelistManager()::addToWhitelist );
	}
	
	//package-private
	boolean hasCorrectRole( @NotNull Member member ) {
		
		return !serverConfig().getWhitelistConfig().useRole() ||
			discordManager().hasCorrectRole( member, serverConfig().getWhitelistConfig().getRoleId() );
	}
	
	//package-private
	boolean isCorrectRole( @NotNull Role role ) {
		
		return serverConfig().getWhitelistConfig().useRole() &&
			serverConfig().getWhitelistConfig().getRoleId() == role.getIdLong();
	}
	
	private void updateLinking(
		@NotNull Linking linking,
		@NotNull List<Patch> patches,
		@NotNull Consumer<Throwable> errorHandler )
		throws IOException {
		
		updateLinking( linking, patches, errorHandler, true );
	}
	
	private synchronized void updateLinking(
		@NotNull Linking linking,
		@NotNull List<Patch> patches,
		@NotNull Consumer<Throwable> errorHandler,
		boolean updateWhitelist )
		throws IOException {
		
		Linkings linkings = linkingsFileManager().load();
		Optional<Linking> foundLinkingOptional = linkings.findLinking( linking );
		if( foundLinkingOptional.isPresent() ) {
			Linking foundLinking = foundLinkingOptional.get();
			if( !patches.isEmpty() ) {
				Linking updatedLinking = PatchUtil.apply( foundLinking, patches );
				linkings.remove( foundLinking );
				linkings.add( updatedLinking );
			}
		} else {
			linkings.add( linking );
		}
		linkingsFileManager().save( linkings );
		if( updateWhitelist ) {
			updateWhitelist( errorHandler );
		}
	}
	
	public synchronized void createLinking(
		@NotNull Member member,
		@NotNull GameProfile gameProfile,
		@NotNull Consumer<Boolean> successHandler,
		@NotNull Runnable whitelistDisabledErrorHandler,
		@NotNull Consumer<Throwable> errorHandler ) throws IOException {
		
		if( isEnabled() ) {
			Linking linking = Linking.builder()
				.discordMemberId( member.getIdLong() )
				.discordUsername( discordManager().getMemberAsTag( member ) )
				.discordNickname( member.getEffectiveName() )
				.hasRole( hasCorrectRole( member ) )
				.active( !serverConfig().getWhitelistConfig().useSingleLinkingManagement() )
				.minecraftGameProfile( new MinecraftGameProfile( gameProfile ) )
				.build();
			
			Linkings linkings = linkingsFileManager().load();
			Optional<Linking> foundLinkingOptional = linkings.findLinking( linking );
			
			if( foundLinkingOptional.isPresent() ) {
				Linking foundLinking = foundLinkingOptional.get();
				PatchUtil.Snapshot snapshot = PatchUtil.take( foundLinking );
				foundLinking.setDiscordUsername( discordManager().getMemberAsTag( member ) );
				foundLinking.setDiscordNickname( member.getEffectiveName() );
				foundLinking.getMinecraftGameProfile().setName( gameProfile.getName() );
				boolean hasChanged = !snapshot.peek().isEmpty();
				linkingsManagementMessageManager().sendOrEditMessage(
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
				linkingsManagementMessageManager().sendOrEditMessage(
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
		} else {
			whitelistDisabledErrorHandler.run();
		}
	}
	
	public synchronized void removeLinking(
		@NotNull Member member,
		@NotNull GameProfile gameProfile,
		@NotNull Runnable successHandler,
		@NotNull Runnable whitelistDisabledErrorHandler,
		@NotNull Consumer<Throwable> errorHandler )
		throws IOException {
		
		if( isEnabled() ) {
			Linkings linkings = linkingsFileManager().load();
			
			Optional<Linking> foundLinking = linkings.findLinking(
				Linking.builder()
					.discordMemberId( member.getIdLong() )
					.minecraftGameProfile( new MinecraftGameProfile( gameProfile ) )
					.build()
			);
			if( foundLinking.isPresent() ) {
				Linking removedLinking = foundLinking.get();
				linkings.remove( removedLinking );
				linkingsFileManager().save( linkings );
				sendLinkingRemovedMessage( removedLinking );
				updateWhitelist( List.of( removedLinking ), errorHandler );
				linkingsManagementMessageManager().deleteMessage( removedLinking );
			}
			successHandler.run();
		} else {
			whitelistDisabledErrorHandler.run();
		}
	}
	
	//package-private
	synchronized void resendMessage( long messageId, @NotNull Consumer<Throwable> errorHandler ) throws IOException {
		
		if( isEnabled() ) {
			
			Linkings linkings = linkingsFileManager().load();
			Optional<Linking> foundLinkingOptional = linkings.findLinking( messageId );
			
			if( foundLinkingOptional.isPresent() ) {
				Linking foundLinking = foundLinkingOptional.get();
				PatchUtil.Snapshot snapshot = PatchUtil.take( foundLinking );
				
				updateLinking( foundLinking, snapshot, errorHandler, false );
			}
		}
	}
	
	//package-private
	synchronized void removeLinkings( @NotNull Member member, @NotNull Consumer<Throwable> errorHandler )
		throws IOException {
		
		if( isEnabled() ) {
			Linkings linkings = linkingsFileManager().load();
			
			List<Linking> removedLinkings = linkings.findLinkings( member.getIdLong() );
			if( linkings.remove( member.getIdLong() ) ) {
				removedLinkings.forEach( linkings::remove );
				linkingsFileManager().save( linkings );
				removedLinkings.forEach( this::sendLinkingRemovedMessage );
				updateWhitelist( removedLinkings, errorHandler );
				removedLinkings.forEach( linkingsManagementMessageManager()::deleteMessage );
			}
		}
	}
	
	//package-private
	synchronized void changeActiveStateOfLinking(
		@NotNull Member member,
		long messageId,
		boolean shouldActive,
		@NotNull Consumer<Throwable> errorHandler )
		throws IOException {
		
		if( isEnabled() && hasSingleLinkingManagementRole( member ) ) {
			
			Linkings linkings = linkingsFileManager().load();
			Optional<Linking> foundLinkingOptional = linkings.findLinking( messageId );
			
			if( foundLinkingOptional.isPresent() ) {
				Linking foundLinking = foundLinkingOptional.get();
				PatchUtil.Snapshot snapshot = PatchUtil.take( foundLinking );
				foundLinking.setActive( shouldActive );
				updateLinking( foundLinking, snapshot, errorHandler, true );
			}
		}
	}
	
	private synchronized void updateLinking(
		@NotNull Linking linking,
		@NotNull PatchUtil.Snapshot snapshot,
		@NotNull Consumer<Throwable> errorHandler,
		boolean updateWhitelist )
		throws IOException {
		
		Member member = discordManager().getMember( linking.getDiscordMemberId() );
		
		if( member == null ) {
			updateWhitelist( errorHandler );
		} else {
			linking.setDiscordUsername( discordManager().getMemberAsTag( member ) );
			linking.setDiscordNickname( member.getEffectiveName() );
			boolean hasChanged = !snapshot.peek().isEmpty();
			linkingsManagementMessageManager().sendOrEditMessage(
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
