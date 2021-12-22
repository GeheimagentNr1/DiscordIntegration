package de.geheimagentnr1.discordintegration.config.command_config.mods;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import de.geheimagentnr1.discordintegration.config.command_config.CommandConfig;
import de.geheimagentnr1.discordintegration.util.VersionHelper;

import java.util.Map;
import java.util.function.Supplier;


public class MobgriefingCommandConfig extends CommandConfig {
	
	
	public MobgriefingCommandConfig() {
		
		super(
			"mobgriefing",
			"mobgriefing list",
			false,
			true,
			false,
			"shows all mobgriefing options of the mobs."
		);
	}
	
	@Override
	public boolean shouldBeInCommandList() {
		
		return VersionHelper.isDependecyWithVersionPresent( "moremobgriefingoptions" );
	}
	
	private MobgriefingCommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand, String description ) {
		
		super( discordCommand, minecraftCommand, useParameters, enabled, managementCommand, description );
	}
	
	private MobgriefingCommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final MobgriefingCommandConfig clone() {
		
		return new MobgriefingCommandConfig( this, mapCreator );
	}
	
	@Override
	public MobgriefingCommandConfig createSubConfig() {
		
		return new MobgriefingCommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			useParameters( this ),
			isEnabled( this ),
			isManagementCommand( this ),
			getDescription( this )
		);
	}
}
