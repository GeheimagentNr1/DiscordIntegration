package de.geheimagentnr1.discordintegration.config;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import de.geheimagentnr1.discordintegration.util.VersionHelper;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractConfig;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;


@Log4j2
public class ServerConfig extends AbstractConfig {
	
	
	@NotNull
	private static final String ACTIVE_KEY = "active";
	
	@NotNull
	private static final String BOT_TOKEN_KEY = "bot_token";
	
	@NotNull
	private static final String CHANNEL_ID_KEY = "channel_id";
	
	@NotNull
	private static final String COMMAND_PREFIX_KEY = "command_prefix";
	
	@NotNull
	private static final String USE_NICKNAME_KEY = "use_nickname";
	
	@NotNull
	private static final String MAX_CHAR_COUNT_KEY = "max_char_count";
	
	@NotNull
	private static final String MESSAGES_KEY = "messages";
	
	public static final BotConfig BOT_CONFIG;
	@NotNull
	private static final String SERVER_STARTED_KEY = "server_started";
	
	@NotNull
	private static final List<String> SERVER_STARTED_ENABLED_KEY = List.of(
		MESSAGES_KEY,
		SERVER_STARTED_KEY,
		"enabled"
	);
	
	@NotNull
	private static final List<String> SERVER_STARTED_MESSAGE_KEY = List.of(
		MESSAGES_KEY,
		SERVER_STARTED_KEY,
		"message"
	);
	
	@NotNull
	private static final String SERVER_STOPPED_KEY = "server_stopped";
	
	@NotNull
	private static final List<String> SERVER_STOPPED_ENABLED_KEY = List.of(
		MESSAGES_KEY,
		SERVER_STOPPED_KEY,
		"enabled"
	);
	
	@NotNull
	private static final List<String> SERVER_STOPPED_MESSAGE_KEY = List.of(
		MESSAGES_KEY,
		SERVER_STOPPED_KEY,
		"message"
	);
	
	@NotNull
	private static final String SERVER_CRASHED_KEY = "server_crashed";
	
	@NotNull
	private static final List<String> SERVER_CRASHED_ENABLED_KEY = List.of(
		MESSAGES_KEY,
		SERVER_CRASHED_KEY,
		"enabled"
	);
	
	@NotNull
	private static final List<String> SERVER_CRASHED_MESSAGE_KEY = List.of(
		MESSAGES_KEY,
		SERVER_CRASHED_KEY,
		"message"
	);
	
	@NotNull
	private static final String PLAYER_JOINED_KEY = "player_joined";
	
	@NotNull
	private static final List<String> PLAYER_JOINED_ENABLED_KEY = List.of( MESSAGES_KEY, PLAYER_JOINED_KEY, "enabled"
	);
	
	@NotNull
	private static final List<String> PLAYER_JOINED_MESSAGE_KEY = List.of( MESSAGES_KEY, PLAYER_JOINED_KEY, "message"
	);
	
	@NotNull
	private static final String PLAYER_LEFT_KEY = "player_left";
	
	@NotNull
	private static final List<String> PLAYER_LEFT_ENABLED_KEY = List.of( MESSAGES_KEY, PLAYER_LEFT_KEY, "enabled" );
	
	@NotNull
	private static final List<String> PLAYER_LEFT_MESSAGE_KEY = List.of( MESSAGES_KEY, PLAYER_LEFT_KEY, "message" );
	
	@NotNull
	private static final String PLAYER_DIED_KEY = "player_died";
	
	@NotNull
	private static final List<String> PLAYER_DIED_ENABLED_KEY = List.of( MESSAGES_KEY, PLAYER_DIED_KEY, "enabled" );
	
	public static final ChatConfig CHAT_CONFIG;
	@NotNull
	private static final List<String> PLAYER_DIED_MESSAGE_KEY = List.of( MESSAGES_KEY, PLAYER_DIED_KEY, "message" );
	
	public static final ManagementConfig MANAGEMENT_CONFIG;
	@NotNull
	private static final String TAMED_MOB_DIED_KEY = "tamed_mob_died";
	
	public static final WhitelistConfig WHITELIST_CONFIG;
	@NotNull
	private static final List<String> TAMED_MOB_DIED_ENABLED_KEY = List.of(
		MESSAGES_KEY,
		TAMED_MOB_DIED_KEY,
		"enabled"
	);
	
	public static final CommandSettingsConfig COMMAND_SETTINGS_CONFIG;
	@NotNull
	private static final List<String> TAMED_MOB_DIED_MESSAGE_KEY = List.of(
		MESSAGES_KEY,
		TAMED_MOB_DIED_KEY,
		"message"
	);
	
	@NotNull
	private static final String PLAYER_GOT_ADVANCEMENT_KEY = "player_got_advancement";
	
	@NotNull
	private static final List<String> PLAYER_GOT_ADVANCEMENT_ENABLED_KEY = List.of(
		MESSAGES_KEY,
		PLAYER_GOT_ADVANCEMENT_KEY,
		"enabled"
	);
	
	@NotNull
	private static final List<String> PLAYER_GOT_ADVANCEMENT_MESSAGE_KEY = List.of(
		MESSAGES_KEY,
		PLAYER_GOT_ADVANCEMENT_KEY,
		"message"
	);
	
	@NotNull
	private static final String OTHER_BOTS_KEY = "other_bots";
	
	static {
		
		BOT_CONFIG = new BotConfig( BUILDER );
		CHAT_CONFIG = new ChatConfig( BUILDER );
		MANAGEMENT_CONFIG = new ManagementConfig( BUILDER );
		WHITELIST_CONFIG = new WhitelistConfig( BUILDER );
		COMMAND_SETTINGS_CONFIG = new CommandSettingsConfig( BUILDER );
		
		CONFIG = BUILDER.build();
	@NotNull
	private static final List<String> TRANSMIT_BOT_MESSAGES_KEY = List.of( OTHER_BOTS_KEY, "transmit_bot_messages" );
	
	@NotNull
	private static final List<String> OTHER_BOTS_COMMAND_PREFIXES_KEY = List.of(
		OTHER_BOTS_KEY,
		"other_bots_command_prefixes"
	);
	
	@NotNull
	private static final String COMMANDS_KEY = "commands";
	
	@NotNull
	private final DiscordNet discordNet;
	
	public ServerConfig( @NotNull AbstractMod _abstractMod, @NotNull DiscordNet _discordNet ) {
		
		super( _abstractMod );
		discordNet = _discordNet;
	}
	
	@NotNull
	@Override
	public ModConfig.Type type() {
		
		return ModConfig.Type.SERVER;
	}
	
	@Override
	public boolean isEarlyLoad() {
		
		return false;
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( "Should the Discord integration be active?", ACTIVE_KEY, false );
		registerConfigValue( "Token of your Discord bot:", BOT_TOKEN_KEY, "INSERT BOT TOKEN HERE" );
		registerConfigValue(
			"Channel ID where the bot will be working",
			CHANNEL_ID_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, Long.MAX_VALUE )
		);
		registerConfigValue( "Command prefix for Discord commands", COMMAND_PREFIX_KEY, "!" );
		registerConfigValue(
			"Shall the nickname of the Discord user be shown in the Minecraft chat as author name? " +
				"(If not, the username of the Discord user is shown as author name.)",
			USE_NICKNAME_KEY,
			true
		);
		registerConfigValue(
			"How long should Discord messages send to Minecraft Chat be at most? " +
				"If the value is -1, there is no limit to the length.",
			MAX_CHAR_COUNT_KEY,
			( builder, path ) -> builder.defineInRange( path, -1, -1, 2000 )
		);
		push( "Messages shown on Discord", MESSAGES_KEY );
		push( "Options for the server start message", SERVER_STARTED_KEY );
		registerConfigValue(
			"Should a message be sent to the Discord chat, if the server started?",
			SERVER_STARTED_ENABLED_KEY,
			true
		);
		registerConfigValue(
			"Message send to the Discord chat, if the Minecraft server started.",
			SERVER_STARTED_MESSAGE_KEY,
			"Server started"
		);
		pop();
		push( "Options for the server stop message", SERVER_STOPPED_KEY );
		registerConfigValue(
			"Should a message be sent to the Discord chat, if the server stopped?",
			SERVER_STOPPED_ENABLED_KEY,
			true
		);
		registerConfigValue(
			"Message send to the Discord chat, if the Minecraft server stopped.",
			SERVER_STOPPED_MESSAGE_KEY,
			"Server stopped"
		);
		pop();
		push( "Options for the server crash message", SERVER_CRASHED_KEY );
		registerConfigValue(
			"Should a message be sent to the Discord chat, if the server crashed?",
			SERVER_CRASHED_ENABLED_KEY,
			true
		);
		registerConfigValue(
			"Message send to the Discord chat, if the Minecraft server crashed.",
			SERVER_CRASHED_MESSAGE_KEY,
			"Server crashed"
		);
		pop();
		push( "Options for the player joined message", PLAYER_JOINED_KEY );
		registerConfigValue(
			"Should a message be sent to the Discord chat, if a player joined?",
			PLAYER_JOINED_ENABLED_KEY,
			true
		);
		registerConfigValue(
			"Message send to the Discord chat, if a player joined. (<player name> <message>)",
			PLAYER_JOINED_MESSAGE_KEY,
			"joined the game."
		);
		pop();
		push( "Options for the player left message", PLAYER_LEFT_KEY );
		registerConfigValue(
			"Should a message be sent to the Discord chat, if a player left?",
			PLAYER_LEFT_ENABLED_KEY,
			true
		);
		registerConfigValue(
			"Message send to the Discord chat, if a player left the server. (<player name> <message>)",
			PLAYER_LEFT_MESSAGE_KEY,
			"disconnected."
		);
		pop();
		push( "Options for the player died message", PLAYER_DIED_KEY );
		registerConfigValue(
			"Should a message be sent to the Discord chat, if a player died?",
			PLAYER_DIED_ENABLED_KEY,
			true
		);
		registerConfigValue(
			"Message send to the Discord chat, if a player died. (<player name> <message>) " +
				"If left empty, the default Minecraft message is send.",
			PLAYER_DIED_MESSAGE_KEY,
			""
		);
		pop();
		push( "Options for the tamed mob died message", TAMED_MOB_DIED_KEY );
		registerConfigValue(
			"Should a message be sent to the Discord chat, if a tamed mob left?",
			TAMED_MOB_DIED_ENABLED_KEY,
			true
		);
		registerConfigValue(
			"Message send to the Discord chat, if a tamed mob died. (<player name> <message>) " +
				"If left empty, the default Minecraft message is send.",
			TAMED_MOB_DIED_MESSAGE_KEY,
			""
		);
		pop();
		push( "Options for the player got advancement message", PLAYER_GOT_ADVANCEMENT_KEY );
		registerConfigValue(
			"Should a message be sent to the Discord chat, if a player got an advancement?",
			PLAYER_GOT_ADVANCEMENT_ENABLED_KEY,
			true
		);
		registerConfigValue(
			"Message send to the Discord chat, if a player got an advancement. (<player name> <message>) " +
				"**<advancement title>**<new line>*<advancement description>*",
			PLAYER_GOT_ADVANCEMENT_MESSAGE_KEY,
			"has made the advancement"
		);
		pop();
		pop();
		push( "Options how to deal with other bots", OTHER_BOTS_KEY );
		registerConfigValue(
			"Should messages of other bots be sent to the Minecraft chat?",
			TRANSMIT_BOT_MESSAGES_KEY,
			false
		);
		registerConfigValue(
			"Command prefixes of other bots. Messages with these prefixes are not sent to the Minecraft chat.",
			OTHER_BOTS_COMMAND_PREFIXES_KEY,
			new ArrayList<>()
		);
		pop();
		registerConfigValue(
			"Command mapping from Discord to Minecraft commands",
			COMMANDS_KEY,
			( builder, path ) -> builder.defineList( path, this::buildDefaultCommandList, CommandConfig::isCorrect )
		);
	}
	
	public static void handleConfigEvent() {
	private List<CommandConfig> buildDefaultCommandList() {
		
		ArrayList<CommandConfig> commands = new ArrayList<>();
		commands.add( new CommandConfig( "difficulty", "difficulty",
			false,
			true, "shows the difficulty of the server."
		) );
		commands.add( new CommandConfig( "gamerules", "discord gamerules",
			false,
			true, "shows the gamerules and their values."
		) );
		commands.add( new CommandConfig( "help", "discord commands",
			false,
			true, "shows all commands with their description."
		) );
		commands.add( new CommandConfig( "mods", "discord mods",
			false,
			true, "shows a list of the mods on the server."
		) );
		commands.add( new CommandConfig( "online", "list",
			false,
			true, "shows how many and which players are on the server."
		) );
		commands.add( new CommandConfig( "seed", "seed",
			false,
			true, "shows the seed of the active world."
		) );
		commands.add( new CommandConfig( "time", "time query daytime",
			false,
			true, "shows the current day time on the server."
		) );
		commands.add( new CommandConfig( "tps", "forge tps",
			false,
			true, "shows the tps statistic of the server and it's dimensions."
		) );
		if( VersionHelper.isDependecyWithVersionPresent( abstractMod.getModId(), "dimension_access_manager" ) ) {
			commands.add( new CommandConfig(
				"dimensions",
				"dimensions status",
				false,
				true,
				"shows the access states of all dimensions."
			) );
		}
		if( VersionHelper.isDependecyWithVersionPresent( abstractMod.getModId(), "moremobgriefingoptions" ) ) {
			commands.add( new CommandConfig(
				"mobgriefing",
				"mobgriefing list",
				false,
				true,
				"shows all mobgriefing options of the mobs."
			) );
		}
		return commands;
	}
	
	@Override
	protected void handleConfigChanging() {
		
		printConfig();
		DiscordManager.init();
		discordNet.init();
	}
	
	public boolean getActive() {
		
		log.info( "Loading \"{}\" Server Config", MOD_NAME );
		BOT_CONFIG.printConfig( log );
		CHAT_CONFIG.printConfig( log );
		MANAGEMENT_CONFIG.printConfig( log );
		WHITELIST_CONFIG.printConfig( log );
		COMMAND_SETTINGS_CONFIG.printConfig( log );
		return getValue( Boolean.class, ACTIVE_KEY );
	}
	
	@NotNull
	public String getBotToken() {
		
		return getValue( String.class, BOT_TOKEN_KEY );
	}
	
	public long getChannelId() {
		
		log.info( "\"{}\" Server Config loaded", MOD_NAME );
		return getValue( Long.class, CHANNEL_ID_KEY );
	}
	
	@NotNull
	public String getCommandPrefix() {
		
		return getValue( String.class, COMMAND_PREFIX_KEY );
	}
	
	public boolean isUseNickname() {
		
		return getValue( Boolean.class, USE_NICKNAME_KEY );
	}
	
	public int getMaxCharCount() {
		
		return getValue( Integer.class, MAX_CHAR_COUNT_KEY );
	}
	
	public boolean getServerStartedMessageEnabled() {
		
		return getValue( Boolean.class, SERVER_STARTED_ENABLED_KEY );
	}
	
	@NotNull
	public String getServerStartedMessage() {
		
		return getValue( String.class, SERVER_STARTED_MESSAGE_KEY );
	}
	
	public boolean getServerStoppedMessageEnabled() {
		
		return getValue( Boolean.class, SERVER_STOPPED_ENABLED_KEY );
	}
	
	@NotNull
	public String getServerStoppedMessage() {
		
		return getValue( String.class, SERVER_STOPPED_MESSAGE_KEY );
	}
	
	public boolean getServerCrashedMessageEnabled() {
		
		return getValue( Boolean.class, SERVER_CRASHED_ENABLED_KEY );
	}
	
	@NotNull
	public String getServerCrashedMessage() {
		
		return getValue( String.class, SERVER_CRASHED_MESSAGE_KEY );
	}
	
	public boolean getPlayerJoinedMessageEnabled() {
		
		return getValue( Boolean.class, PLAYER_JOINED_ENABLED_KEY );
	}
	
	@NotNull
	public String getPlayerJoinedMessage() {
		
		return getValue( String.class, PLAYER_JOINED_MESSAGE_KEY );
	}
	
	public boolean getPlayerLeftMessageEnabled() {
		
		return getValue( Boolean.class, PLAYER_LEFT_ENABLED_KEY );
	}
	
	@NotNull
	public String getPlayerLeftMessage() {
		
		return getValue( String.class, PLAYER_LEFT_MESSAGE_KEY );
	}
	
	public boolean getPlayerDiedMessageEnabled() {
		
		return getValue( Boolean.class, PLAYER_DIED_ENABLED_KEY );
	}
	
	@NotNull
	public String getPlayerDiedMessage() {
		
		return getValue( String.class, PLAYER_DIED_MESSAGE_KEY );
	}
	
	public boolean getTamedMobDiedMessageEnabled() {
		
		return getValue( Boolean.class, TAMED_MOB_DIED_ENABLED_KEY );
	}
	
	@NotNull
	public String getTamedMobDiedMessage() {
		
		return getValue( String.class, TAMED_MOB_DIED_MESSAGE_KEY );
	}
	
	public boolean getPlayerGotAdvancementMessageEnabled() {
		
		return getValue( Boolean.class, PLAYER_GOT_ADVANCEMENT_ENABLED_KEY );
	}
	
	@NotNull
	public String getPlayerGotAdvancementMessage() {
		
		return getValue( String.class, PLAYER_GOT_ADVANCEMENT_MESSAGE_KEY );
	}
	
	public boolean isTransmitBotMessages() {
		
		return getValue( Boolean.class, TRANSMIT_BOT_MESSAGES_KEY );
	}
	
	@NotNull
	public List<String> getOtherBotsCommandPrefixes() {
		
		return getListValue( String.class, OTHER_BOTS_COMMAND_PREFIXES_KEY );
	}
	
	@NotNull
	public List<? extends AbstractCommentedConfig> getCommands() {
		
		return getListValue( AbstractCommentedConfig.class, COMMANDS_KEY );
	}
}
