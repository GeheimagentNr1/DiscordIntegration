package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class ManagementConfig extends AbstractSubConfig {
	
	
	@NotNull
	private static final String ENABLED_KEY = "enabled";
	
	@NotNull
	private static final String CHANNEL_ID_KEY = "channel_id";
	
	@NotNull
	private static final String ROLE_ID_KEY = "role_id";
	
	@NotNull
	private static final String MANAGEMENT_MESSAGES_CONFIG_KEY = "messages";
	
	ManagementConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( "Should a management channel be used?", ENABLED_KEY, false );
		registerConfigValue(
			"Channel ID, where the management channel should be.",
			CHANNEL_ID_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, Long.MAX_VALUE )
		);
		registerConfigValue(
			"Role ID, which Discord users need to execute management commands",
			ROLE_ID_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, Long.MAX_VALUE )
		);
		registerSubConfig(
			"Messages shown on Discord in the management channel",
			MANAGEMENT_MESSAGES_CONFIG_KEY,
			new ManagementMessagesConfig( abstractMod )
		);
	}
	
	public boolean isEnabled() {
		
		return getValue( Boolean.class, ENABLED_KEY );
	}
	
	public long getChannelId() {
		
		return getValue( Long.class, CHANNEL_ID_KEY );
	}
	
	public long getRoleId() {
		
		return getValue( Long.class, ROLE_ID_KEY );
	}
	
	@NotNull
	public ManagementMessagesConfig getManagementMessagesConfig() {
		
		return getSubConfig( ManagementMessagesConfig.class, MANAGEMENT_MESSAGES_CONFIG_KEY );
	}
}
