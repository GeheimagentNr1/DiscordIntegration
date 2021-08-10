package de.geheimagentnr1.discordintegration.handlers;

import com.mojang.brigadier.CommandDispatcher;
import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.commands.DiscordCommand;
import de.geheimagentnr1.discordintegration.elements.commands.MeToDiscordCommand;
import de.geheimagentnr1.discordintegration.elements.commands.SayToDiscordCommand;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordEventHandler;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;


@Mod.EventBusSubscriber(
	modid = DiscordIntegration.MODID,
	bus = Mod.EventBusSubscriber.Bus.FORGE,
	value = Dist.DEDICATED_SERVER
)
public class ForgeEventHandler {
	
	
	@SubscribeEvent
	public static void handleServerStartingEvent( FMLServerStartingEvent event ) {
		
		DiscordEventHandler.setServer( event.getServer() );
	}
	
	@SubscribeEvent
	public static void handlerRegisterCommandsEvent( RegisterCommandsEvent event ) {
		
		CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
		DiscordCommand.register( dispatcher );
		MeToDiscordCommand.register( dispatcher );
		SayToDiscordCommand.register( dispatcher );
	}
	
	@SubscribeEvent
	public static void handleServerStartedEvent( FMLServerStartedEvent event ) {
		
		if( ServerConfig.getServerStartedMessageEnabled() ) {
			DiscordNet.sendMessage( ServerConfig.getServerStartedMessage() );
		}
	}
	
	@SubscribeEvent
	public static void handleServerStoppedEvent( FMLServerStoppedEvent event ) {
		
		if( event.getServer().isRunning() ) {
			if( ServerConfig.getServerCrashedMessageEnabled() ) {
				DiscordNet.sendMessage( ServerConfig.getServerCrashedMessage() );
			}
		} else {
			if( ServerConfig.getServerStoppedMessageEnabled() ) {
				DiscordNet.sendMessage( ServerConfig.getServerStoppedMessage() );
			}
		}
		DiscordNet.stop();
	}
	
	@SubscribeEvent
	public static void handlePlayerLoggedInEvent( PlayerEvent.PlayerLoggedInEvent event ) {
		
		if( ServerConfig.getPlayerJoinedMessageEnabled() ) {
			DiscordNet.sendPlayerMessage( event.getPlayer(), ServerConfig.getPlayerJoinedMessage() );
		}
	}
	
	@SubscribeEvent
	public static void handlePlayerLoggedOutEvent( PlayerEvent.PlayerLoggedOutEvent event ) {
		
		if( ServerConfig.getPlayerLeftMessageEnabled() ) {
			DiscordNet.sendPlayerMessage( event.getPlayer(), ServerConfig.getPlayerLeftMessage() );
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
		
		if( entity instanceof PlayerEntity ) {
			if( ServerConfig.getPlayerDiedMessageEnabled() ) {
				DiscordNet.sendDeathMessage( event, ServerConfig.getPlayerDiedMessage() );
			}
		} else {
			if( entity instanceof TameableEntity && ( (TameableEntity)entity ).getOwnerUUID() != null ) {
				if( ServerConfig.getTamedMobDiedMessageEnabled() ) {
					DiscordNet.sendDeathMessage( event, ServerConfig.getTamedMobDiedMessage() );
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void handleAdvancementEvent( AdvancementEvent event ) {
		
		DisplayInfo displayInfo = event.getAdvancement().getDisplay();
		
		if( displayInfo != null && displayInfo.shouldAnnounceChat() &&
			ServerConfig.getPlayerGotAdvancementMessageEnabled() ) {
			DiscordNet.sendPlayerMessage(
				event.getPlayer(),
				String.format(
					"%s **%s**%n*%s*",
					ServerConfig.getPlayerGotAdvancementMessage(),
					displayInfo.getTitle().getString(),
					displayInfo.getDescription().getString()
				)
			);
		}
	}
}
