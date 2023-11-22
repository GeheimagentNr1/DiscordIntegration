package de.geheimagentnr1.discordintegration.config.command_config.mods;

import de.geheimagentnr1.discordintegration.config.command_config.CommandConfig;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.util.VersionHelper;
import org.jetbrains.annotations.NotNull;


public class DimensionsCommandConfig extends CommandConfig {
	
	
	public DimensionsCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	public boolean shouldBeInCommandList() {
		
		return VersionHelper.isDependecyWithVersionPresent( abstractMod.getModId(), "dimension_access_manager" );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "dimensions";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "dimensions status";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows the access states of all dimensions.";
	}
}
