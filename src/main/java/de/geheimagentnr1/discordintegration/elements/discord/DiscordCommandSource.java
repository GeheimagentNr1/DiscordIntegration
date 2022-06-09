package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;


//package-private
class DiscordCommandSource implements CommandSource {
	
	
	private String message = "";
	
	//package-private
	void sendMessage() {
		
		DiscordNet.sendFeedbackMessage( message );
	}
	
	@Override
	public void sendSystemMessage( @Nonnull Component component ) {
		
		message += String.format( "%s%n", component.getString() );
	}
	
	@Override
	public boolean acceptsSuccess() {
		
		return true;
	}
	
	@Override
	public boolean acceptsFailure() {
		
		return true;
	}
	
	@Override
	public boolean shouldInformAdmins() {
		
		return false;
	}
}
