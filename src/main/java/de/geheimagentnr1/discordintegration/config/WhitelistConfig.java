package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class WhitelistConfig extends AbstractSubConfig {
	
	
	@NotNull
	private static final String ENABLED_KEY = "enabled";
	
	@NotNull
	private static final String USE_ROLE_KEY = "use_role";
	
	@NotNull
	private static final String ROLE_ID_KEY = "role_id";
	
	@NotNull
	private static final String USE_SINGLE_LINKING_MANAGEMENT_KEY = "use_single_linking_management";
	
	@NotNull
	private static final String SINGLE_LINKING_MANAGEMENT_ROLE_ID_KEY = "single_linking_management_role_id";
	
	@NotNull
	private static final String LINKING_MANAGEMENT_CHANNEL_ID_KEY = "linking_management_channel_id";
	
	WhitelistConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( "Should the whitelist management in Discord be enabled?", ENABLED_KEY, false );
		registerConfigValue( "Does the user have to have a specific role to be whitelisted?", USE_ROLE_KEY, true );
		registerConfigValue(
			"Role ID of the Discord role, that a user have to have to be whitelisted.",
			ROLE_ID_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, Long.MAX_VALUE )
		);
		registerConfigValue(
			"Does every linking has to be separately activated, before the Minecraft account is being whitelisted?",
			USE_SINGLE_LINKING_MANAGEMENT_KEY,
			true
		);
		registerConfigValue(
			"Role ID of the Discord role, that a user have to have to activate or deactivate the linkings. " +
				"Only needed if " + getFullPath( USE_SINGLE_LINKING_MANAGEMENT_KEY ) + " is true",
			SINGLE_LINKING_MANAGEMENT_ROLE_ID_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, Long.MAX_VALUE )
		);
		registerConfigValue(
			"Channel ID of the channel, where the activation of the linkings are handled.",
			LINKING_MANAGEMENT_CHANNEL_ID_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, Long.MAX_VALUE )
		);
	}
	
	public boolean isEnabled() {
		
		return getValue( Boolean.class, ENABLED_KEY );
	}
	
	public boolean useRole() {
		
		return getValue( Boolean.class, USE_ROLE_KEY );
	}
	
	public long getRoleId() {
		
		return getValue( Long.class, ROLE_ID_KEY );
	}
	
	public boolean useSingleLinkingManagement() {
		
		return getValue( Boolean.class, USE_SINGLE_LINKING_MANAGEMENT_KEY );
	}
	
	public long getSingleLinkingManagementRoleId() {
		
		return getValue( Long.class, SINGLE_LINKING_MANAGEMENT_ROLE_ID_KEY );
	}
	
	public long getLinkingManagementChannelId() {
		
		return getValue( Long.class, LINKING_MANAGEMENT_CHANNEL_ID_KEY );
	}
}
