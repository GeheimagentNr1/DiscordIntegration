package de.geheimagentnr1.discordintegration.api.events;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.jetbrains.annotations.NotNull;


public interface ForgeEventHandlerInterface {
	
	
	default void handleServerStartingEvent( @NotNull ServerStartingEvent event ) {
		
		//NOOP
	}
	
	default void handleServerStartedEvent( @NotNull ServerStartedEvent event ) {
		
		//NOOP
	}
	
	default void handleServerStoppedEvent( @NotNull ServerStoppedEvent event ) {
		
		//NOOP
	}
	
	default void handleRegisterCommandsEvent( @NotNull RegisterCommandsEvent event ) {
		
		//NOOP
	}
	
	default void handlePlayerLoggedInEvent( @NotNull PlayerEvent.PlayerLoggedInEvent event ) {
		
		//NOOP
	}
	
	default void handlePlayerLoggedOutEvent( @NotNull PlayerEvent.PlayerLoggedOutEvent event ) {
		
		//NOOP
	}
	
	default void handleServerChatEvent( @NotNull ServerChatEvent event ) {
		
		//NOOP
	}
	
	default void handleLivingDeathEvent( @NotNull LivingDeathEvent event ) {
		
		//NOOP
	}
	
	default void handleAdvancementEarnEvent( @NotNull AdvancementEvent.AdvancementEarnEvent event ) {
		
		//NOOP
	}
}
