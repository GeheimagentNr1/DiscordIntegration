package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import de.geheimagentnr1.discordintegration.api.events.ForgeEventHandlerInterface;
import de.geheimagentnr1.discordintegration.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


@RequiredArgsConstructor
public class DiscordMessageHandler implements ForgeEventHandlerInterface {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	private final DiscordManager discordManager;
	
	@NotNull
	private final ChatManager chatManager;
	
	@NotNull
	private final ManagementManager managementManager;
	
	@NotNull
	private final DiscordMessageBuilder discordMessageBuilder;
	
	@Override
	public void handleServerStartedEvent( @NotNull ServerStartedEvent event ) {
		
		// Initialize Discord connection when server starts (config values are now accessible)
		discordManager.init();
		if( serverConfig.getChatConfig().getChatMessagesConfig().getServerStarted().isEnabled() ) {
			chatManager.sendMessage(
				serverConfig.getChatConfig().getChatMessagesConfig().getServerStarted().getMessage()
			);
		}
		if( serverConfig.getManagementConfig().getManagementMessagesConfig().getServerStarted().isEnabled() ) {
			managementManager.sendMessage(
				serverConfig.getManagementConfig().getManagementMessagesConfig().getServerStarted().getMessage()
			);
		}
		discordManager.setServerStarted();
	}
	
	@Override
	public void handleServerStoppedEvent( @NotNull ServerStoppedEvent event ) {
		
		if( event.getServer().isRunning() ) {
			if( serverConfig.getChatConfig().getChatMessagesConfig().getServerCrashed().isEnabled() ) {
				chatManager.sendMessage(
					serverConfig.getChatConfig().getChatMessagesConfig().getServerCrashed().getMessage()
				);
			}
			if( serverConfig.getManagementConfig().getManagementMessagesConfig().getServerCrashed().isEnabled() ) {
				managementManager.sendMessage(
					serverConfig.getManagementConfig().getManagementMessagesConfig().getServerCrashed().getMessage()
				);
			}
		} else {
			if( serverConfig.getChatConfig().getChatMessagesConfig().getServerStopped().isEnabled() ) {
				chatManager.sendMessage(
					serverConfig.getChatConfig().getChatMessagesConfig().getServerStopped().getMessage()
				);
			}
			if( serverConfig.getManagementConfig().getManagementMessagesConfig().getServerStopped().isEnabled() ) {
				managementManager.sendMessage(
					serverConfig.getManagementConfig().getManagementMessagesConfig().getServerStopped().getMessage()
				);
			}
		}
		discordManager.stop();
	}
	
	@Override
	public void handlePlayerLoggedInEvent( @NotNull PlayerEvent.PlayerLoggedInEvent event ) {
		
		if( serverConfig.getChatConfig().getChatMessagesConfig().getPlayerJoined().isEnabled() ) {
			chatManager.sendMessage(
				MessageUtil.replaceParameters(
					serverConfig.getChatConfig().getChatMessagesConfig().getPlayerJoined().getMessage(),
					Map.of(
						"player", discordMessageBuilder.getEntityName( event.getEntity() )
					)
				)
			);
		}
		if( serverConfig.getManagementConfig().getManagementMessagesConfig().getPlayerJoined().isEnabled() ) {
			managementManager.sendMessage(
				MessageUtil.replaceParameters(
					serverConfig.getManagementConfig().getManagementMessagesConfig().getPlayerJoined().getMessage(),
					Map.of(
						"player", discordMessageBuilder.getEntityName( event.getEntity() )
					)
				)
			);
		}
		discordManager.updatePresence( ServerLifecycleHooks.getCurrentServer().getPlayerCount() );
	}
	
	@Override
	public void handlePlayerLoggedOutEvent( @NotNull PlayerEvent.PlayerLoggedOutEvent event ) {
		
		if( serverConfig.getChatConfig().getChatMessagesConfig().getPlayerLeft().isEnabled() ) {
			chatManager.sendMessage(
				MessageUtil.replaceParameters(
					serverConfig.getChatConfig().getChatMessagesConfig().getPlayerLeft().getMessage(),
					Map.of(
						"player", discordMessageBuilder.getEntityName( event.getEntity() )
					)
				)
			);
		}
		if( serverConfig.getManagementConfig().getManagementMessagesConfig().getPlayerLeft().isEnabled() ) {
			managementManager.sendMessage(
				MessageUtil.replaceParameters(
					serverConfig.getManagementConfig().getManagementMessagesConfig().getPlayerLeft().getMessage(),
					Map.of(
						"player", discordMessageBuilder.getEntityName( event.getEntity() )
					)
				)
			);
		}
		discordManager.updatePresence( ServerLifecycleHooks.getCurrentServer().getPlayerCount() - 1 );
	}
	
	@Override
	public void handleServerChatEvent( @NotNull ServerChatEvent event ) {
		
		if( !event.isCanceled() ) {
			chatManager.sendChatMessage( event.getPlayer(), event.getRawText() );
		}
	}
	
	@Override
	public void handleLivingDeathEvent( @NotNull LivingDeathEvent event ) {
		
		LivingEntity entity = event.getEntity();
		String name = discordMessageBuilder.getEntityName( entity );
		String default_message = discordMessageBuilder.buildDeathMessage( event, entity, name );
		
		if( entity instanceof Player ) {
			if( serverConfig.getChatConfig().getChatMessagesConfig().getPlayerDied().isEnabled() ) {
				chatManager.sendMessage(
					MessageUtil.replaceParameters(
						serverConfig.getChatConfig().getChatMessagesConfig().getPlayerDied().getMessage(),
						Map.of(
							"player", name,
							"default_message", default_message
						)
					)
				);
			}
		} else {
			if( entity instanceof TamableAnimal && ( (TamableAnimal)entity ).getOwnerUUID() != null ) {
				if( serverConfig.getChatConfig().getChatMessagesConfig().getTamedMobDied().isEnabled() ) {
					chatManager.sendMessage(
						MessageUtil.replaceParameters(
							serverConfig.getChatConfig().getChatMessagesConfig().getTamedMobDied().getMessage(),
							Map.of(
								"tamed_mob", name,
								"default_message", default_message
							)
						)
					);
				}
			}
		}
	}
	
	@Override
	public void handleAdvancementEarnEvent( @NotNull AdvancementEvent.AdvancementEarnEvent event ) {
		
		event.getAdvancement().value().display().ifPresent( displayInfo -> {
			if( displayInfo.shouldAnnounceChat() &&
				serverConfig.getChatConfig().getChatMessagesConfig().getPlayerGotAdvancement().isEnabled() ) {
				chatManager.sendMessage(
					MessageUtil.replaceParameters(
						serverConfig.getChatConfig().getChatMessagesConfig().getPlayerGotAdvancement().getMessage(),
						Map.of(
							"player", discordMessageBuilder.getEntityName( event.getEntity() ),
							"advancement_title", displayInfo.getTitle().getString(),
							"advancement_description", displayInfo.getDescription().getString(),
							"new_line", System.lineSeparator()
						)
					)
				);
			}
		} );
	}
}
