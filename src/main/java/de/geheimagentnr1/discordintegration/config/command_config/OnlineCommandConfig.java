package de.geheimagentnr1.discordintegration.config.command_config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;

import java.util.Map;
import java.util.function.Supplier;


public class OnlineCommandConfig extends CommandConfig {
	
	
	public OnlineCommandConfig() {
		
		super(
			"online",
			"list",
			false,
			true,
			false,
			"shows how many and which players are on the server."
		);
	}
	
	private OnlineCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand, String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private OnlineCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final OnlineCommandConfig clone() {
		
		return new OnlineCommandConfig( this, mapCreator );
	}
	
	@Override
	public OnlineCommandConfig createSubConfig() {
		
		return new OnlineCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
