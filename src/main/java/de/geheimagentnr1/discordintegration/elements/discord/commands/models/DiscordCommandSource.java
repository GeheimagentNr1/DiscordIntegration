package de.geheimagentnr1.discordintegration.elements.discord.commands.models;

import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class DiscordCommandSource implements CommandSource {
	
	
	private final Consumer<String> feedbackSender;
	
	private boolean messageNotSent = true;
	
	private String message = "";
	
	public void sendMessage() {
		
		if( messageNotSent && !message.isEmpty() ) {
			messageNotSent = false;
			feedbackSender.accept( message );
		}
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
