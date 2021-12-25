package de.geheimagentnr1.discordintegration.elements.discord.management;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageSender;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.MinecraftGameProfile;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.world.entity.player.Player;


@SuppressWarnings( { "SynchronizeOnThis", "NestedSynchronizedStatement" } )
@Log4j2
public class ManagementManager {
	
	
	private static TextChannel channel;
	
	public static void init() {
		
		synchronized( DiscordManager.class ) {
			synchronized( ManagementManager.class ) {
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
		}
	}
	
	public static synchronized void stop() {
		
		channel = null;
	}
	
	private static boolean shouldInitialize() {
		
		return DiscordManager.isInitialized() && ServerConfig.MANAGEMENT_CONFIG.isEnabled();
	}
	
	private static boolean isInitialized() {
		
		synchronized( DiscordManager.class ) {
			synchronized( ManagementManager.class ) {
				return shouldInitialize() && channel != null;
			}
		}
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
	
	public static void sendLinkingMessage( String discordName, String minecraftName, String message ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( ManagementManager.class ) {
				if( isInitialized() ) {
					DiscordMessageSender.sendMessage(
						channel,
						String.format( "**%s** %s **%s**", minecraftName, message, discordName )
					);
				}
			}
		}
	}
	
	public static void sendPlayerMessage( Player player, String message ) {
		
		sendMessage( DiscordMessageBuilder.buildPlayerMessage( player, message ) );
	}
	
	public static void sendMessage( String message ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( ManagementManager.class ) {
				if( isInitialized() ) {
					DiscordMessageSender.sendMessage( channel, message );
				}
			}
		}
	}
	
	//package-private
	static void sendFeedbackMessage( String message ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( ManagementManager.class ) {
				if( isInitialized() ) {
					for( String messagePart : DiscordMessageBuilder.buildFeedbackMessage( message ) ) {
						DiscordMessageSender.sendMessage( channel, messagePart );
					}
				}
			}
		}
	}
}
