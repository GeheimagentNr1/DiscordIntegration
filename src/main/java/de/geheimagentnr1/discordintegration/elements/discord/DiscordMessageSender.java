package de.geheimagentnr1.discordintegration.elements.discord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;


@Slf4j
public class DiscordMessageSender {
	
	
	public static void sendMessage( TextChannel channel, String message ) {
		
		try {
			for( int start = 0; start < message.length(); start += 2000 ) {
				channel.sendMessage( message.substring( start, Math.min( message.length(), start + 2000 ) ) )
					.queue();
			}
		} catch( Exception exception ) {
			log.error( "Message could not be send to channel {}", channel.getIdLong(), exception );
		}
	}
}
