package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.discordintegration.api.AbstractMod;
import de.geheimagentnr1.discordintegration.api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class HelpCommandConfig extends CommandConfig {
	
	
	public HelpCommandConfig( @NotNull AbstractMod _abstractMod, @NotNull AbstractSubConfig _parent ) {
		
		super( _abstractMod, _parent );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "help";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "discord commands";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows all commands with their description.";
	}
}
