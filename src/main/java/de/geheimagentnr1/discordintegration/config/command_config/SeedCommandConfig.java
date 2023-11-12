package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class SeedCommandConfig extends CommandConfig {
	
	
	public SeedCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "seed";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "seed";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows the seed of the active world.";
	}
}
