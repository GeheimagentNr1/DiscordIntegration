package de.geheimagentnr1.discordintegration.net;

import de.geheimagentnr1.discordintegration.config.MainConfig;
import de.geheimagentnr1.discordintegration.handlers.DiscordEventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;


public class DiscordNet {
	
	
	private static boolean on = false;
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static JDA jda;
	
	private static TextChannel channel;
	
	public static void init() {
		
		if( MainConfig.getActive() ) {
			try {
				jda = new JDABuilder( MainConfig.getBotToken() ).addEventListeners( new DiscordEventHandler() )
					.build();
				jda.setAutoReconnect( true );
				jda.awaitReady();
				channel = jda.getTextChannelById( MainConfig.getChannelID() );
				if( channel == null ) {
					LOGGER.error( "Discord Text Channel {} not found", MainConfig.getChannelID() );
					on = false;
				} else {
					on = true;
				}
			} catch( LoginException | InterruptedException exception ) {
				LOGGER.error( "Login to Discord failed", exception );
				on = false;
			}
		} else {
			stop();
		}
	}
	
	public static void stop() {
		
		if( on && jda != null ) {
			on = false;
			jda.shutdown();
			channel = null;
			jda = null;
		}
	}
	
	public static void sendChatMessage( PlayerEntity player, String message ) {
		
		sendMessage( buildChatMessage( getPlayerName( player ), message ) );
	}
	
	public static void sendChatMessage( CommandSource source, ITextComponent message ) {
		
		sendMessage( buildChatMessage( getCommandSourceName( source ), message.getString() ) );
	}
	
	public static void sendMeChatMessage( CommandSource source, String action ) {
		
		sendMessage( buildChatMessage( getCommandSourceName( source ), "*" + action + "*" ) );
	}
	
	private static String buildChatMessage( String name, String message ) {
		
		return "**[" + name + "]** " + message;
	}
	
	public static void sendPlayerMessage( PlayerEntity player, String message ) {
		
		sendMessage( "**" + getPlayerName( player ) + "** " + message );
	}
	
	public static void sendMessage( String message ) {
		
		if( on ) {
			try {
				channel.sendMessage( message ).queue();
			} catch( IllegalStateException exception ) {
				LOGGER.error( "Message could not be send", exception );
			}
		}
	}
	
	private static String getPlayerName( PlayerEntity player ) {
		
		return player.getDisplayName().getString();
	}
	
	private static String getCommandSourceName( CommandSource source ) {
		
		return source.getDisplayName().getString();
	}
	
	public static TextChannel getChannel() {
		
		return channel;
	}
}
