package de.geheimagentnr1.discordintegration.net;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordEventHandler;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.events.ForgeEventHandlerInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.List;


@Log4j2
@RequiredArgsConstructor
public class DiscordNet implements ForgeEventHandlerInterface {
	
	
	@NotNull
	public static final String FEEDBACK_START = "**```";
	
	@NotNull
	public static final String FEEDBACK_END = "```**";
	
	@NotNull
	private final List<GatewayIntent> INTENTS = Collections.singletonList( GatewayIntent.GUILD_MESSAGES );
	
	@NotNull
	private final AbstractMod abstractMod;
	
	private ServerConfig serverConfig;
	
	private DiscordEventHandler discordEventHandler;
	
	private JDA jda;
	
	private TextChannel channel;
	
	@NotNull
	private ServerConfig serverConfig() {
		
		if( serverConfig == null ) {
			serverConfig = abstractMod.getConfig( ModConfig.Type.SERVER, ServerConfig.class )
				.orElseThrow( () -> new IllegalStateException( "DiscordIntegration ServerConfig could not be found" ) );
		}
		return serverConfig;
	}
	
	public synchronized void init() {
		
		stop();
		if( serverConfig().getActive() ) {
			try {
				discordEventHandler = new DiscordEventHandler( abstractMod, serverConfig(), this );
				jda = JDABuilder.create( serverConfig().getBotToken(), INTENTS )
					.addEventListeners( discordEventHandler )
					.setAutoReconnect( true )
					.build();
				jda.awaitReady();
				channel = jda.getTextChannelById( serverConfig().getChannelId() );
				if( channel == null ) {
					log.error( "Discord Text Channel {} not found", serverConfig().getChannelId() );
				}
			} catch( LoginException | InterruptedException exception ) {
				log.error( "Login to Discord failed", exception );
			}
		}
	}
	
	private synchronized void stop() {
		
		if( isJdaInitialized() ) {
			jda.shutdown();
			channel = null;
			jda = null;
		}
	}
	
	private synchronized boolean isJdaInitialized() {
		
		return jda != null;
	}
	
	public synchronized boolean isInitialized() {
		
		return isJdaInitialized() && channel != null;
	}
	
	public synchronized boolean feedBackAllowed( @NotNull TextChannel _channel, @NotNull User author ) {
		
		return _channel.getIdLong() == serverConfig().getChannelId() && _channel.getIdLong() == channel.getIdLong() &&
			author.getIdLong() != jda.getSelfUser().getIdLong();
	}
	
	public void sendDeathMessage( @NotNull LivingDeathEvent event, @NotNull String customMessage ) {
		
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
	
	public void sendPlayerMessage( @NotNull Player player, @NotNull String message ) {
		
		sendMessage( String.format( "**%s** %s", getPlayerName( player ), message ) );
	}
	
	public void sendChatMessage( @NotNull Player player, @NotNull String message ) {
		
		sendChatMessage( getPlayerName( player ), message );
	}
	
	@NotNull
	private String getPlayerName( @Nullable Player player ) {
		
		return player == null ? "Server" : player.getDisplayName().getString();
	}
	
	public void sendChatMessage( @NotNull CommandSourceStack source, @NotNull Component message ) {
		
		sendCommandChatMessage( source, message.getString() );
	}
	
	public void sendEmoteChatMessage( @NotNull CommandSourceStack source, @NotNull Component action ) {
		
		sendCommandChatMessage( source, String.format( "*%s*", action.getString() ) );
	}
	
	private void sendCommandChatMessage( @NotNull CommandSourceStack source, @NotNull String message ) {
		
		sendChatMessage( source.getDisplayName().getString(), message );
	}
	
	private void sendChatMessage( @NotNull String name, @NotNull String message ) {
		
		sendMessage( String.format( "**[%s]** %s", name, message ) );
	}
	
	public void sendFeedbackMessage( @NotNull String message ) {
		
		for( int start = 0; start <= message.length(); start += 1990 ) {
			sendMessage( FEEDBACK_START + message.substring( start, Math.min( message.length(), start + 1990 ) ) +
				FEEDBACK_END );
		}
	}
	
	public synchronized void sendMessage( @NotNull String message ) {
		
		if( isInitialized() ) {
			try {
				for( int start = 0; start < message.length(); start += 2000 ) {
					channel.sendMessage( message.substring( start, Math.min( message.length(), start + 2000 ) ) )
						.queue();
				}
			} catch( Exception exception ) {
				log.error( "Message could not be send", exception );
			}
		}
	}
	
	@SubscribeEvent
	@Override
	public synchronized void handleServerStartingEvent( @NotNull ServerStartingEvent event ) {
		
		discordEventHandler.setServer( event.getServer() );
	}
	
	@SubscribeEvent( priority = EventPriority.LOWEST )
	@Override
	public void handleServerStoppedEvent( @NotNull ServerStoppedEvent event ) {
		
		stop();
	}
}
