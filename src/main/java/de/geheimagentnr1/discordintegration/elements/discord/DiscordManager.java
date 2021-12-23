package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatMessageEventHandler;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsEventHandler;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManagementMessageManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementMessageEventHandler;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;
import java.util.function.Consumer;


@Slf4j
public class DiscordManager {
	
	
	private static JDA jda;
	
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
				
				if( getGuild() == null ) {
					log.error( "The bot has no access to the guild {}", ServerConfig.BOT_CONFIG.getGuildId() );
					stop();
				} else {
					ChatManager.init();
					ManagementManager.init();
					LinkingsManagementMessageManager.init();
					LinkingsManager.updateWhitelist( new Consumer<Throwable>() {
						
						@Override
						public void accept( Throwable throwable ) {
							
							log.error( "Whitelist could not be updated on startup", throwable );
						}
					} );
				}
			} catch( Exception exception ) {
				log.error( "Login to Discord failed", exception );
			}
		}
	}
	
	public static synchronized void stop() {
		
		if( isInitialized() ) {
			ChatManager.stop();
			ManagementManager.stop();
			LinkingsManagementMessageManager.stop();
			jda.shutdown();
			jda = null;
		}
	}
	
	private static synchronized boolean shouldInitialize() {
		
		return ServerConfig.BOT_CONFIG.isActive();
	}
	
	public static synchronized boolean isInitialized() {
		
		return shouldInitialize() && jda != null;
	}
	
	public static synchronized JDA getJda() {
		
		return jda;
	}
	
	public static synchronized SelfUser getSelfUser() {
		
		return jda.getSelfUser();
	}
	
	public static synchronized Guild getGuild() {
		
		return jda.getGuildById( ServerConfig.BOT_CONFIG.getGuildId() );
	}
	
	public static boolean hasCorrectRole( Member member, long roleId ) {
		
		return member.getRoles().stream().anyMatch( role -> role.getIdLong() == roleId );
	}
	
	public static Member getMember( Long discordMemberId ) {
		
		return getGuild().getMemberById( discordMemberId );
	}
}
