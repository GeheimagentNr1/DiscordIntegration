package de.geheimagentnr1.discordintegration.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import de.geheimagentnr1.discordintegration.DiscordIntegration;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ModConfig {
	
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String mod_name = "Discord Integration";
	
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	
	private static final ForgeConfigSpec CONFIG;
	
	public static final ForgeConfigSpec.ConfigValue<String> BOT_TOKEN;
	
	public static final ForgeConfigSpec.LongValue CHANNEL_ID;
	
	static {
		
		BOT_TOKEN = BUILDER.comment( "Token of your Discord bot:" ).define( "bot_token", "INSERT BOT TOKEN HERE" );
		CHANNEL_ID = BUILDER.comment( "Channel ID where the bot will be working" )
			.defineInRange( "channel_id", 0, 0, Long.MAX_VALUE );
		
		CONFIG = BUILDER.build();
	}
	
	public static void load() {
		
		CommentedFileConfig configData = CommentedFileConfig.builder( FMLPaths.CONFIGDIR.get().resolve(
			DiscordIntegration.MODID + ".toml" ) ).sync().autosave().writingMode( WritingMode.REPLACE ).build();
		
		LOGGER.info( "Loading \"{}\" Config", mod_name );
		configData.load();
		CONFIG.setConfig( configData );
		LOGGER.info( "{} = {}", BOT_TOKEN.getPath(), BOT_TOKEN.get() );
		LOGGER.info( "{} = {}", CHANNEL_ID.getPath(), CHANNEL_ID.get() );
		LOGGER.info( "\"{}\" Config loaded", mod_name );
	}
}
