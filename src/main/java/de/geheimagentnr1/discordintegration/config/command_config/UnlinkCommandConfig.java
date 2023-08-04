package de.geheimagentnr1.discordintegration.config.command_config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;

import java.util.Map;
import java.util.function.Supplier;


public class UnlinkCommandConfig extends CommandConfig {
	
	
	public UnlinkCommandConfig() {
		
		super(
			"unlink",
			"discord linkings unlink",
			true,
			false,
			false,
			"%command%<Minecraft player name>%command_description_separator%unlinks a Minecraft player from the " +
				"Discord user using this command."
		);
	}
	
	private UnlinkCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand,
		String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private UnlinkCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final UnlinkCommandConfig clone() {
		
		return new UnlinkCommandConfig( this, mapCreator );
	}
	
	@Override
	public UnlinkCommandConfig createSubConfig() {
		
		return new UnlinkCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
