package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MainConfig {
	
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String mod_name = "Discord Integration";
	
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	
	public static final ForgeConfigSpec CONFIG;
	
	private static final ForgeConfigSpec.BooleanValue ACTIVE;
	
	private static final ForgeConfigSpec.ConfigValue<String> BOT_TOKEN;
	
	private static final ForgeConfigSpec.LongValue CHANNEL_ID;
	
	static {
		
		ACTIVE = BUILDER.comment( "Should the discord integration be active?" ).define( "active", false );
		BOT_TOKEN = BUILDER.comment( "Token of your Discord bot:" ).define( "bot_token", "INSERT BOT TOKEN HERE" );
		CHANNEL_ID = BUILDER.comment( "Channel ID where the bot will be working" )
			.defineInRange( "channel_id", 0, 0, Long.MAX_VALUE );
		
		CONFIG = BUILDER.build();
	}
	
	public static void handleConfigEvent() {
		
		printConfig();
		DiscordNet.init();
	}
	
	private static void printConfig() {
		
		LOGGER.info( "Loading \"{}\" Config", mod_name );
		LOGGER.info( "{} = {}", ACTIVE.getPath(), ACTIVE.get() );
		LOGGER.info( "{} = {}", BOT_TOKEN.getPath(), BOT_TOKEN.get() );
		LOGGER.info( "{} = {}", CHANNEL_ID.getPath(), CHANNEL_ID.get() );
		LOGGER.info( "\"{}\" Config loaded", mod_name );
	}
	
	public static boolean getActive() {
		
		return ACTIVE.get();
	}
	
	public static String getBotToken() {
		
		return BOT_TOKEN.get();
	}
	
	public static long getChannelID() {
		
		return CHANNEL_ID.get();
	}
}
