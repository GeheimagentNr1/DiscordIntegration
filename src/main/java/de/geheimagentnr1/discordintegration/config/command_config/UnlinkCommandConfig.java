package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class UnlinkCommandConfig extends CommandConfig {
	
	
	public UnlinkCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "unlink";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "discord linkings unlink";
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
		
		return "%command% <Minecraft player name>%command_description_separator%unlinks a Minecraft player from the " +
			"Discord user using this command.";
	}
}
