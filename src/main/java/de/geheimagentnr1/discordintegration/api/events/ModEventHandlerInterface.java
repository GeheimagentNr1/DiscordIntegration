package de.geheimagentnr1.discordintegration.api.events;

import net.neoforged.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;


public interface ModEventHandlerInterface {
	
	
	default void handleModConfigLoadingEvent( @NotNull ModConfigEvent.Loading event ) {
		
		//NOOP
	}
	
	default void handleModConfigReloadingEvent( @NotNull ModConfigEvent.Reloading event ) {
		
		//NOOP
	}
}
