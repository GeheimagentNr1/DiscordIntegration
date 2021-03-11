package de.geheimagentnr1.discordintegration.net;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordEventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;


public class DiscordNet {
	
	
	private static final Logger LOGGER = LogManager.getLogger( DiscordNet.class );
	
	public static final String FEEDBACK_START = "**```";
	
	public static final String FEEDBACK_END = "```**";
	
	private static JDA jda;
	
	private static TextChannel channel;
	
	
	public static synchronized void init() {
		
		stop();
		if( ServerConfig.getActive() ) {
			try {
				jda = new JDABuilder( ServerConfig.getBotToken() )
						.addEventListeners( new DiscordEventHandler() )
						.setAutoReconnect( true )
						.build();
				jda.awaitReady();
				channel = jda.getTextChannelById( ServerConfig.getChannelId() );
				if( channel == null ) {
					LOGGER.error( "Discord Text Channel {} not found", ServerConfig.getChannelId() );
				}
			} catch( LoginException | InterruptedException exception ) {
				LOGGER.error( "Login to Discord failed", exception );
			}
		}
	}
	
	public static synchronized void stop() {
		
		if( isJdaInitialized() ) {
			jda.shutdown();
			channel = null;
			jda = null;
		}
	}
	
	private static synchronized boolean isJdaInitialized() {
		
		return jda != null;
	}
	
	public static synchronized boolean isInitialized() {
		
		return isJdaInitialized() && channel != null;
	}
	
	public static synchronized boolean feedBackAllowed( TextChannel _channel, User author ) {
		
		return _channel.getIdLong() == ServerConfig.getChannelId() &&
			_channel.getIdLong() == channel.getIdLong() &&
			author.getIdLong() != jda.getSelfUser().getIdLong();
	}
	
	public static void sendPlayerMessage( PlayerEntity player, String message ) {
		
		sendMessage( String.format( "**%s** %s", getPlayerName( player ), message ) );
	}
	
	public static void sendChatMessage( PlayerEntity player, String message ) {
		
		sendChatMessage( getPlayerName( player ), message );
	}
	
	private static String getPlayerName( PlayerEntity player ) {
		
		return player.getDisplayName().getString();
	}
	
	public static void sendChatMessage( CommandSource source, ITextComponent message ) {
		
		sendCommandChatMessage( source, message.getString() );
	}
	
	public static void sendMeChatMessage( CommandSource source, String action ) {
		
		sendCommandChatMessage( source, String.format( "*%s*", action ) );
	}
	
	private static void sendCommandChatMessage( CommandSource source, String message ) {
		
		sendChatMessage( source.getDisplayName().getString(), message );
	}
	
	private static void sendChatMessage( String name, String message ) {
		
		sendMessage( String.format( "**[%s]** %s", name, message ) );
	}
	
	public static void sendFeedbackMessage( String message ) {
		
		for( int start = 0; start <= message.length(); start += 1990 ) {
			sendMessage(
				FEEDBACK_START + message.substring( start, Math.min( message.length(), start + 1990 ) ) + FEEDBACK_END
			);
		}
	}
	
	public static synchronized void sendMessage( String message ) {
		
		if( isInitialized() ) {
			try {
				for( int start = 0; start < message.length(); start += 2000 ) {
					channel.sendMessage(
						message.substring( start, Math.min( message.length(), start + 2000 ) )
					).queue();
				}
			} catch( Exception exception ) {
				LOGGER.error( "Message could not be send", exception );
			}
		}
	}
}
