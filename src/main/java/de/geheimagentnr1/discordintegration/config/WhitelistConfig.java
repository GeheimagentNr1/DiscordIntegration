package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;


public class WhitelistConfig {
	
	
	private final ForgeConfigSpec.BooleanValue enabled;
	
	private final ForgeConfigSpec.BooleanValue use_role;
	
	private final ForgeConfigSpec.LongValue role_id;
	
	private final ForgeConfigSpec.BooleanValue use_single_linking_management;
	
	private final ForgeConfigSpec.LongValue single_linking_management_role_id;
	
	private final ForgeConfigSpec.LongValue linking_management_channel_id;
	
	//package-private
	WhitelistConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Settings for whitelist management in Discord" )
			.push( "whitelist" );
		enabled = builder.comment( "Should the whitelist management in Discord be enabled?" )
			.define( "enabled", false );
		use_role = builder.comment( "Does the user have to have a specific role to be whitelisted?" )
			.define( "use_role", true );
		role_id = builder.comment( "Role ID of the Discord role, that a user have to have to be whitelisted." )
			.defineInRange( "role_id", 0, 0, Long.MAX_VALUE );
		use_single_linking_management = builder.comment(
				"Does every linking has to be separately activated, before the Minecraft account is being " +
					"whitelisted?" )
			.define( "use_single_linking_management", true );
		single_linking_management_role_id = builder.comment(
				"Role ID of the Discord role, that a user have to have to activate or deactivate the linkings. Only " +
					"needed if " + String.join( ".", use_single_linking_management.getPath() ) + " is true" )
			.defineInRange( "single_linking_management_role_id", 0, 0, Long.MAX_VALUE );
		linking_management_channel_id = builder.comment(
				"Channel ID of the channel, where the activation of the linkings are handled." )
			.defineInRange( "linking_management_channel_id", 0, 0, Long.MAX_VALUE );
		builder.pop();
	}
	
	public boolean isEnabled() {
		
		return enabled.get();
	}
	
	public boolean useRole() {
		
		return use_role.get();
	}
	
	public long getRoleId() {
		
		return role_id.get();
	}
	
	public boolean useSingleLinkingManagement() {
		
		return use_single_linking_management.get();
	}
	
	public long getSingleLinkingManagementRoleId() {
		
		return single_linking_management_role_id.get();
	}
	
	public long getLinkingManagementChannelId() {
		
		return linking_management_channel_id.get();
	}
	
	//package-private
	@SuppressWarnings( "DuplicatedCode" )
	void printConfig( Logger logger ) {
		
		logger.info( "{} = {}", enabled.getPath(), enabled.get() );
		logger.info( "{} = {}", use_role.getPath(), use_role.get() );
		logger.info( "{} = {}", role_id.getPath(), role_id.get() );
		logger.info( "{} = {}", use_single_linking_management.getPath(), use_single_linking_management.get() );
		logger.info( "{} = {}", single_linking_management_role_id.getPath(), single_linking_management_role_id.get() );
		logger.info( "{} = {}", linking_management_channel_id.getPath(), linking_management_channel_id.get() );
	}
}