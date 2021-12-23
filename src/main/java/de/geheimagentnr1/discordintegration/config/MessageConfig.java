package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.slf4j.Logger;


public class MessageConfig {
	
	
	private final ForgeConfigSpec.BooleanValue enabled;
	
	private final ForgeConfigSpec.ConfigValue<String> message;
	
	//package-private
	MessageConfig(
		ForgeConfigSpec.Builder builder,
		String path,
		String pathComment,
		String enabledComment,
		String messageComment,
		String messageDefaultValue ) {
		
		builder.comment( pathComment )
			.push( path );
		enabled = builder.comment( enabledComment )
			.define( "enabled", true );
		message = builder.comment( messageComment )
			.define( "message", messageDefaultValue );
		builder.pop();
	}
	
	public boolean isEnabled() {
		
		return enabled.get();
	}
	
	public String getMessage() {
		
		return message.get();
	}
	
	//package-private
	void printConfig( Logger logger ) {
		
		logger.info( "{} = {}", enabled.getPath(), enabled.get() );
		logger.info( "{} = {}", message.getPath(), message.get() );
	}
}
