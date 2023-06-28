package de.geheimagentnr1.discordintegration.elements.commands;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandInterface;
import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandsRegisterFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@RequiredArgsConstructor
public class ModCommandsRegisterFactory extends CommandsRegisterFactory {
	
	
	@NotNull
	private final DiscordNet discordNet;
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	@Override
	public List<CommandInterface> commands() {
		
		return List.of(
			new DiscordCommand( serverConfig ),
			new EmoteToDiscordCommand( discordNet ),
			new SayToDiscordCommand( discordNet )
		);
	}
}
