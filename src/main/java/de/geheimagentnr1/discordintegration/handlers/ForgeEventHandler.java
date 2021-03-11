package de.geheimagentnr1.discordintegration.handlers;

import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;


@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER )
public class ForgeEventHandler {
	
	
	@SubscribeEvent
	public static void handleServerStarting( FMLServerStartingEvent event ) {
		
		DiscordEventHandler.setServer( event.getServer() );
		CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
		DiscordCommand.register( dispatcher );
		SayToDiscordCommand.register( dispatcher );
		MeToDiscordCommand.register( dispatcher );
	}
	
	@SubscribeEvent
	public static void handleServerStarted( FMLServerStartedEvent event ) {
		
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
			DiscordNet.sendPlayerMessage(
				event.getPlayer(),
				String.format(
					"has made the advancement **%s**%n*%s*",
					displayInfo.getTitle().getString(),
					displayInfo.getDescription().getString()
				)
			);
		}
	}
}
