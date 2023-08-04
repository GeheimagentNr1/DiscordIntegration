package de.geheimagentnr1.discordintegration.config.command_config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;

import java.util.Map;
import java.util.function.Supplier;


public class ModsCommandConfig extends CommandConfig {
	
	
	public ModsCommandConfig() {
		
		super(
			"mods",
			"discord mods",
			false,
			true,
			false,
			"%command%%command_description_separator%shows a list of the mods on the server."
		);
	}
	
	private ModsCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand,
		String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private ModsCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final ModsCommandConfig clone() {
		
		return new ModsCommandConfig( this, mapCreator );
	}
	
	@Override
	public ModsCommandConfig createSubConfig() {
		
		return new ModsCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
