package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class LinkCommandConfig extends CommandConfig {
	
	
	public LinkCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "link";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "discord linkings link";
	}
	
	@Override
	protected boolean useParametersDefaultValue() {
		
		return true;
	}
	
	@Override
	protected boolean enabledDefaultValue() {
		
		return false;
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command% <Minecraft player name>%command_description_separator%links a Minecraft player with the " +
			"Discord user using this command.";
	}
}
