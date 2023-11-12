package de.geheimagentnr1.discordintegration.config.command_config;


import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractListEntryConfig;
import org.jetbrains.annotations.NotNull;


public class CommandConfig extends AbstractListEntryConfig {
	
	
	@NotNull
	private static final String DISCORD_COMMAND_KEY = "discord_command";
	
	@NotNull
	private static final String MINECRAFT_COMMAND_KEY = "minecraft_command";
	
	@NotNull
	private static final String USE_PARAMETERS_KEY = "use_parameters";
	
	@NotNull
	private static final String ENABLED_KEY = "enabled";
	
	@NotNull
	private static final String MANAGEMENT_COMMAND_KEY = "management_command";
	
	@NotNull
	private static final String DESCRIPTION_KEY = "description";
	
	public CommandConfig(
		@NotNull AbstractMod _abstractMod,
		@NotNull AbstractCommentedConfig _abstractCommentedConfig ) {
		
		super( _abstractMod, _abstractCommentedConfig );
	}
	
	public CommandConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( "Discord command without prefix", DISCORD_COMMAND_KEY, discordCommandDefaultValue() );
		registerConfigValue(
			"Minecraft command without prefix ('/')",
			MINECRAFT_COMMAND_KEY,
			minecraftCommandDefaultValue()
		);
		registerConfigValue(
			"Should everything attached to the Discord command, be attached to the Minecraft command, too?",
			USE_PARAMETERS_KEY,
			useParametersDefaultValue()
		);
		registerConfigValue( "Should the command be active?", ENABLED_KEY, enabledDefaultValue() );
		registerConfigValue(
			"Should this command only be usable by Discord users with the management role?",
			MANAGEMENT_COMMAND_KEY,
			managementCommandDefaultValue()
		);
		registerConfigValue(
			"Description for the help command " +
				"(Available parameters: %command% = Command, " +
				"%command_description_separator% = Command Description Separator)",
			DESCRIPTION_KEY,
			descriptionDefaultValue()
		);
	}
	
	public boolean shouldBeInCommandList() {
		
		return true;
	}
	
	@NotNull
	protected String discordCommandDefaultValue() {
		
		return "";
	}
	
	@NotNull
	protected String minecraftCommandDefaultValue() {
		
		return "";
	}
	
	protected boolean useParametersDefaultValue() {
		
		return false;
	}
	
	protected boolean enabledDefaultValue() {
		
		return true;
	}
	
	protected boolean managementCommandDefaultValue() {
		
		return false;
	}
	
	@NotNull
	protected String descriptionDefaultValue() {
		
		return "";
	}
	
	@NotNull
	public String getDiscordCommand() {
		
		return getValue( String.class, DISCORD_COMMAND_KEY );
	}
	
	@NotNull
	public String getMinecraftCommand() {
		
		return getValue( String.class, MINECRAFT_COMMAND_KEY );
	}
	
	public boolean useParameters() {
		
		return getValue( Boolean.class, USE_PARAMETERS_KEY );
	}
	
	public boolean isEnabled() {
		
		return getValue( Boolean.class, ENABLED_KEY );
	}
	
	public boolean isManagementCommand() {
		
		return getValue( Boolean.class, MANAGEMENT_COMMAND_KEY );
	}
	
	@NotNull
	public String getDescription() {
		
		return getValue( String.class, DESCRIPTION_KEY );
	}
}
