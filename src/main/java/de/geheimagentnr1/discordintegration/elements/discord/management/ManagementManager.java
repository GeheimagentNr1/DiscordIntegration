package de.geheimagentnr1.discordintegration.elements.discord.management;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.elements.discord.AbstractDiscordIntegrationServiceProvider;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings( { "SynchronizeOnThis", "NestedSynchronizedStatement" } )
@Log4j2
@RequiredArgsConstructor
public class ManagementManager extends AbstractDiscordIntegrationServiceProvider {
	
	
	@Getter
	@NotNull
	private final DiscordIntegration discordIntegration;
	
	@Nullable
	private TextChannel channel;
	
	public void init() {
		
		synchronized( DiscordManager.class ) {
			synchronized( ManagementManager.class ) {
				stop();
				if( shouldInitialize() ) {
					long channelId = serverConfig().getManagementConfig().getChannelId();
					JDA jda = discordManager().getJda();
					channel = jda.getTextChannelById( channelId );
					if( channel == null ) {
						log.error( "Discord Management Text Channel {} not found", channelId );
					}
				}
			}
		}
	}
	
	public synchronized void stop() {
		
		channel = null;
	}
	
	private boolean shouldInitialize() {
		
		return discordManager().isInitialized() && serverConfig().getManagementConfig().isEnabled();
	}
	
	private boolean isInitialized() {
		
		synchronized( DiscordManager.class ) {
			synchronized( ManagementManager.class ) {
				return shouldInitialize() && channel != null;
			}
		}
	}
	
	//package-private
	boolean isCorrectChannel( long channelId ) {
		
		return isInitialized() && serverConfig().getManagementConfig().getChannelId() == channelId;
	}
	
	public boolean hasManagementRole( @NotNull Member member ) {
		
		return discordManager().hasCorrectRole( member, serverConfig().getManagementConfig().getRoleId() );
	}
	
	public void sendMessage( @NotNull String message ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( ManagementManager.class ) {
				if( isInitialized() ) {
					discordMessageSender().sendMessage( channel, message );
				}
			}
		}
	}
	
	//package-private
	void sendFeedbackMessage( @NotNull String message ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( ManagementManager.class ) {
				if( isInitialized() ) {
					for( String messagePart : discordMessageBuilder().buildFeedbackMessage( message ) ) {
						discordMessageSender().sendMessage( channel, messagePart );
					}
				}
			}
		}
	}
}
