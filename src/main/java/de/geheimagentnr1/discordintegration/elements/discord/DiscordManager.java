package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.config.WhitelistConfig;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatMessageEventHandler;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsEventHandler;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementMessageEventHandler;
import de.geheimagentnr1.discordintegration.api.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@SuppressWarnings( "SynchronizeOnThis" )
@Log4j2
@RequiredArgsConstructor
public class DiscordManager extends AbstractDiscordIntegrationServiceProvider {
	
	
	@Getter( AccessLevel.PROTECTED )
	@NotNull
	private final DiscordIntegration discordIntegration;
	
	private boolean serverStarted = false;
	
	private JDA jda;
	
	private Guild guild;
	
	@NotNull
	private static final List<GatewayIntent> INTENTS = List.of(
		GatewayIntent.GUILD_MESSAGES,
		GatewayIntent.GUILD_MEMBERS,
		GatewayIntent.GUILD_MESSAGE_REACTIONS,
		GatewayIntent.MESSAGE_CONTENT
	);
	
	public void init() {
		
		synchronized( DiscordManager.class ) {
			stop();
			if( shouldInitialize() ) {
				try {
					jda = JDABuilder.create( serverConfig().getBotConfig().getBotToken(), INTENTS )
						.addEventListeners( new ChatMessageEventHandler(
							serverConfig(),
							this,
							chatManager(),
							discordCommandHandler(),
							discordMessageBuilder()
						) )
						.addEventListeners( new ManagementMessageEventHandler(
							managementManager(),
							discordCommandHandler()
						) )
						.addEventListeners( new LinkingsEventHandler(
							this,
							linkingsManagementMessageManager(),
							linkingsManager()
						) )
						.setAutoReconnect( true )
						.build();
					jda.awaitReady();
					
					guild = jda.getGuildById( serverConfig().getBotConfig().getGuildId() );
					if( guild == null ) {
						log.error(
							"The bot has no access to the guild {}",
							serverConfig().getBotConfig().getGuildId()
						);
						stop();
					} else {
						checkLinkingManagementRole();
						chatManager().init();
						managementManager().init();
						linkingsManagementMessageManager().init();
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
	}
	
	private void checkLinkingManagementRole() {
		
		WhitelistConfig whitelistConfig = serverConfig().getWhitelistConfig();
		
		if( !whitelistConfig.isEnabled() || !whitelistConfig.useSingleLinkingManagement() ) {
			return;
		}
		long roleId = whitelistConfig.getSingleLinkingManagementRoleId();
		
		if( roleId == guild.getIdLong() ) {
			log.warn(
				"whitelist.single_linking_management_role_id is the @everyone role: " +
					"every member of the Discord server can activate or deactivate all linkings"
			);
		} else if( whitelistConfig.useRole() && roleId == whitelistConfig.getRoleId() ) {
			log.warn(
				"whitelist.single_linking_management_role_id is the same role as whitelist.role_id: " +
					"every member, who can be whitelisted, can activate or deactivate all linkings"
			);
		} else if( guild.getRoleById( roleId ) == null ) {
			log.warn(
				"whitelist.single_linking_management_role_id {} is no role of the Discord server: " +
					"nobody can activate or deactivate linkings",
				roleId
			);
		}
	}
	
	public void stop() {
		
		synchronized( DiscordManager.class ) {
			if( isInitialized() ) {
				jda.shutdown();
				// Wait for JDA: NeoForge 21.9+ closes the mod class loader after the server stopped, a JDA thread that
				// is still shutting down then fails to load its classes and keeps the JVM alive.
				try {
					if( !jda.awaitShutdown( Duration.ofSeconds( 10 ) ) ) {
						jda.shutdownNow();
						jda.awaitShutdown( Duration.ofSeconds( 5 ) );
					}
				} catch( InterruptedException exception ) {
					Thread.currentThread().interrupt();
					log.error( "Interrupted while waiting for Discord to shut down", exception );
				}
				jda = null;
				guild = null;
				chatManager().stop();
				managementManager().stop();
				linkingsManagementMessageManager().stop();
			}
		}
	}
	
	private boolean shouldInitialize() {
		
		return serverConfig().getBotConfig().isActive();
	}
	
	public boolean isInitialized() {
		
		synchronized( DiscordManager.class ) {
			return shouldInitialize() &&
				jda != null &&
				jda.getStatus() != JDA.Status.SHUTTING_DOWN &&
				jda.getStatus() != JDA.Status.SHUTDOWN &&
				guild != null;
		}
	}
	
	public JDA getJda() {
		
		synchronized( DiscordManager.class ) {
			return jda;
		}
	}
	
	public void setServerStarted() {
		
		synchronized( DiscordManager.class ) {
			if( !serverStarted ) {
				serverStarted = true;
				updateWhitelist();
			}
		}
	}
	
	public void updatePresence( int onlinePlayerCount ) {
		
		synchronized( DiscordManager.class ) {
			if( isInitialized() ) {
				if( serverConfig().getBotConfig().getDiscordPresenceConfig().isShow() ) {
					jda.getPresence().setPresence(
						Activity.playing(
							MessageUtil.replaceParameters(
								serverConfig().getBotConfig().getDiscordPresenceConfig().getMessage(),
								Map.of(
									"online_player_count",
									String.valueOf( onlinePlayerCount ),
									"max_player_count",
									String.valueOf( ServerLifecycleHooks.getCurrentServer().getMaxPlayers() )
								)
							)
						),
						false
					);
				} else {
					jda.getPresence().setPresence( (Activity)null, false );
				}
			}
		}
	}
	
	private void updateWhitelist() {
		
		if( linkingsManager().isEnabled() ) {
			Consumer<Throwable> errorHandler = throwable ->
				log.error( "Whitelist could not be updated on startup", throwable );
			
			try {
				log.info( "Check Discord whitelist on startup" );
				linkingsManager().updateWhitelist(
					errorHandler,
					serverConfig().getWhitelistConfig().useSingleLinkingManagement()
				);
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@NotNull
	public SelfUser getSelfUser() {
		
		synchronized( DiscordManager.class ) {
			return jda.getSelfUser();
		}
	}
	
	@Nullable
	public Member getMember( @NotNull Long discordMemberId ) {
		
		synchronized( DiscordManager.class ) {
			if( isInitialized() ) {
				return guild.getMemberById( discordMemberId );
			} else {
				return null;
			}
		}
	}
	
	public boolean hasCorrectRole( @NotNull Member member, long roleId ) {
		
		return member.getRoles().stream().anyMatch( role -> role.getIdLong() == roleId );
	}
	
	@NotNull
	public String getMemberAsTag( @NotNull Member member ) {
		
		return member.getUser().getAsTag();
	}
}
