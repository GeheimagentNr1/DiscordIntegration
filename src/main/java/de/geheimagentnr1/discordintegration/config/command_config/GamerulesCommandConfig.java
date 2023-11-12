package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class GamerulesCommandConfig extends CommandConfig {
	
	
	public GamerulesCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "gamerules";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "discord gamerules";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows the gamerules and their values.";
	}
}
