package de.geheimagentnr1.discordintegration.net;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordEventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.List;


public class DiscordNet {
	
	
	private static final Logger LOGGER = LogManager.getLogger( DiscordNet.class );
	
	public static final String FEEDBACK_START = "**```";
	
	public static final String FEEDBACK_END = "```**";
	
	private static JDA jda;
	
	private static TextChannel channel;
	
	private static final List<GatewayIntent> INTENTS = Collections.singletonList( GatewayIntent.GUILD_MESSAGES );
	
	public static synchronized void init() {
		
		stop();
		if( ServerConfig.getActive() ) {
			try {
				jda = JDABuilder.create( ServerConfig.getBotToken(), INTENTS )
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
		
		return _channel.getIdLong() == ServerConfig.getChannelId() && _channel.getIdLong() == channel.getIdLong() &&
			author.getIdLong() != jda.getSelfUser().getIdLong();
	}
	
	public static void sendDeathMessage( LivingDeathEvent event, String customMessage ) {
		
		LivingEntity entity = event.getEntity();
		String name = entity.getDisplayName().getString();
		if( customMessage.isEmpty() ) {
			sendMessage(
				event.getSource()
					.getLocalizedDeathMessage( entity )
					.getString()
					.replace( name, "**" + name + "**" )
			);
		} else {
			sendMessage( String.format( "**%s** %s", entity.getDisplayName().getString(), customMessage ) );
		}
	}
	
	public static void sendPlayerMessage( Player player, String message ) {
		
		sendMessage( String.format( "**%s** %s", getPlayerName( player ), message ) );
	}
	
	public static void sendChatMessage( Player player, String message ) {
		
		sendChatMessage( getPlayerName( player ), message );
	}
	
	private static String getPlayerName( Player player ) {
		
		return player == null ? "Server" : player.getDisplayName().getString();
	}
	
	public static void sendChatMessage( CommandSourceStack source, Component message ) {
		
		sendCommandChatMessage( source, message.getString() );
	}
	
	public static void sendMeChatMessage( CommandSourceStack source, String action ) {
		
		sendCommandChatMessage( source, String.format( "*%s*", action ) );
	}
	
	private static void sendCommandChatMessage( CommandSourceStack source, String message ) {
		
		sendChatMessage( source.getDisplayName().getString(), message );
	}
	
	private static void sendChatMessage( String name, String message ) {
		
		sendMessage( String.format( "**[%s]** %s", name, message ) );
	}
	
	public static void sendFeedbackMessage( String message ) {
		
		for( int start = 0; start <= message.length(); start += 1990 ) {
			sendMessage( FEEDBACK_START + message.substring( start, Math.min( message.length(), start + 1990 ) ) +
				FEEDBACK_END );
		}
	}
	
	public static synchronized void sendMessage( String message ) {
		
		if( isInitialized() ) {
			try {
				for( int start = 0; start < message.length(); start += 2000 ) {
					channel.sendMessage( message.substring( start, Math.min( message.length(), start + 2000 ) ) )
						.queue();
				}
			} catch( Exception exception ) {
				LOGGER.error( "Message could not be send", exception );
			}
		}
	}
}
