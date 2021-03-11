package de.geheimagentnr1.discordintegration.elements.discord;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import de.geheimagentnr1.discordintegration.config.CommandConfig;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.Objects;


//package-private
class DiscordCommandHandler {
	
	
	private static final String MOD_NAME = ModLoadingContext.get().getActiveContainer().getModInfo().getDisplayName();
	
	//package-private
	static boolean handleCommand( String command, MinecraftServer server ) {
		
		DiscordCommandSource discordCommandSource = new DiscordCommandSource();
		CommandSource source = buildSource( server, discordCommandSource );
		for( AbstractCommentedConfig abstractCommentedConfig : ServerConfig.getCommands() ) {
			if( CommandConfig.getEnabled( abstractCommentedConfig ) &&
				buildDiscordCommand( abstractCommentedConfig ).equals( command ) ) {
				server.getCommandManager().handleCommand( source, buildMinecraftCommand( abstractCommentedConfig ) );
				discordCommandSource.sendMessage();
				return true;
			}
		}
		return false;
	}
	
	private static CommandSource buildSource( MinecraftServer server, DiscordCommandSource discordCommandSource ) {
		
		return new CommandSource(
			discordCommandSource,
			Vector3d.ZERO,
			Vector2f.ZERO,
			Objects.requireNonNull( server.getWorld( World.OVERWORLD ) ),
			4,
			MOD_NAME,
			new StringTextComponent( MOD_NAME ),
			server,
			null
		);
	}
	
	private static String buildDiscordCommand( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return ServerConfig.getCommandPrefix() + CommandConfig.getDiscordCommand( abstractCommentedConfig );
	}
	
	private static String buildMinecraftCommand( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return "/" + CommandConfig.getMinecraftCommand( abstractCommentedConfig );
	}
}
