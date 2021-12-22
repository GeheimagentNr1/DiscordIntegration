package de.geheimagentnr1.discordintegration.config;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import de.geheimagentnr1.discordintegration.config.command_config.*;
import de.geheimagentnr1.discordintegration.config.command_config.mods.DimensionsCommandConfig;
import de.geheimagentnr1.discordintegration.config.command_config.mods.MobgriefingCommandConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CommandSettingsConfig {
	
	
	private final ForgeConfigSpec.ConfigValue<String> command_prefix;
	
	private final ForgeConfigSpec.IntValue command_normal_user_permission_level;//TODO: Use
	
	private final ForgeConfigSpec.IntValue command_management_user_permission_level;//TODO: Use
	
	private final CommandSettingsOtherBotsConfig command_settings_other_bots_config;
	
	private final ForgeConfigSpec.ConfigValue<List<? extends CommandConfig>> commands;
	
	//package-private
	CommandSettingsConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Command settings" )
			.push( "command_settings" );
		command_prefix = builder.comment( "Command prefix for Discord commands" )
			.define( "command_prefix", "!" );
		command_normal_user_permission_level = builder.comment(
				"Permission level for Minecraft commands for users, who don't have the management role" )
			.defineInRange( "command_normal_user_permission_level", 2, 0, 4 );
		command_management_user_permission_level = builder.comment(
				"Permission level for Minecraft commands for users, who do have the management role" )
			.defineInRange( "command_management_user_permission_level", 4, 0, 4 );
		command_settings_other_bots_config = new CommandSettingsOtherBotsConfig( builder );
		commands = builder.comment( "Command mapping from Discord to Minecraft commands" )
			.defineList( "commands", CommandSettingsConfig::buildDefaultCommandList, CommandConfig::isCorrect );
		builder.pop();
	}
	
	@SuppressWarnings( "OverlyCoupledMethod" )
	private static List<CommandConfig> buildDefaultCommandList() {
		
		ArrayList<CommandConfig> commands = new ArrayList<>();
		commands.add( new DifficultyCommandConfig() );
		commands.add( new GamerulesCommandConfig() );
		commands.add( new HelpCommandConfig() );
		commands.add( new LinkCommandConfig() );
		commands.add( new ModsCommandConfig() );
		commands.add( new OnlineCommandConfig() );
		commands.add( new SeedCommandConfig() );
		commands.add( new TimeCommandConfig() );
		commands.add( new TpsCommandConfig() );
		commands.add( new UnlinkCommandConfig() );
		//Modded commands
		commands.add( new DimensionsCommandConfig() );
		commands.add( new MobgriefingCommandConfig() );
		
		return commands.stream()
			.filter( CommandConfig::shouldBeInCommandList )
			.collect( Collectors.toList() );
	}
	
	public String getCommandPrefix() {
		
		return command_prefix.get();
	}
	
	public int getCommandNormalUserPermissionLevel() {
		
		return command_normal_user_permission_level.get();
	}
	
	public int getCommandManagementUserPermissionLevel() {
		
		return command_management_user_permission_level.get();
	}
	
	public CommandSettingsOtherBotsConfig getCommandSettingsOtherBotsConfig() {
		
		return command_settings_other_bots_config;
	}
	
	public List<? extends AbstractCommentedConfig> getCommands() {
		
		return commands.get();
	}
	
	//package-private
	void printConfig( Logger logger ) {
		
		logger.info( "{} = {}", command_prefix.getPath(), command_prefix.get() );
		command_settings_other_bots_config.printConfig( logger );
		
		List<? extends AbstractCommentedConfig> commandList = commands.get();
		for( int i = 0; i < commandList.size(); i++ ) {
			CommandConfig.printConfig(
				logger,
				String.format(
					"%s[%d]",
					commands.getPath(),
					i
				),
				commandList.get( i )
			);
		}
	}
}
