package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.commands.DiscordCommand;
import de.geheimagentnr1.discordintegration.elements.commands.EmoteToDiscordCommand;
import de.geheimagentnr1.discordintegration.elements.commands.SayToDiscordCommand;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import de.geheimagentnr1.discordintegration.util.MessageUtil;
import lombok.extern.log4j.Log4j2;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;


@Log4j2
@Mod.EventBusSubscriber(
	modid = DiscordIntegration.MODID,
	bus = Mod.EventBusSubscriber.Bus.FORGE,
	value = Dist.DEDICATED_SERVER
)
public class ForgeEventHandler {
	
	
	@SubscribeEvent
	public static void handlerRegisterCommandsEvent( RegisterCommandsEvent event ) {
		
		DiscordCommand.register( event.getDispatcher() );
		EmoteToDiscordCommand.register( event.getDispatcher() );
		SayToDiscordCommand.register( event.getDispatcher() );
	}
	
	@SubscribeEvent
	public static void handleServerStartedEvent( ServerStartedEvent event ) {
		
		if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerStarted().isEnabled() ) {
			ChatManager.sendMessage(
				ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerStarted().getMessage()
			);
		}
		if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getServerStarted().isEnabled() ) {
			ManagementManager.sendMessage(
				ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getServerStarted().getMessage()
			);
		}
		DiscordManager.setServerStarted();
	}
	
	@SubscribeEvent
	public static void handleServerStoppedEvent( ServerStoppedEvent event ) {
		
		if( event.getServer().isRunning() ) {
			if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerCrashed().isEnabled() ) {
				ChatManager.sendMessage(
					ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerCrashed().getMessage()
				);
			}
			if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getServerCrashed().isEnabled() ) {
				ManagementManager.sendMessage(
					ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getServerCrashed().getMessage()
				);
			}
		} else {
			if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerStopped().isEnabled() ) {
				ChatManager.sendMessage(
					ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerStopped().getMessage()
				);
			}
			if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getServerStopped().isEnabled() ) {
				ManagementManager.sendMessage(
					ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getServerStopped().getMessage()
				);
			}
		}
		DiscordManager.stop();
	}
	
	@SubscribeEvent
	public static void handlePlayerLoggedInEvent( PlayerEvent.PlayerLoggedInEvent event ) {
		
		if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerJoined().isEnabled() ) {
			ChatManager.sendMessage(
				MessageUtil.replaceParameters(
					ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerJoined().getMessage(),
					Map.of(
						"player", DiscordMessageBuilder.getEntityName( event.getEntity() )
					)
				)
			);
		}
		if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getPlayerJoined().isEnabled() ) {
			ManagementManager.sendMessage(
				MessageUtil.replaceParameters(
					ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getPlayerJoined().getMessage(),
					Map.of(
						"player", DiscordMessageBuilder.getEntityName( event.getEntity() )
					)
				)
			);
		}
		DiscordManager.updatePresence( ServerLifecycleHooks.getCurrentServer().getPlayerCount() );
	}
	
	@SubscribeEvent
	public static void handlePlayerLoggedOutEvent( PlayerEvent.PlayerLoggedOutEvent event ) {
		
		if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerLeft().isEnabled() ) {
			ChatManager.sendMessage(
				MessageUtil.replaceParameters(
					ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerLeft().getMessage(),
					Map.of(
						"player", DiscordMessageBuilder.getEntityName( event.getEntity() )
					)
				)
			);
		}
		if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getPlayerLeft().isEnabled() ) {
			ManagementManager.sendMessage(
				MessageUtil.replaceParameters(
					ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getPlayerLeft().getMessage(),
					Map.of(
						"player", DiscordMessageBuilder.getEntityName( event.getEntity() )
					)
				)
			);
		}
		DiscordManager.updatePresence( ServerLifecycleHooks.getCurrentServer().getPlayerCount() - 1 );
	}
	
	@SubscribeEvent
	public static void handleServerChatSubmittedEvent( ServerChatEvent event ) {
		
		if( !event.isCanceled() ) {
			ChatManager.sendChatMessage( event.getPlayer(), event.getRawText() );
		}
	}
	
	@SubscribeEvent
	public static void handleLivingDeathEvent( LivingDeathEvent event ) {
		
		LivingEntity entity = event.getEntity();
		String name = DiscordMessageBuilder.getEntityName( entity );
		String default_message = DiscordMessageBuilder.buildDeathMessage( event, entity, name );
		
		if( entity instanceof Player ) {
			if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerDied().isEnabled() ) {
				ChatManager.sendMessage(
					MessageUtil.replaceParameters(
						ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerDied().getMessage(),
						Map.of(
							"player", name,
							"default_message", default_message
						)
					)
				);
			}
		} else {
			if( entity instanceof TamableAnimal && ( (TamableAnimal)entity ).getOwnerUUID() != null ) {
				if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getTamedMobDied().isEnabled() ) {
					ChatManager.sendMessage(
						MessageUtil.replaceParameters(
							ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getTamedMobDied().getMessage(),
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
	public static void handleAdvancementEvent( AdvancementEvent event ) {
		
		DisplayInfo displayInfo = event.getAdvancement().getDisplay();
		
		if( displayInfo != null && displayInfo.shouldAnnounceChat() &&
			ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerGotAdvancement().isEnabled() ) {
			ChatManager.sendMessage(
				MessageUtil.replaceParameters(
					ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerGotAdvancement().getMessage(),
					Map.of(
						"player", DiscordMessageBuilder.getEntityName( event.getEntity() ),
						"advancement_title", displayInfo.getTitle().getString(),
						"advancement_description", displayInfo.getDescription().getString(),
						"new_line", System.lineSeparator()
					)
				)
			);
		}
	}
}
