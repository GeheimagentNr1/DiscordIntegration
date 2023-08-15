package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;


@Log4j2
public class ServerConfig {
	
	
	public static final String MOD_NAME = ModLoadingContext.get().getActiveContainer().getModInfo().getDisplayName();
	
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
		DiscordManager.init();
	}
	
	private static void printConfig() {
		
		log.info( "Loading \"{}\" Server Config", MOD_NAME );
		BOT_CONFIG.printConfig( log );
		CHAT_CONFIG.printConfig( log );
		MANAGEMENT_CONFIG.printConfig( log );
		WHITELIST_CONFIG.printConfig( log );
		COMMAND_SETTINGS_CONFIG.printConfig( log );
		
		log.info( "\"{}\" Server Config loaded", MOD_NAME );
	}
}
