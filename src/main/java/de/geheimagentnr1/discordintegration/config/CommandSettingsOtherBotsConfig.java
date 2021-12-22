package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class CommandSettingsOtherBotsConfig {
	
	
	
	private final ForgeConfigSpec.BooleanValue transmit_bot_messages;
	
	private final ForgeConfigSpec.ConfigValue<List<String>> other_bots_command_prefixes;
	
	//package-private
	CommandSettingsOtherBotsConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Options how to deal with other bots" )
			.push( "other_bots" );
		transmit_bot_messages = builder.comment( "Should messages of other bots be sent to the Minecraft chat?" )
			.define( "transmit_bot_messages", false );
		other_bots_command_prefixes = builder.comment( "Command prefixes of other bots. " +
				"Messages with these prefixes are not sent to the Minecraft chat." )
			.define( "other_bots_command_prefixes", new ArrayList<>() );
		builder.pop();
	}
	
	public boolean transmitBotMessages() {
		
		return transmit_bot_messages.get();
	}
	
	public List<String> getOtherBotsCommandPrefixes() {
		
		return other_bots_command_prefixes.get();
	}
	
	//package-private
	void printConfig( Logger logger ) {
		
		logger.info( "{} = {}", transmit_bot_messages.getPath(), transmit_bot_messages.get() );
		logger.info( "{} = {}", other_bots_command_prefixes.getPath(), other_bots_command_prefixes.get() );
	}
}
