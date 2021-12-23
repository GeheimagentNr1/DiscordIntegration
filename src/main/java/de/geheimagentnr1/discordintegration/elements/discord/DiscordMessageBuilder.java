package de.geheimagentnr1.discordintegration.elements.discord;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.List;


public class DiscordMessageBuilder {
	
	
	private static final String FEEDBACK_START = "**```";
	
	private static final String FEEDBACK_END = "```**";
	
	public static boolean isMessageBotFeedback( String message ) {
		
		return message.startsWith( FEEDBACK_START ) || message.endsWith( FEEDBACK_END );
	}
	
	private static String getPlayerName( Player player ) {
		
		return player.getDisplayName().getString();
	}
	
	public static String buildDeathMessage( LivingDeathEvent event, String customMessage ) {
		
		LivingEntity entity = event.getEntityLiving();
		String name = entity.getDisplayName().getString();
		if( customMessage.isEmpty() ) {
			return event.getSource()
				.getLocalizedDeathMessage( entity )
				.getString()
				.replace( name, "**" + name + "**" );
		} else {
			return String.format( "**%s** %s", name, customMessage );
		}
	}
	
	public static String buildPlayerMessage( Player player, String message ) {
		
		return buildPlayerMessage( getPlayerName( player ), message );
	}
	
	public static String buildPlayerMessage( String playerName, String message ) {
		
		return String.format( "**%s** %s", playerName, message );
	}
	
	public static String buildMeChatMessage( CommandSourceStack source, String action ) {
		
		return buildChatMessage( source, String.format( "*%s*", action ) );
	}
	
	public static String buildChatMessage( Player player, String message ) {
		
		return buildChatMessage( getPlayerName( player ), message );
	}
	
	public static String buildChatMessage( CommandSourceStack source, Component message ) {
		
		return buildChatMessage( source, message.getString() );
	}
	
	private static String buildChatMessage( CommandSourceStack source, String message ) {
		
		return buildChatMessage( source.getDisplayName().getString(), message );
	}
	
	private static String buildChatMessage( String name, String message ) {
		
		return String.format( "**[%s]** %s", name, message );
	}
	
	public static List<String> buildFeedbackMessage( String message ) {
		
		List<String> messages = new ArrayList<>();
		for( int start = 0; start <= message.length(); start += 1990 ) {
			messages.add( FEEDBACK_START + message.substring( start, Math.min( message.length(), start + 1990 ) ) +
				FEEDBACK_END );
		}
		return messages;
	}
}
