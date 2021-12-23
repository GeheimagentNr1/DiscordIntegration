package de.geheimagentnr1.discordintegration.config.command_config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;

import java.util.Map;
import java.util.function.Supplier;


public class LinkCommandConfig extends CommandConfig {
	
	
	public LinkCommandConfig() {
		
		super(
			"link",
			"discord link",
			true,
			false,
			false,
			"<Minecraft player name> links a minecraft player with the discord user using this command."
		);
	}
	
	private LinkCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand, String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private LinkCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final LinkCommandConfig clone() {
		
		return new LinkCommandConfig( this, mapCreator );
	}
	
	@Override
	public LinkCommandConfig createSubConfig() {
		
		return new LinkCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
