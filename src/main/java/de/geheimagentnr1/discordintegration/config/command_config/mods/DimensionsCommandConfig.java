package de.geheimagentnr1.discordintegration.config.command_config.mods;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import de.geheimagentnr1.discordintegration.config.command_config.CommandConfig;
import de.geheimagentnr1.discordintegration.util.VersionHelper;

import java.util.Map;
import java.util.function.Supplier;


public class DimensionsCommandConfig extends CommandConfig {
	
	
	public DimensionsCommandConfig() {
		
		super(
			"dimensions",
			"dimensions status",
			false,
			true,
			false,
			"shows the access states of all dimensions."
		);
	}
	
	@Override
	public boolean shouldBeInCommandList() {
		
		return VersionHelper.isDependecyWithVersionPresent( "dimension_access_manager" );
	}
	
	private DimensionsCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand,
		String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private DimensionsCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final DimensionsCommandConfig clone() {
		
		return new DimensionsCommandConfig( this, mapCreator );
	}
	
	@Override
	public DimensionsCommandConfig createSubConfig() {
		
		return new DimensionsCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
