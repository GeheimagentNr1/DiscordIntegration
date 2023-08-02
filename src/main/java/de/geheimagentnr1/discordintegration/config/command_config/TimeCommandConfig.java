package de.geheimagentnr1.discordintegration.config.command_config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;

import java.util.Map;
import java.util.function.Supplier;


public class TimeCommandConfig extends CommandConfig {
	
	
	public TimeCommandConfig() {
		
		super(
			"time",
			"time query daytime",
			false,
			true,
			false,
			"shows the current day's time on the server."
		);
	}
	
	private TimeCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand,
		String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private TimeCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final TimeCommandConfig clone() {
		
		return new TimeCommandConfig( this, mapCreator );
	}
	
	@Override
	public TimeCommandConfig createSubConfig() {
		
		return new TimeCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
