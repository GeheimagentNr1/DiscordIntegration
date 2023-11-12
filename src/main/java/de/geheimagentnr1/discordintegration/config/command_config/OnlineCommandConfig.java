package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class OnlineCommandConfig extends CommandConfig {
	
	
	public OnlineCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "online";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "list";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows how many and which players are on the server.";
	}
}
