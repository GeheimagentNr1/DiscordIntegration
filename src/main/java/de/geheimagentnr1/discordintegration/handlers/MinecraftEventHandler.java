package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.commands.minecraft.MeCommandToDiscord;
import de.geheimagentnr1.discordintegration.commands.minecraft.SayCommandToDiscord;
import de.geheimagentnr1.discordintegration.config.ModConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;


@SuppressWarnings( "unused" )
@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE )
public class MinecraftEventHandler {
	
	
	@SubscribeEvent
	public static void handleServerStarting( FMLServerStartingEvent event ) {
		
		SayCommandToDiscord.register( event.getCommandDispatcher() );
		MeCommandToDiscord.register( event.getCommandDispatcher() );
	}
	
	@SubscribeEvent
	public static void handleServerStarted( FMLServerStartedEvent event ) {
		
		ModConfig.load();
		DiscordNet.init();
		DiscordEventHandler.setServer( event.getServer() );
		DiscordNet.sendMessage( "Server started" );
	}
	
	
	@SubscribeEvent
	public static void handleServerStoppedEvent( FMLServerStoppedEvent event ) {
		
		if( event.getServer().isServerRunning() ) {
			DiscordNet.sendMessage( "Server crashed" );
		} else {
			DiscordNet.sendMessage( "Server stopped" );
		}
		DiscordNet.stop();
	}
	
	@SubscribeEvent
	public static void handlePlayerLoggedInEvent( PlayerEvent.PlayerLoggedInEvent event ) {
		
		DiscordNet.sendPlayerMessage( event.getPlayer(), "joined the game." );
	}
	
	
	@SubscribeEvent
	public static void handlePlayerLoggedOutEvent( PlayerEvent.PlayerLoggedOutEvent event ) {
		
		DiscordNet.sendPlayerMessage( event.getPlayer(), "disconnected." );
	}
	
	@SubscribeEvent
	public static void handleServerChatEvent( ServerChatEvent event ) {
		
		DiscordNet.sendChatMessage( event.getPlayer(), event.getMessage() );
	}
	
	@SubscribeEvent
	public static void handleLivingEntityDeathEvent( LivingDeathEvent event ) {
		
		LivingEntity entity = event.getEntityLiving();
		
		if( entity instanceof PlayerEntity || entity instanceof TameableEntity &&
			( (TameableEntity)entity ).getOwnerId() != null ) {
			String name = entity.getDisplayName().getString();
			DiscordNet.sendMessage( event.getSource().getDeathMessage( entity ).getString()
				.replaceFirst( name, "**" + name + "**" ) );
		}
	}
	
	@SubscribeEvent
	public static void handleAdvancementEvent( AdvancementEvent event ) {
		
		DisplayInfo displayInfo = event.getAdvancement().getDisplay();
		
		if( displayInfo != null && displayInfo.shouldAnnounceToChat() ) {
			//noinspection HardcodedLineSeparator
			String message = "has made the advancement **" + displayInfo.getTitle().getString() +
				"**\n*" + displayInfo.getDescription().getString() + "*";
			DiscordNet.sendPlayerMessage( event.getPlayer(), message );
		}
	}
}
