package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.commands.discord.*;
import net.dv8tion.jda.api.entities.Message;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;


public class DiscordCommandHandler {
	
	
	private final static CommandHandler[] command_handlers = init();
	
	private static CommandSource source;
	
	private static CommandHandler[] init() {
		
		ArrayList<CommandHandler> commandHandlers = new ArrayList<>();
		
		commandHandlers.add( new DifficultyCommand() );
		commandHandlers.add( new GamerulesCommand() );
		commandHandlers.add( new ModsCommand() );
		commandHandlers.add( new OnlineCommand() );
		commandHandlers.add( new SeedCommand() );
		commandHandlers.add( new TimeCommand() );
		commandHandlers.add( new TpsCommand() );
		return commandHandlers.toArray( new CommandHandler[0] );
	}
	
	public static boolean handleCommand( Message message, MinecraftServer server ) {
		
		String command = message.getContentRaw();
		for( CommandHandler command_handler : command_handlers ) {
			if( command_handler.handleCommand( source, command, server ) ) {
				return true;
			}
		}
		return false;
	}
	
	//package-private
	static void initSource( MinecraftServer server ) {
		
		source = new CommandSource( new DiscordCommandSource(), Vec3d.ZERO, Vec2f.ZERO,
			server.getWorld( DimensionType.OVERWORLD ), 2, "Discord Integration Mod",
			new StringTextComponent( "Discord Integration Mod" ), server, null );
	}
}
