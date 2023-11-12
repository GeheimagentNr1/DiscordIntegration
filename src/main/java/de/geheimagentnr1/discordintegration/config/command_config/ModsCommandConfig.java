package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class ModsCommandConfig extends CommandConfig {
	
	
	public ModsCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "mods";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "discord mods";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows a list of the mods on the server.";
	}
}
