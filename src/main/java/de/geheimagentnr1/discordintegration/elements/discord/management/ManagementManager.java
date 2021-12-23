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
import net.minecraft.world.entity.player.Player;


@Slf4j
public class ManagementManager {
	
	
	private static TextChannel channel;
	
	public static void init() {
		
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
	
	public static void stop() {
		
		channel = null;
	}
	
	private static boolean shouldInitialize() {
		
		return DiscordManager.isInitialized() && ServerConfig.MANAGEMENT_CONFIG.isEnabled();
	}
	
	private static boolean isInitialized() {
		
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
	
	public static void sendPlayerMessage( Player player, String message ) {
		
		sendMessage( DiscordMessageBuilder.buildPlayerMessage( player, message ) );
	}
	
	public static void sendMessage( String message ) {
		
		if( isInitialized() ) {
			DiscordMessageSender.sendMessage( channel, message );
		}
	}
	
	//package-private
	static void sendFeedbackMessage( String message ) {
		
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
