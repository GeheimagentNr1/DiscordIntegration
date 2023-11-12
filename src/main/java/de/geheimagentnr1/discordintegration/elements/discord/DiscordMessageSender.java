package de.geheimagentnr1.discordintegration.elements.discord;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;


@Log4j2
public class DiscordMessageSender {
	
	
	public void sendMessage( @NotNull TextChannel channel, @NotNull String message ) {
		
		try {
			for( int start = 0; start < message.length(); start += 2000 ) {
				channel.sendMessage( message.substring( start, Math.min( message.length(), start + 2000 ) ) ).queue();
			}
		} catch( Exception exception ) {
			log.error( "Message could not be send to channel {}", channel.getIdLong(), exception );
		}
	}
}
