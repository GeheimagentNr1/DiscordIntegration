package de.geheimagentnr1.discordintegration.elements.discord.management;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageSender;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.MinecraftGameProfile;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;


@Slf4j
public class ManagementManager {
	
	
	private static TextChannel channel;
	
	public static synchronized void init() {
		
		stop();
		if( shouldInitialize() ) {
			long channelId = ServerConfig.MANAGEMENT_CONFIG.getChannelId();
			JDA jda = DiscordManager.getJda();
			channel = jda.getTextChannelById( channelId );
			if( channel == null ) {
				log.error( "Discord Management Text Channel {} not found", channelId );
			}
		}
	}
	
	public static synchronized void stop() {
		
		channel = null;
	}
	
	private static synchronized boolean shouldInitialize() {
		
		return DiscordManager.isInitialized() && ServerConfig.MANAGEMENT_CONFIG.isEnabled();
	}
	
	private static synchronized boolean isInitialized() {
		
		return shouldInitialize() && channel != null;
	}
	
	//package-private
	static boolean isCorrectChannel( long channelId ) {
		
		return isInitialized() && ServerConfig.MANAGEMENT_CONFIG.getChannelId() == channelId;
	}
	
	public static boolean hasManagementRole( Member member ) {
		
		return DiscordManager.hasCorrectRole( member, ServerConfig.MANAGEMENT_CONFIG.getRoleId() );
	}
	
	public static void sendWhitelistMessage( MinecraftGameProfile minecraftGameProfile, String message ) {
		
		sendMessage( DiscordMessageBuilder.buildPlayerMessage( minecraftGameProfile.getName(), message ) );
	}
	
	private static synchronized void sendMessage( String message ) {
		
		if( isInitialized() ) {
			DiscordMessageSender.sendMessage( channel, message );
		}
	}
	
	//package-private
	static synchronized void sendFeedbackMessage( String message ) {
		
		if( isInitialized() ) {
			for( String messagePart : DiscordMessageBuilder.buildFeedbackMessage( message ) ) {
				DiscordMessageSender.sendMessage( channel, messagePart );
			}
		}
	}
	
	public static void sendLinkingMessage( String discordName, String minecraftName, String message ) {
		
		if( isInitialized() ) {
			DiscordMessageSender.sendMessage(
				channel,
				String.format( "**%s** %s **%s**", minecraftName, message, discordName )
			);
		}
	}
}
