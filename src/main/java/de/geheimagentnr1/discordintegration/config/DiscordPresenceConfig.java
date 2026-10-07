package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;


public class DiscordPresenceConfig {
	
	
	private final ForgeConfigSpec.BooleanValue show;
	
	private final ForgeConfigSpec.ConfigValue<String> message;
	
	//package-private
	DiscordPresenceConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Discord Presence configuration" )
			.push( "discord_presence" );
		show = builder.comment( "Shall a Discord Presence message be shown?" )
			.define( "show_discord_presence", false );
		message = builder.comment( "Message shown in the Discord Presence (activity is always playing). " +
				"(Available parameters: %online_player_count% = Online Player Count, " +
				"%max_player_count% = Max Player Count)" )
			.define( "message", "Minecraft with %online_player_count% players" );
		builder.pop();
	}
	
	public boolean isShow() {
		
		return show.get();
	}
	
	public String getMessage() {
		
		return message.get();
	}
	
	//package-private
	void printConfig( Logger logger ) {
		
		logger.info( "{} = {}", show.getPath(), show.get() );
		logger.info( "{} = {}", message.getPath(), message.get() );
	}
}
