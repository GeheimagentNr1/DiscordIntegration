package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class DiscordPresenceConfig extends AbstractSubConfig {
	
	
	@NotNull
	private static final String SHOW_KEY = "show_discord_presence";
	
	@NotNull
	private static final String MESSAGE_KEY = "message";
	
	protected DiscordPresenceConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( "Shall a Discord Presence message be shown?", SHOW_KEY, false );
		registerConfigValue(
			"Message shown in the Discord Presence (activity is always playing). " +
				"(Available parameters: %online_player_count% = Online Player Count, " +
				"%max_player_count% = Max Player Count)",
			MESSAGE_KEY,
			"Minecraft with %online_player_count% players"
		);
	}
	
	public boolean isShow() {
		
		return getValue( Boolean.class, SHOW_KEY );
	}
	
	@NotNull
	public String getMessage() {
		
		return getValue( String.class, MESSAGE_KEY );
	}
}
