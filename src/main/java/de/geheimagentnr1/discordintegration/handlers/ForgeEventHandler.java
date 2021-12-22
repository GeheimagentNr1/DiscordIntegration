package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.commands.DiscordCommand;
import de.geheimagentnr1.discordintegration.elements.commands.MeToDiscordCommand;
import de.geheimagentnr1.discordintegration.elements.commands.SayToDiscordCommand;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordEventHandler;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
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
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(
	modid = DiscordIntegration.MODID,
	bus = Mod.EventBusSubscriber.Bus.FORGE,
	value = Dist.DEDICATED_SERVER
)
public class ForgeEventHandler {
	
	
	@SubscribeEvent
	public static void handleServerStartingEvent( ServerStartingEvent event ) {
		
		DiscordEventHandler.setServer( event.getServer() );
	}
	
	@SubscribeEvent
	public static void handlerRegisterCommandsEvent( RegisterCommandsEvent event ) {
		
		DiscordCommand.register( event.getDispatcher() );
		MeToDiscordCommand.register( event.getDispatcher() );
		SayToDiscordCommand.register( event.getDispatcher() );
	}
	
	@SubscribeEvent
	public static void handleServerStartedEvent( ServerStartedEvent event ) {
		
		if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerStarted().isEnabled() ) {
			DiscordNet.sendMessage( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerStarted().getMessage() );
		}
	}
	
	@SubscribeEvent
	public static void handleServerStoppedEvent( ServerStoppedEvent event ) {
		
		if( event.getServer().isRunning() ) {
			if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerCrashed().isEnabled() ) {
				DiscordNet.sendMessage( ServerConfig.CHAT_CONFIG.getChatMessagesConfig()
					.getServerCrashed()
					.getMessage() );
			}
		} else {
			if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getServerStopped().isEnabled() ) {
				DiscordNet.sendMessage( ServerConfig.CHAT_CONFIG.getChatMessagesConfig()
					.getServerStopped()
					.getMessage() );
			}
		}
		DiscordNet.stop();
	}
	
	@SubscribeEvent
	public static void handlePlayerLoggedInEvent( PlayerEvent.PlayerLoggedInEvent event ) {
		
		if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerJoined().isEnabled() ) {
			DiscordNet.sendPlayerMessage(
				event.getPlayer(),
				ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerJoined().getMessage()
			);
		}
	}
	
	@SubscribeEvent
	public static void handlePlayerLoggedOutEvent( PlayerEvent.PlayerLoggedOutEvent event ) {
		
		if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerLeft().isEnabled() ) {
			DiscordNet.sendPlayerMessage(
				event.getPlayer(),
				ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerLeft().getMessage()
			);
		}
	}
	
	@SubscribeEvent
	public static void handleServerChatEvent( ServerChatEvent event ) {
		
		if( !event.isCanceled() ) {
			DiscordNet.sendChatMessage( event.getPlayer(), event.getMessage() );
		}
	}
	
	@SubscribeEvent
	public static void handleLivingDeathEvent( LivingDeathEvent event ) {
		
		LivingEntity entity = event.getEntityLiving();
		
		if( entity instanceof Player ) {
			if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerDied().isEnabled() ) {
				DiscordNet.sendDeathMessage(
					event,
					ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerDied().getMessage()
				);
			}
		} else {
			if( entity instanceof TamableAnimal && ( (TamableAnimal)entity ).getOwnerUUID() != null ) {
				if( ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getTamedMobDied().isEnabled() ) {
					DiscordNet.sendDeathMessage(
						event,
						ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getTamedMobDied().getMessage()
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
			DiscordNet.sendPlayerMessage(
				event.getPlayer(),
				String.format(
					"%s **%s**%n*%s*",
					ServerConfig.CHAT_CONFIG.getChatMessagesConfig().getPlayerGotAdvancement().getMessage(),
					displayInfo.getTitle().getString(),
					displayInfo.getDescription().getString()
				)
			);
		}
	}
}
