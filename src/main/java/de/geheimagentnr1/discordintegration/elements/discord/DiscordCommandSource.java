package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;


//package-private
@RequiredArgsConstructor
class DiscordCommandSource implements CommandSource {
	
	
	@NotNull
	private final DiscordNet discordNet;
	
	@NotNull
	private String message = "";
	
	//package-private
	void sendMessage() {
		
		discordNet.sendFeedbackMessage( message );
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
