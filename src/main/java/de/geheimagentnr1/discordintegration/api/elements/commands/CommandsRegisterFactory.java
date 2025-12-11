package de.geheimagentnr1.discordintegration.api.elements.commands;

import de.geheimagentnr1.discordintegration.api.events.ForgeEventHandlerInterface;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public abstract class CommandsRegisterFactory implements ForgeEventHandlerInterface {
	
	
	@Override
	public void handleRegisterCommandsEvent( @NotNull RegisterCommandsEvent event ) {
		
		commands().forEach( commandInterface -> commandInterface.register( event.getDispatcher() ) );
	}
	
	@NotNull
	protected abstract List<CommandInterface> commands();
}
