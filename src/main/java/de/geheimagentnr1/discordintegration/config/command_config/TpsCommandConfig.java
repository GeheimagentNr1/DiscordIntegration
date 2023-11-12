package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import org.jetbrains.annotations.NotNull;


public class TpsCommandConfig extends CommandConfig {
	
	
	public TpsCommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "tps";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "forge tps";
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows the tps statistic of the server and its dimensions.";
	}
}
