package de.geheimagentnr1.discordintegration.config.command_config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;

import java.util.Map;
import java.util.function.Supplier;


public class GamerulesCommandConfig extends CommandConfig {
	
	
	public GamerulesCommandConfig() {
		
		super(
			"gamerules",
			"discord gamerules",
			false,
			true,
			false,
			"%command%%command_description_separator%shows the gamerules and their values."
		);
	}
	
	private GamerulesCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand,
		String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private GamerulesCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final GamerulesCommandConfig clone() {
		
		return new GamerulesCommandConfig( this, mapCreator );
	}
	
	@Override
	public GamerulesCommandConfig createSubConfig() {
		
		return new GamerulesCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
