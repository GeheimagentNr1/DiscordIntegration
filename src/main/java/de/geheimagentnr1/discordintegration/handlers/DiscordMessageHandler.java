package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import de.geheimagentnr1.minecraft_forge_api.events.ForgeEventHandlerInterface;
import de.geheimagentnr1.minecraft_forge_api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
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
	
	@SubscribeEvent
	@Override
	public void handleServerStartedEvent( @NotNull ServerStartedEvent event ) {
		
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
	
	@SubscribeEvent
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
	
	@SubscribeEvent
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
	
	@SubscribeEvent
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
	
	@SubscribeEvent
	@Override
	public void handleServerChatEvent( @NotNull ServerChatEvent event ) {
		
		if( !event.isCanceled() ) {
			chatManager.sendChatMessage( event.getPlayer(), event.getRawText() );
		}
	}
	
	@SubscribeEvent
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
	
	@SubscribeEvent
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
