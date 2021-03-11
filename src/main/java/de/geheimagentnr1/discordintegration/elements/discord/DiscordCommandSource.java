package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.UUID;


//package-private
class DiscordCommandSource implements ICommandSource {
	
	
	private String message = "";
	
	//package-private
	void sendMessage() {
		
		DiscordNet.sendFeedbackMessage( message );
	}
	
	@Override
	public void sendMessage( ITextComponent component, @Nonnull UUID uuid ) {
		
		message += String.format( "%s%n", component.getString() );
	}
	
	@Override
	public boolean shouldReceiveFeedback() {
		
		return true;
	}
	
	@Override
	public boolean shouldReceiveErrors() {
		
		return true;
	}
	
	@Override
	public boolean allowLogging() {
		
		return false;
	}
}
