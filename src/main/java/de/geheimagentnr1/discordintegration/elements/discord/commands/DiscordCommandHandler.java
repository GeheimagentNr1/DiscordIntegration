package de.geheimagentnr1.discordintegration.elements.discord.commands;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.config.command_config.CommandConfig;
import de.geheimagentnr1.discordintegration.elements.discord.AbstractDiscordIntegrationServiceProvider;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSource;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import de.geheimagentnr1.minecraft_forge_api.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;


@Getter( AccessLevel.PROTECTED )
@RequiredArgsConstructor
public class DiscordCommandHandler extends AbstractDiscordIntegrationServiceProvider {
	
	
	@NotNull
	private final DiscordIntegration discordIntegration;
	
	public boolean isCommand( String message ) {
		
		return message.startsWith( serverConfig().getCommandSettingsConfig().getCommandPrefix() );
	}
	
	public void handleCommand(
		Member member,
		String command,
		MinecraftServer server,
		Consumer<String> feedbackSender ) {
		
		if( !executeCommand( member, command, server, feedbackSender ) ) {
			feedbackSender.accept( MessageUtil.replaceParameters(
				serverConfig().getCommandSettingsConfig().getCommandMessagesConfig().getUnknownCommandErrorMessage(),
				Map.of(
					"username", discordManager().getMemberAsTag( member ),
					"nickname", member.getEffectiveName(),
					"new_line", System.lineSeparator()
				)
			) );
		}
	}
	
	private boolean executeCommand(
		Member member,
		String command,
		MinecraftServer server,
		Consumer<String> feedbackSender ) {
		
		boolean hasManagementRole = managementManager().hasManagementRole( member );
		DiscordCommandSource discordCommandSource = new DiscordCommandSource( feedbackSender );
		CommandSourceStack source = new DiscordCommandSourceStack(
			discordIntegration,
			discordCommandSource,
			hasManagementRole
				? serverConfig().getCommandSettingsConfig().getCommandManagementUserPermissionLevel()
				: serverConfig().getCommandSettingsConfig().getCommandNormalUserPermissionLevel(),
			server,
			member
		);
		
		for( CommandConfig commandConfig : serverConfig().getCommandSettingsConfig().getCommands() ) {
			if( commandConfig.isEnabled() ) {
				
				String discordCommand = buildDiscordCommand( commandConfig );
				
				if( doesCommandMatch( commandConfig, command, discordCommand ) ) {
					if( !commandConfig.isManagementCommand() || hasManagementRole ) {
						server.getCommands().performPrefixedCommand(
							source,
							buildMinecraftCommand( commandConfig, discordCommand, command )
						);
						discordCommandSource.sendMessage();
					} else {
						feedbackSender.accept( MessageUtil.replaceParameters(
							serverConfig().getCommandSettingsConfig()
								.getCommandMessagesConfig()
								.getInvalidPermissionsErrorMessage(),
							Map.of(
								"username", discordManager().getMemberAsTag( member ),
								"nickname", member.getEffectiveName(),
								"new_line", System.lineSeparator()
							)
						) );
					}
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean doesCommandMatch(
		CommandConfig commandConfig,
		String command,
		String discordCommand ) {
		
		if( commandConfig.useParameters() ) {
			return command.startsWith( discordCommand + " " );
		} else {
			return command.equals( discordCommand );
		}
	}
	
	private String buildDiscordCommand( CommandConfig commandConfig ) {
		
		return serverConfig().getCommandSettingsConfig().getCommandPrefix() + commandConfig.getDiscordCommand();
	}
	
	private String buildMinecraftCommand(
		CommandConfig commandConfig,
		String discordCommand,
		String command ) {
		
		if( commandConfig.useParameters() ) {
			return "/" + commandConfig.getMinecraftCommand() + command.replaceFirst( discordCommand, "" );
		}
		return "/" + commandConfig.getMinecraftCommand();
	}
}
