package de.geheimagentnr1.discordintegration.elements.discord.commands.models;

import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


@RequiredArgsConstructor
public class DiscordCommandSource implements CommandSource {
	
	
	@NotNull
	private final Consumer<String> feedbackSender;
	
	private boolean messageNotSent = true;
	
	@NotNull
	private String message = "";
	
	public void sendMessage() {
		
		if( messageNotSent && !message.isEmpty() ) {
			messageNotSent = false;
			feedbackSender.accept( message );
		}
	}
	
	@Override
	public void sendSystemMessage( @NotNull Component component ) {
		
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
