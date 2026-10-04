package de.geheimagentnr1.discordintegration.config.command_config;

import de.geheimagentnr1.discordintegration.api.AbstractMod;
import de.geheimagentnr1.discordintegration.api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class TimeCommandConfig extends CommandConfig {
	
	
	public TimeCommandConfig( @NotNull AbstractMod _abstractMod, @NotNull AbstractSubConfig _parent ) {
		
		super( _abstractMod, _parent );
	}
	
	@NotNull
	@Override
	protected String discordCommandDefaultValue() {
		
		return "time";
	}
	
	@NotNull
	@Override
	protected String minecraftCommandDefaultValue() {
		
		return "time query day";
	}
	
	// "time query daytime" was the default before Minecraft 26.1 and no longer exists there (world clocks).
	@NotNull
	@Override
	public String getMinecraftCommand() {
		
		String minecraftCommand = super.getMinecraftCommand();
		return minecraftCommand.equals( "time query daytime" ) ? minecraftCommandDefaultValue() : minecraftCommand;
	}
	
	@NotNull
	@Override
	protected String descriptionDefaultValue() {
		
		return "%command%%command_description_separator%shows the current day's time on the server.";
	}
}
