package de.geheimagentnr1.discordintegration.config.command_config;


import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class DifficultyCommandConfig extends CommandConfig {
	
	
	public DifficultyCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "difficulty";
	}
	
	@NotNull
	
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "difficulty";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows the difficulty of the server.";
	}
}
