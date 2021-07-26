package de.geheimagentnr1.discordintegration.config;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import de.geheimagentnr1.discordintegration.util.VersionHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class ServerConfig {
	
	
	private static final Logger LOGGER = LogManager.getLogger( ServerConfig.class );
	
	private static final String MOD_NAME = ModLoadingContext.get().getActiveContainer().getModInfo().getDisplayName();
	
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	
	public static final ForgeConfigSpec CONFIG;
	
	private static final ForgeConfigSpec.BooleanValue ACTIVE;
	
	private static final ForgeConfigSpec.ConfigValue<String> BOT_TOKEN;
	
	private static final ForgeConfigSpec.LongValue CHANNEL_ID;
	
	private static final ForgeConfigSpec.ConfigValue<String> COMMAND_PREFIX;
	
	private static final ForgeConfigSpec.IntValue MAX_CHAR_COUNT;
	
	private static final ForgeConfigSpec.BooleanValue SERVER_STARTED_MESSAGE_ENABLED;
	
	private static final ForgeConfigSpec.ConfigValue<String> SERVER_STARTED_MESSAGE;
	
	private static final ForgeConfigSpec.BooleanValue SERVER_STOPPED_MESSAGE_ENABLED;
	
	private static final ForgeConfigSpec.ConfigValue<String> SERVER_STOPPED_MESSAGE;
	
	private static final ForgeConfigSpec.BooleanValue SERVER_CRASHED_MESSAGE_ENABLED;
	
	private static final ForgeConfigSpec.ConfigValue<String> SERVER_CRASHED_MESSAGE;
	
	private static final ForgeConfigSpec.BooleanValue PLAYER_JOINED_MESSAGE_ENABLED;
	
	private static final ForgeConfigSpec.ConfigValue<String> PLAYER_JOINED_MESSAGE;
	
	private static final ForgeConfigSpec.BooleanValue PLAYER_LEFT_MESSAGE_ENABLED;
	
	private static final ForgeConfigSpec.ConfigValue<String> PLAYER_LEFT_MESSAGE;
	
	private static final ForgeConfigSpec.BooleanValue PLAYER_DIED_MESSAGE_ENABLED;
	
	private static final ForgeConfigSpec.ConfigValue<String> PLAYER_DIED_MESSAGE;
	
	private static final ForgeConfigSpec.BooleanValue TAMED_MOB_DIED_MESSAGE_ENABLED;
	
	private static final ForgeConfigSpec.ConfigValue<String> TAMED_MOB_DIED_MESSAGE;
	
	private static final ForgeConfigSpec.BooleanValue PLAYER_GOT_ADVANCEMENT_MESSAGE_ENABLED;
	
	private static final ForgeConfigSpec.ConfigValue<String> PLAYER_GOT_ADVANCEMENT_MESSAGE;
	
	private static final ForgeConfigSpec.BooleanValue TRANSMIT_BOT_MESSAGES;
	
	private static final ForgeConfigSpec.ConfigValue<List<String>> OTHER_BOTS_COMMAND_PREFIXES;
	
	private static final ForgeConfigSpec.ConfigValue<List<? extends CommandConfig>> COMMANDS;
	
	static {
		
		ACTIVE = BUILDER.comment( "Should the Discord integration be active?" )
			.define( "active", false );
		BOT_TOKEN = BUILDER.comment( "Token of your Discord bot:" )
			.define( "bot_token", "INSERT BOT TOKEN HERE" );
		CHANNEL_ID = BUILDER.comment( "Channel ID where the bot will be working" )
			.defineInRange( "channel_id", 0, 0, Long.MAX_VALUE );
		COMMAND_PREFIX = BUILDER.comment( "Command prefix for Discord commands" )
			.define( "command_prefix", "!" );
		MAX_CHAR_COUNT = BUILDER.comment( "How long should Discord messages send to Minecraft Chat be at most? " +
			"If the value is -1, there is no limit to the length." )
			.defineInRange( "max_char_count", -1, -1, 2000 );
		BUILDER.comment( "Messages shown on Discord" )
			.push( "messages" );
		BUILDER.comment( "Options for the server start message" )
			.push( "server_started" );
		SERVER_STARTED_MESSAGE_ENABLED = BUILDER.comment(
			"Should a message be sent to the Discord chat, if the server started?" )
			.define( "enabled", true );
		SERVER_STARTED_MESSAGE = BUILDER.comment( "Message send to the Discord chat, if the Minecraft server started" +
			"." )
			.define( "message", "Server started" );
		BUILDER.pop();
		BUILDER.comment( "Options for the server stop message" )
			.push( "server_stopped" );
		SERVER_STOPPED_MESSAGE_ENABLED = BUILDER.comment(
			"Should a message be sent to the Discord chat, if the server stopped?" )
			.define( "enabled", true );
		SERVER_STOPPED_MESSAGE = BUILDER.comment( "Message send to the Discord chat, if the Minecraft server stopped" +
			"." )
			.define( "message", "Server stopped" );
		BUILDER.pop();
		BUILDER.comment( "Options for the server crash message" )
			.push( "server_crashed" );
		SERVER_CRASHED_MESSAGE_ENABLED = BUILDER.comment(
			"Should a message be sent to the Discord chat, if the server crashed?" )
			.define( "enabled", true );
		SERVER_CRASHED_MESSAGE = BUILDER.comment( "Message send to the Discord chat, if the Minecraft server crashed" +
			"." )
			.define( "message", "Server crashed" );
		BUILDER.pop();
		BUILDER.comment( "Options for the player joined message" )
			.push( "player_joined" );
		PLAYER_JOINED_MESSAGE_ENABLED = BUILDER.comment(
			"Should a message be sent to the Discord chat, if a player joined?" )
			.define( "enabled", true );
		PLAYER_JOINED_MESSAGE = BUILDER.comment(
			"Message send to the Discord chat, if a player joined. (<player name> <message>)" )
			.define( "message", "joined the game." );
		BUILDER.pop();
		BUILDER.comment( "Options for the player left message" )
			.push( "player_left" );
		PLAYER_LEFT_MESSAGE_ENABLED =
			BUILDER.comment( "Should a message be sent to the Discord chat, if a player left?" )
				.define( "enabled", true );
		PLAYER_LEFT_MESSAGE = BUILDER.comment(
			"Message send to the Discord chat, if a player left the server. (<player name> <message>)" )
			.define( "message", "disconnected." );
		BUILDER.pop();
		BUILDER.comment( "Options for the player died message" )
			.push( "player_died" );
		PLAYER_DIED_MESSAGE_ENABLED =
			BUILDER.comment( "Should a message be sent to the Discord chat, if a player died?" )
				.define( "enabled", true );
		PLAYER_DIED_MESSAGE = BUILDER.comment(
			"Message send to the Discord chat, if a player died. (<player name> <message>) If left empty, the default " +
				"Minecraft message is send." )
			.define( "message", "" );
		BUILDER.pop();
		BUILDER.comment( "Options for the tamed mob died message" )
			.push( "tamed_mob_died" );
		TAMED_MOB_DIED_MESSAGE_ENABLED = BUILDER.comment(
			"Should a message be sent to the Discord chat, if a tamed mob left?" )
			.define( "enabled", true );
		TAMED_MOB_DIED_MESSAGE = BUILDER.comment(
			"Message send to the Discord chat, if a tamed mob died. (<player name> <message>) If left empty, the " +
				"default Minecraft message is send." )
			.define( "message", "" );
		BUILDER.pop();
		BUILDER.comment( "Options for the player got advancement message" )
			.push( "player_got_advancement" );
		PLAYER_GOT_ADVANCEMENT_MESSAGE_ENABLED = BUILDER.comment(
			"Should a message be sent to the Discord chat, if a player got an advancement?" )
			.define( "enabled", true );
		PLAYER_GOT_ADVANCEMENT_MESSAGE = BUILDER.comment(
			"Message send to the Discord chat, if a player got an advancement. (<player name> <message>) " +
				"**<advancement title>**<new line>*<advancement description>*" )
			.define( "message", "has made the advancement" );
		BUILDER.pop();
		BUILDER.pop();
		BUILDER.comment( "Options how to deal with other bots" )
			.push( "other_bots" );
		TRANSMIT_BOT_MESSAGES = BUILDER.comment( "Should messages of other bots be sent to the Minecraft chat?" )
			.define( "transmit_bot_messages", false );
		OTHER_BOTS_COMMAND_PREFIXES = BUILDER.comment( "Command prefixes of other bots. " +
			"Messages with these prefixes are not sent to the Minecraft chat." )
			.define( "other_bots_command_prefixes", new ArrayList<>() );
		BUILDER.pop();
		COMMANDS = BUILDER.comment( "Command mapping from Discord to Minecraft commands" )
			.defineList( "commands", ServerConfig::buildDefaultCommandList, CommandConfig::isCorrect );
		
		CONFIG = BUILDER.build();
	}
	
	private static List<CommandConfig> buildDefaultCommandList() {
		
		ArrayList<CommandConfig> commands = new ArrayList<>();
		commands.add( new CommandConfig(
			"difficulty",
			"difficulty",
			true,
			"shows the difficulty of the server."
		) );
		commands.add( new CommandConfig(
			"gamerules",
			"discord gamerules",
			true,
			"shows the gamerules and their values."
		) );
		commands.add( new CommandConfig(
			"help",
			"discord commands",
			true,
			"shows all commands with their description."
		) );
		commands.add( new CommandConfig(
			"mods",
			"discord mods",
			true,
			"shows a list of the mods on the server."
		) );
		commands.add( new CommandConfig(
			"online",
			"list",
			true,
			"shows how many and which players are on the server."
		) );
		commands.add( new CommandConfig(
			"seed",
			"seed",
			true,
			"shows the seed of the active world."
		) );
		commands.add( new CommandConfig(
			"time",
			"time query daytime",
			true,
			"shows the current day time on the server."
		) );
		commands.add( new CommandConfig(
			"tps",
			"forge tps",
			true,
			"shows the tps statistic of the server and it's dimensions."
		) );
		if( VersionHelper.isDependecyWithVersionPresent( "dimension_access_manager" ) ) {
			commands.add( new CommandConfig(
				"dimensions",
				"dimensions status",
				true,
				"shows the access states of all dimensions."
			) );
		}
		if( VersionHelper.isDependecyWithVersionPresent( "moremobgriefingoptions" ) ) {
			commands.add( new CommandConfig(
				"mobgriefing",
				"mobgriefing list",
				true,
				"shows all mobgriefing options of the mobs."
			) );
		}
		return commands;
	}
	
	public static void handleConfigEvent() {
		
		printConfig();
		DiscordNet.init();
	}
	
	private static void printConfig() {
		
		LOGGER.info( "Loading \"{}\" Server Config", MOD_NAME );
		LOGGER.info( "{} = {}", ACTIVE.getPath(), ACTIVE.get() );
		LOGGER.info( "{} = {}", BOT_TOKEN.getPath(), BOT_TOKEN.get() );
		LOGGER.info( "{} = {}", CHANNEL_ID.getPath(), CHANNEL_ID.get() );
		LOGGER.info( "{} = {}", COMMAND_PREFIX.getPath(), COMMAND_PREFIX.get() );
		LOGGER.info( "{} = {}", TRANSMIT_BOT_MESSAGES.getPath(), TRANSMIT_BOT_MESSAGES.get() );
		LOGGER.info( "{} = {}", MAX_CHAR_COUNT.getPath(), MAX_CHAR_COUNT.get() );
		LOGGER.info( "{} = {}", COMMANDS.getPath(), COMMANDS.get() );
		LOGGER.info( "\"{}\" Server Config loaded", MOD_NAME );
	}
	
	public static boolean getActive() {
		
		return ACTIVE.get();
	}
	
	public static String getBotToken() {
		
		return BOT_TOKEN.get();
	}
	
	public static long getChannelId() {
		
		return CHANNEL_ID.get();
	}
	
	public static String getCommandPrefix() {
		
		return COMMAND_PREFIX.get();
	}
	
	public static boolean isTransmitBotMessages() {
		
		return TRANSMIT_BOT_MESSAGES.get();
	}
	
	public static List<String> getOtherBotsCommandPrefixes() {
		
		return OTHER_BOTS_COMMAND_PREFIXES.get();
	}
	
	public static int getMaxCharCount() {
		
		return MAX_CHAR_COUNT.get();
	}
	
	public static boolean getServerStartedMessageEnabled() {
		
		return SERVER_STARTED_MESSAGE_ENABLED.get();
	}
	
	public static String getServerStartedMessage() {
		
		return SERVER_STARTED_MESSAGE.get();
	}
	
	public static boolean getServerStoppedMessageEnabled() {
		
		return SERVER_STOPPED_MESSAGE_ENABLED.get();
	}
	
	public static String getServerStoppedMessage() {
		
		return SERVER_STOPPED_MESSAGE.get();
	}
	
	public static boolean getServerCrashedMessageEnabled() {
		
		return SERVER_CRASHED_MESSAGE_ENABLED.get();
	}
	
	public static String getServerCrashedMessage() {
		
		return SERVER_CRASHED_MESSAGE.get();
	}
	
	public static boolean getPlayerJoinedMessageEnabled() {
		
		return PLAYER_JOINED_MESSAGE_ENABLED.get();
	}
	
	public static String getPlayerJoinedMessage() {
		
		return PLAYER_JOINED_MESSAGE.get();
	}
	
	public static boolean getPlayerLeftMessageEnabled() {
		
		return PLAYER_LEFT_MESSAGE_ENABLED.get();
	}
	
	public static String getPlayerLeftMessage() {
		
		return PLAYER_LEFT_MESSAGE.get();
	}
	
	public static boolean getPlayerDiedMessageEnabled() {
		
		return PLAYER_DIED_MESSAGE_ENABLED.get();
	}
	
	public static String getPlayerDiedMessage() {
		
		return PLAYER_DIED_MESSAGE.get();
	}
	
	public static boolean getTamedMobDiedMessageEnabled() {
		
		return TAMED_MOB_DIED_MESSAGE_ENABLED.get();
	}
	
	public static String getTamedMobDiedMessage() {
		
		return TAMED_MOB_DIED_MESSAGE.get();
	}
	
	public static boolean getPlayerGotAdvancementMessageEnabled() {
		
		return PLAYER_GOT_ADVANCEMENT_MESSAGE_ENABLED.get();
	}
	
	public static String getPlayerGotAdvancementMessage() {
		
		return PLAYER_GOT_ADVANCEMENT_MESSAGE.get();
	}
	
	public static List<? extends AbstractCommentedConfig> getCommands() {
		
		return COMMANDS.get();
	}
}
