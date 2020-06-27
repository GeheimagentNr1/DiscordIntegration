package de.geheimagentnr1.discordintegration.commands.discord;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.UUID;


public class DiscordCommandSource implements ICommandSource {
	
	
	@Override
	public void sendMessage( ITextComponent component, @Nonnull UUID uuid ) {
		
		DiscordNet.sendMessage( component.getString() );
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
