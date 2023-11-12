package de.geheimagentnr1.discordintegration.elements.commands;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandInterface;
import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandsRegisterFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@RequiredArgsConstructor
public class ModCommandsRegisterFactory extends CommandsRegisterFactory {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	private final DiscordManager discordManager;
	
	@NotNull
	private final ChatManager chatManager;
	
	@NotNull
	private final LinkingsManager linkingsManager;
	
	@NotNull
	@Override
	public List<CommandInterface> commands() {
		
		return List.of(
			new DiscordCommand( serverConfig, discordManager, linkingsManager ),
			new DiscordOpCommand( serverConfig, discordManager, linkingsManager ),
			new EmoteToDiscordCommand( chatManager ),
			new SayToDiscordCommand( chatManager )
		);
	}
}
