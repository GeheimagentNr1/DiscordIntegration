package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;


public class ManagementConfig {
	
	
	private final ForgeConfigSpec.BooleanValue enabled;
	
	private final ForgeConfigSpec.LongValue channel_id;
	
	private final ForgeConfigSpec.LongValue role_id;
	
	private final ManagementMessagesConfig management_messages_config;
	
	//package-private
	ManagementConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Chat channel settings" )
			.push( "chat" );
		enabled = builder.comment( "Should the Minecraft and Discord chat be linked?" )
			.define( "enabled", false );
		channel_id = builder.comment( "Channel ID, where the management channel should be." )
			.defineInRange( "channel_id", 0, 0, Long.MAX_VALUE );
		role_id = builder.comment( "Role ID, which discord users need to execute managment commands" )
			.defineInRange( "channel_id", 0, 0, Long.MAX_VALUE );
		management_messages_config = new ManagementMessagesConfig( builder );
		builder.pop();
	}
	
	public boolean isEnabled() {
		
		return enabled.get();
	}
	
	public long getChannelId() {
		
		return channel_id.get();
	}
	
	public long getRoleId() {
		
		return role_id.get();
	}
	
	public ManagementMessagesConfig getManagementMessagesConfig() {
		
		return management_messages_config;
	}
	
	//package-private
	void printConfig( Logger logger ) {
		
		logger.info( "{} = {}", enabled.getPath(), enabled.get() );
		logger.info( "{} = {}", channel_id.getPath(), channel_id.get() );
		logger.info( "{} = {}", role_id.getPath(), role_id.get() );
		management_messages_config.printConfig( logger );
	}
}
