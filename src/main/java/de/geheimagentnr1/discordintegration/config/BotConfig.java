package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;


public class BotConfig {
	
	
	
	private final ForgeConfigSpec.BooleanValue active;
	
	private final ForgeConfigSpec.ConfigValue<String> bot_token;
	
	//package-private
	BotConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "General bot configuration" )
			.push( "bot" );
		active = builder.comment( "Should the Discord integration be active?" )
			.define( "active", false );
		bot_token = builder.comment( "Token of your Discord bot:" )
			.define( "bot_token", "INSERT BOT TOKEN HERE" );
		builder.pop();
	}
	
	public boolean isActive() {
		
		return active.get();
	}
	
	public String getBotToken() {
		
		return bot_token.get();
	}
	
	//package-private
	void printConfig( Logger logger ) {
		
		logger.info( "{} = {}", active.getPath(), active.get() );
		logger.info( "{} = {}", bot_token.getPath(), bot_token.get() );
	}
}
