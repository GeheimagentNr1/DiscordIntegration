package de.geheimagentnr1.discordintegration.config.command_config;


import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;

import java.util.Map;
import java.util.function.Supplier;


public class DifficultyCommandConfig extends CommandConfig {
	
	
	public DifficultyCommandConfig() {
		
		super(
			"difficulty",
			"difficulty",
			false,
			true,
			false,
			"%command%%command_description_separator%shows the difficulty of the server."
		);
	}
	
	private DifficultyCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand,
		String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private DifficultyCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final DifficultyCommandConfig clone() {
		
		return new DifficultyCommandConfig( this, mapCreator );
	}
	
	@Override
	public DifficultyCommandConfig createSubConfig() {
		
		return new DifficultyCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
