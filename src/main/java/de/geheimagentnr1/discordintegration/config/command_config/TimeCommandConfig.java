package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class TimeCommandConfig extends CommandConfig {
	
	
	public TimeCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "time";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "time query daytime";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows the current day's time on the server.";
	}
}
