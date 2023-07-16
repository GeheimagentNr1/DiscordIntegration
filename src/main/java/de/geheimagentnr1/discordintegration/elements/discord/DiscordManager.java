package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatMessageEventHandler;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsEventHandler;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManagementMessageManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementMessageEventHandler;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


@Log4j2
public class DiscordManager {
	
	
	private static boolean serverStarted = false;
	
	private static JDA jda;
	
	private static Guild guild;
	
	private static final List<GatewayIntent> INTENTS = List.of(
		GatewayIntent.GUILD_MESSAGES,
		GatewayIntent.GUILD_MEMBERS,
		GatewayIntent.GUILD_MESSAGE_REACTIONS
	);
	
	public static synchronized void init() {
		
		stop();
		if( shouldInitialize() ) {
			try {
				jda = JDABuilder.create( ServerConfig.BOT_CONFIG.getBotToken(), INTENTS )
					.addEventListeners( new ChatMessageEventHandler() )
					.addEventListeners( new ManagementMessageEventHandler() )
					.addEventListeners( new LinkingsEventHandler() )
					.setAutoReconnect( true )
					.build();
				jda.awaitReady();
				
				guild = jda.getGuildById( ServerConfig.BOT_CONFIG.getGuildId() );
				if( guild == null ) {
					log.error( "The bot has no access to the guild {}", ServerConfig.BOT_CONFIG.getGuildId() );
					stop();
				} else {
					ChatManager.init();
					ManagementManager.init();
					LinkingsManagementMessageManager.init();
					if( serverStarted ) {
						updateWhitelist();
					}
				}
				updatePresence( ServerLifecycleHooks.getCurrentServer().getPlayerCount() );
			} catch( Exception exception ) {
				log.error( "Login to Discord failed", exception );
			}
		}
	}
	
	public static synchronized void stop() {
		
		if( isInitialized() ) {
			jda.shutdown();
			jda = null;
			guild = null;
			ChatManager.stop();
			ManagementManager.stop();
			LinkingsManagementMessageManager.stop();
		}
	}
	
	private static boolean shouldInitialize() {
		
		return ServerConfig.BOT_CONFIG.isActive();
	}
	
	public static synchronized boolean isInitialized() {
		
		return shouldInitialize() &&
			jda != null &&
			jda.getStatus() != JDA.Status.SHUTTING_DOWN &&
			jda.getStatus() != JDA.Status.SHUTDOWN &&
			guild != null;
	}
	
	public static synchronized JDA getJda() {
		
		return jda;
	}
	
	public static synchronized void setServerStarted() {
		
		if( !serverStarted ) {
			serverStarted = true;
			updateWhitelist();
		}
	}
	
	public static void updatePresence( int playerCount ) {
		
		jda.getPresence().setPresence(
			Activity.playing( String.format(
				"Minecraft with %d players",//TODO
				playerCount
			) ),
			false
		);
	}
	
	private static void updateWhitelist() {
		
		if( LinkingsManager.isEnabled() ) {
			Consumer<Throwable> errorHandler = throwable ->
				log.error( "Whitelist could not be updated on startup", throwable );
			
			try {
				log.info( "Check Discord whitelist on startup" );
				LinkingsManager.updateWhitelist( errorHandler, true );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	public static synchronized SelfUser getSelfUser() {
		
		return jda.getSelfUser();
	}
	
	public static synchronized Member getMember( Long discordMemberId ) {
		
		if( isInitialized() ) {
			return guild.getMemberById( discordMemberId );
		} else {
			return null;
		}
	}
	
	public static boolean hasCorrectRole( Member member, long roleId ) {
		
		return member.getRoles().stream().anyMatch( role -> role.getIdLong() == roleId );
	}
	
	public static String getNameFromMember( Member member ) {
		
		return member.getUser().getAsTag();
	}
}
