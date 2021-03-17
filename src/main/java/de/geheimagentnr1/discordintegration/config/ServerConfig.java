package de.geheimagentnr1.discordintegration.config;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import de.geheimagentnr1.discordintegration.util.VersionHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


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
	
	private static final ForgeConfigSpec.BooleanValue TRANSMIT_BOT_MESSAGES;
	
	private static final ForgeConfigSpec.ConfigValue<List<String>> OTHER_BOTS_COMMAND_PREFIXES;
	
	private static final ForgeConfigSpec.ConfigValue<List<? extends CommandConfig>> COMMANDS;
	
	static {
		
		ACTIVE = BUILDER.comment( "Should the discord integration be active?" )
			.define( "active", false );
		BOT_TOKEN = BUILDER.comment( "Token of your Discord bot:" )
			.define( "bot_token", "INSERT BOT TOKEN HERE" );
		CHANNEL_ID = BUILDER.comment( "Channel ID where the bot will be working" )
			.defineInRange( "channel_id", 0, 0, Long.MAX_VALUE );
		COMMAND_PREFIX = BUILDER.comment( "Command prefix for Discord commands." )
			.define( "command_prefix", "!" );
		MAX_CHAR_COUNT = BUILDER.comment( "How long should Discord messages send to Minecraft Chat be at most? " +
			"If the value is -1, there is no limit to the length." )
			.defineInRange( "max_char_count", -1, -1, 2000 );
		BUILDER.comment( "Options how to deal with other bots" )
			.push( "other_bots" );
		TRANSMIT_BOT_MESSAGES = BUILDER.comment( "Should messages of other bots be sent to the Minecraft chat?" )
			.define( "transmit_bot_messages", false );
		OTHER_BOTS_COMMAND_PREFIXES = BUILDER.comment( "Command prefixes of other bots. " +
			"Messages with these prefixes are not sent to the Mincraft chat." )
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
		
		LOGGER.info( "Loading \"{}\" Config", MOD_NAME );
		LOGGER.info( "{} = {}", ACTIVE.getPath(), ACTIVE.get() );
		LOGGER.info( "{} = {}", BOT_TOKEN.getPath(), BOT_TOKEN.get() );
		LOGGER.info( "{} = {}", CHANNEL_ID.getPath(), CHANNEL_ID.get() );
		LOGGER.info( "{} = {}", COMMAND_PREFIX.getPath(), COMMAND_PREFIX.get() );
		LOGGER.info( "{} = {}", TRANSMIT_BOT_MESSAGES.getPath(), TRANSMIT_BOT_MESSAGES.get() );
		LOGGER.info( "{} = {}", MAX_CHAR_COUNT.getPath(), MAX_CHAR_COUNT.get() );
		LOGGER.info( "{} = {}", COMMANDS.getPath(), COMMANDS.get() );
		LOGGER.info( "\"{}\" Config loaded", MOD_NAME );
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
	
	public static List<? extends AbstractCommentedConfig> getCommands() {
		
		return COMMANDS.get();
	}
}
