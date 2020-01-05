package de.geheimagentnr1.discordintegration.commands.discord;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.text.ITextComponent;


public class DiscordCommandSource implements ICommandSource {
	
	
	/**
	 * Send a chat message to the CommandSender
	 */
	@Override
	public void sendMessage( ITextComponent component ) {
		
		DiscordNet.sendMessage( component.getFormattedText() );
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
