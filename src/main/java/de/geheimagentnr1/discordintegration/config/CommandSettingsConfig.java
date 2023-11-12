package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.discordintegration.config.command_config.*;
import de.geheimagentnr1.discordintegration.config.command_config.mods.DimensionsCommandConfig;
import de.geheimagentnr1.discordintegration.config.command_config.mods.MobgriefingCommandConfig;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CommandSettingsConfig extends AbstractSubConfig {
	
	
	@NotNull
	private static final String COMMAND_PREFIX_KEY = "command_prefix";
	
	@NotNull
	private static final String COMMAND_NORMAL_USER_PERMISSION_LEVEL_KEY = "command_normal_user_permission_level";
	
	@NotNull
	private static final String COMMAND_MANAGEMENT_USER_PERMISSION_LEVEL_KEY =
		"command_management_user_permission_level";
	
	@NotNull
	private static final String OTHER_BOTS_COMMAND_PREFIXES_KEY = "other_bots_command_prefixes";
	
	@NotNull
	private static final String COMMAND_MESSAGES_CONFIG_KEY = "messages";
	
	@NotNull
	private static final String COMMANDS_KEY = "commands";
	
	protected CommandSettingsConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( "Command prefix for Discord commands", COMMAND_PREFIX_KEY, "!" );
		registerConfigValue(
			"Permission level for Minecraft commands for users, who don't have the management role",
			COMMAND_NORMAL_USER_PERMISSION_LEVEL_KEY,
			( builder, path ) -> builder.defineInRange( path, 2, 0, 4 )
		);
		registerConfigValue(
			"Permission level for Minecraft commands for users, who do have the management role",
			COMMAND_MANAGEMENT_USER_PERMISSION_LEVEL_KEY,
			( builder, path ) -> builder.defineInRange( path, 4, 0, 4 )
		);
		registerConfigValue(
			"Command prefixes of other bots. Messages with these prefixes are not sent to the Minecraft chat.",
			OTHER_BOTS_COMMAND_PREFIXES_KEY,
			new ArrayList<>()
		);
		registerSubConfig(
			"Command message settings",
			COMMAND_MESSAGES_CONFIG_KEY,
			new CommandMessagesConfig( abstractMod )
		);
		registerSubConfigList(
			"Command mapping from Discord to Minecraft commands",
			COMMANDS_KEY,
			CommandConfig.class,
			CommandConfig::new,
			buildDefaultCommandList()
		);
	}
	
	@NotNull
	private List<CommandConfig> buildDefaultCommandList() {
		
		ArrayList<CommandConfig> commands = new ArrayList<>();
		commands.add( new DifficultyCommandConfig( abstractMod ) );
		commands.add( new GamerulesCommandConfig( abstractMod ) );
		commands.add( new HelpCommandConfig( abstractMod ) );
		commands.add( new LinkCommandConfig( abstractMod ) );
		commands.add( new ModsCommandConfig( abstractMod ) );
		commands.add( new OnlineCommandConfig( abstractMod ) );
		commands.add( new SeedCommandConfig( abstractMod ) );
		commands.add( new TimeCommandConfig( abstractMod ) );
		commands.add( new TpsCommandConfig( abstractMod ) );
		commands.add( new UnlinkCommandConfig( abstractMod ) );
		//Modded commands
		commands.add( new DimensionsCommandConfig( abstractMod ) );
		commands.add( new MobgriefingCommandConfig( abstractMod ) );
		
		return commands.stream()
			.filter( CommandConfig::shouldBeInCommandList )
			.collect( Collectors.toList() );
	}
	
	@NotNull
	public String getCommandPrefix() {
		
		return getValue( String.class, COMMAND_PREFIX_KEY );
	}
	
	public int getCommandNormalUserPermissionLevel() {
		
		return getValue( Integer.class, COMMAND_NORMAL_USER_PERMISSION_LEVEL_KEY );
	}
	
	public int getCommandManagementUserPermissionLevel() {
		
		return getValue( Integer.class, COMMAND_MANAGEMENT_USER_PERMISSION_LEVEL_KEY );
	}
	
	@NotNull
	public List<String> getOtherBotsCommandPrefixes() {
		
		return getListValue( String.class, OTHER_BOTS_COMMAND_PREFIXES_KEY );
	}
	
	@NotNull
	public CommandMessagesConfig getCommandMessagesConfig() {
		
		return getSubConfig( CommandMessagesConfig.class, COMMAND_MESSAGES_CONFIG_KEY );
	}
	
	@NotNull
	public List<CommandConfig> getCommands() {
		
		return getSubConfigListValue( CommandConfig.class, COMMANDS_KEY );
	}
}
