package de.geheimagentnr1.discordintegration.elements.discord.commands;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.config.command_config.CommandConfig;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSource;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;


public class DiscordCommandHandler {
	
	
	public static boolean isCommand( String message ) {
		
		return message.startsWith( ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandPrefix() );
	}
	
	public static void handleCommand(
		Member member,
		String command,
		MinecraftServer server,
		Consumer<String> feedbackSender ) {
		
		if( member != null ) {
			if( !executeCommand( member, command, server, feedbackSender ) ) {
				feedbackSender.accept( String.format(
					"%n%s%nError: Unknown Command",
					member.getEffectiveName()
				) );
			}
		}
	}
	
	private static boolean executeCommand(
		Member member,
		String command,
		MinecraftServer server,
		Consumer<String> feedbackSender ) {
		
		boolean hasManagementRole = ManagementManager.hasManagementRole( member );
		DiscordCommandSource discordCommandSource = new DiscordCommandSource( feedbackSender );
		CommandSourceStack source = new DiscordCommandSourceStack(
			discordCommandSource,
			hasManagementRole
				? ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandManagementUserPermissionLevel()
				: ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandNormalUserPermissionLevel(),
			server,
			member
		);
		
		for( AbstractCommentedConfig commandConfig : ServerConfig.COMMAND_SETTINGS_CONFIG.getCommands() ) {
			if( CommandConfig.isEnabled( commandConfig ) &&
				( !CommandConfig.isManagementCommand( commandConfig ) ||
					hasManagementRole ) ) {
				String discordCommand = buildDiscordCommand( commandConfig );
				
				if( doesCommandMatch( commandConfig, command, discordCommand ) ) {
					server.getCommands().performCommand(
						source,
						buildMinecraftCommand( commandConfig, discordCommand, command )
					);
					discordCommandSource.sendMessage();
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean doesCommandMatch(
		AbstractCommentedConfig commandConfig,
		String command,
		String discordCommand ) {
		
		if( CommandConfig.useParameters( commandConfig ) ) {
			return command.startsWith( discordCommand + " " );
		} else {
			return command.equals( discordCommand );
		}
	}
	
	private static String buildDiscordCommand( AbstractCommentedConfig commandConfig ) {
		
		return ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandPrefix() +
			CommandConfig.getDiscordCommand( commandConfig );
	}
	
	private static String buildMinecraftCommand(
		AbstractCommentedConfig commandConfig,
		String discordCommand,
		String command ) {
		
		if( CommandConfig.useParameters( commandConfig ) ) {
			return "/" + CommandConfig.getMinecraftCommand( commandConfig ) + command.replaceFirst(
				discordCommand,
				""
			);
		}
		return "/" + CommandConfig.getMinecraftCommand( commandConfig );
	}
}
