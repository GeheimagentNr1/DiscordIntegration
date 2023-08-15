package de.geheimagentnr1.discordintegration.elements.discord.chat;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageSender;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;


@SuppressWarnings( { "SynchronizeOnThis", "NestedSynchronizedStatement" } )
@Log4j2
public class ChatManager {
	
	
	private static TextChannel channel;
	
	public static void init() {
		
		synchronized( DiscordManager.class ) {
			synchronized( ChatManager.class ) {
				stop();
				if( shouldInitialize() ) {
					long channelId = ServerConfig.CHAT_CONFIG.getChannelId();
					JDA jda = DiscordManager.getJda();
					channel = jda.getTextChannelById( channelId );
					if( channel == null ) {
						log.error( "Discord Chat Text Channel {} not found", channelId );
					}
				}
			}
		}
	}
	
	public static synchronized void stop() {
		
		channel = null;
	}
	
	private static boolean shouldInitialize() {
		
		return DiscordManager.isInitialized() && ServerConfig.CHAT_CONFIG.isEnabled();
	}
	
	private static boolean isInitialized() {
		
		synchronized( DiscordManager.class ) {
			synchronized( ChatManager.class ) {
				return shouldInitialize() && channel != null;
			}
		}
	}
	
	//package-private
	static boolean isCorrectChannel( long channelId ) {
		
		return isInitialized() && ServerConfig.CHAT_CONFIG.getChannelId() == channelId;
	}
	
	public static void sendMeChatMessage( CommandSourceStack source, String action ) {
		
		sendMessage( DiscordMessageBuilder.buildMeChatMessage( source, action ) );
	}
	
	public static void sendChatMessage( CommandSourceStack source, Component message ) {
		
		if( !ServerConfig.CHAT_CONFIG.suppressServerMessages() ||
			!"Server".equals( source.getTextName() ) ||
			source.getEntity() != null ) {
			sendMessage( DiscordMessageBuilder.buildChatMessage( source, message ) );
		}
	}
	
	public static void sendChatMessage( ServerPlayer player, String message ) {
		
		sendMessage( DiscordMessageBuilder.buildChatMessage( player, message ) );
	}
	
	public static void sendMessage( String message ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( ChatManager.class ) {
				if( isInitialized() ) {
					DiscordMessageSender.sendMessage( channel, message );
				}
			}
		}
	}
	
	//package-private
	static void sendFeedbackMessage( String message ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( ChatManager.class ) {
				if( isInitialized() ) {
					for( String messagePart : DiscordMessageBuilder.buildFeedbackMessage( message ) ) {
						DiscordMessageSender.sendMessage( channel, messagePart );
					}
				}
			}
		}
	}
}
