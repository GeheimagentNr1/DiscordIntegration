package de.geheimagentnr1.discordintegration.elements.discord;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import de.geheimagentnr1.discordintegration.config.CommandConfig;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


//package-private
class DiscordCommandHandler {
	
	
	//package-private
	boolean handleCommand(
		@NotNull String command,
		@NotNull AbstractMod abstractMod,
		@NotNull ServerConfig serverConfig,
		@NotNull DiscordNet discordNet,
		@NotNull MinecraftServer server ) {
		
		DiscordCommandSource discordCommandSource = new DiscordCommandSource( discordNet );
		CommandSourceStack source = buildSource( abstractMod, server, discordCommandSource );
		for( AbstractCommentedConfig abstractCommentedConfig : serverConfig.getCommands() ) {
			String discordCommand = buildDiscordCommand( serverConfig, abstractCommentedConfig );
			if( CommandConfig.getEnabled( abstractCommentedConfig ) && (
				discordCommand.equals( command ) ||
					CommandConfig.getUseParameter( abstractCommentedConfig ) &&
						command.startsWith( discordCommand + " " ) ) ) {
				server.getCommands().performPrefixedCommand(
					source,
					buildMinecraftCommand( abstractCommentedConfig, discordCommand, command )
				);
				discordCommandSource.sendMessage();
				return true;
			}
		}
		return false;
	}
	
	@NotNull
	private CommandSourceStack buildSource(
		@NotNull AbstractMod abstractMod,
		@NotNull MinecraftServer server,
		@NotNull DiscordCommandSource discordCommandSource ) {
		
		return new CommandSourceStack(
			discordCommandSource,
			Vec3.ZERO,
			Vec2.ZERO,
			Objects.requireNonNull( server.overworld() ),
			4,
			abstractMod.getModName(),
			Component.literal( abstractMod.getModName() ),
			server,
			null
		);
	}
	
	@NotNull
	private String buildDiscordCommand(
		@NotNull ServerConfig serverConfig,
		@NotNull AbstractCommentedConfig abstractCommentedConfig ) {
		
		return serverConfig.getCommandPrefix() + CommandConfig.getDiscordCommand( abstractCommentedConfig );
	}
	
	@NotNull
	private String buildMinecraftCommand(
		@NotNull AbstractCommentedConfig abstractCommentedConfig,
		@NotNull String discordCommand,
		@NotNull String command ) {
		
		if( CommandConfig.getUseParameter( abstractCommentedConfig ) ) {
			return "/" + CommandConfig.getMinecraftCommand( abstractCommentedConfig ) + command.replaceFirst(
				discordCommand,
				""
			);
		}
		return "/" + CommandConfig.getMinecraftCommand( abstractCommentedConfig );
	}
}
