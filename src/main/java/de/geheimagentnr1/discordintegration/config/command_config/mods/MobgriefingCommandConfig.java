package de.geheimagentnr1.discordintegration.config.command_config.mods;

import de.geheimagentnr1.discordintegration.config.command_config.CommandConfig;
import de.geheimagentnr1.discordintegration.util.VersionHelper;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class MobgriefingCommandConfig extends CommandConfig {
	
	
	public MobgriefingCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	public boolean shouldBeInCommandList() {
		
		return VersionHelper.isDependecyWithVersionPresent( abstractMod.getModId(), "moremobgriefingoptions" );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "mobgriefing";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "mobgriefing list";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows all mobgriefing options of the mobs.";
	}
}
