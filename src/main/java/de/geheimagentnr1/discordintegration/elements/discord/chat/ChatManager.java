package de.geheimagentnr1.discordintegration.elements.discord.chat;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.elements.discord.AbstractDiscordIntegrationServiceProvider;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings( { "SynchronizeOnThis", "NestedSynchronizedStatement" } )
@Log4j2
@RequiredArgsConstructor
public class ChatManager extends AbstractDiscordIntegrationServiceProvider {
	
	
	@Getter( AccessLevel.PROTECTED )
	@NotNull
	private final DiscordIntegration discordIntegration;
	
	@Nullable
	private TextChannel channel;
	
	public void init() {
		
		synchronized( DiscordManager.class ) {
			synchronized( ChatManager.class ) {
				stop();
				if( shouldInitialize() ) {
					long channelId = serverConfig().getChatConfig().getChannelId();
					JDA jda = discordManager().getJda();
					channel = jda.getTextChannelById( channelId );
					if( channel == null ) {
						log.error( "Discord Chat Text Channel {} not found", channelId );
					}
				}
			}
		}
	}
	
	public synchronized void stop() {
		
		channel = null;
	}
	
	private boolean shouldInitialize() {
		
		return discordManager().isInitialized() && serverConfig().getChatConfig().isEnabled();
	}
	
	private boolean isInitialized() {
		
		synchronized( DiscordManager.class ) {
			synchronized( ChatManager.class ) {
				return shouldInitialize() && channel != null;
			}
		}
	}
	
	//package-private
	boolean isCorrectChannel( long channelId ) {
		
		return isInitialized() && serverConfig().getChatConfig().getChannelId() == channelId;
	}
	
	public void sendEmoteChatMessage( CommandSourceStack source, Component action ) {
		
		sendMessage( discordMessageBuilder().buildMeChatMessage( source, action.getString() ) );
	}
	
	public void sendChatMessage( CommandSourceStack source, Component message ) {
		
		if( !serverConfig().getChatConfig().suppressServerMessages() ||
			!"Server".equals( source.getTextName() ) ||
			source.getEntity() != null ) {
			sendMessage( discordMessageBuilder().buildChatMessage( source, message ) );
		}
	}
	
	public void sendChatMessage( ServerPlayer player, String message ) {
		
		sendMessage( discordMessageBuilder().buildChatMessage( player, message ) );
	}
	
	public void sendMessage( String message ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( ChatManager.class ) {
				if( isInitialized() ) {
					discordMessageSender().sendMessage( channel, message );
				}
			}
		}
	}
	
	//package-private
	void sendFeedbackMessage( String message ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( ChatManager.class ) {
				if( isInitialized() ) {
					for( String messagePart : discordMessageBuilder().buildFeedbackMessage( message ) ) {
						discordMessageSender().sendMessage( channel, messagePart );
					}
				}
			}
		}
	}
}
