package de.geheimagentnr1.discordintegration.config.command_config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;

import java.util.Map;
import java.util.function.Supplier;


public class TpsCommandConfig extends CommandConfig {
	
	
	public TpsCommandConfig() {
		
		super(
			"tps",
			"forge tps",
			false,
			true,
			false,
			"%command%%command_description_separator%shows the tps statistic of the server and its dimensions."
		);
	}
	
	private TpsCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand,
		String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private TpsCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final TpsCommandConfig clone() {
		
		return new TpsCommandConfig( this, mapCreator );
	}
	
	@Override
	public TpsCommandConfig createSubConfig() {
		
		return new TpsCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
