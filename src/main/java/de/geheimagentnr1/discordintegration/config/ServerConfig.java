package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ServerConfig {
	
	
	private static final Logger LOGGER = LogManager.getLogger( ServerConfig.class );
	
	private static final String MOD_NAME = ModLoadingContext.get().getActiveContainer().getModInfo().getDisplayName();
	
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	
	public static final ForgeConfigSpec CONFIG;
	
	public static final BotConfig BOT_CONFIG;
	
	public static final ChatConfig CHAT_CONFIG;
	
	public static final ManagementConfig MANAGEMENT_CONFIG;
	
	public static final WhitelistConfig WHITELIST_CONFIG;
	
	public static final CommandSettingsConfig COMMAND_SETTINGS_CONFIG;
	
	static {
		
		BOT_CONFIG = new BotConfig( BUILDER );
		CHAT_CONFIG = new ChatConfig( BUILDER );
		MANAGEMENT_CONFIG = new ManagementConfig( BUILDER );
		WHITELIST_CONFIG = new WhitelistConfig( BUILDER );
		COMMAND_SETTINGS_CONFIG = new CommandSettingsConfig( BUILDER );
		
		CONFIG = BUILDER.build();
	}
	
	public static void handleConfigEvent() {
		
		printConfig();
		DiscordNet.init();
	}
	
	private static void printConfig() {
		
		LOGGER.info( "Loading \"{}\" Server Config", MOD_NAME );
		BOT_CONFIG.printConfig( LOGGER );
		CHAT_CONFIG.printConfig( LOGGER );
		MANAGEMENT_CONFIG.printConfig( LOGGER );
		WHITELIST_CONFIG.printConfig( LOGGER );
		COMMAND_SETTINGS_CONFIG.printConfig( LOGGER );
		
		LOGGER.info( "\"{}\" Server Config loaded", MOD_NAME );
	}
}
