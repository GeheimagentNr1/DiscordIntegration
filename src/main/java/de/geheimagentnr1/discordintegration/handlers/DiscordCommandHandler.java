package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.commands.discord.*;
import de.geheimagentnr1.discordintegration.commands.discord.dimension_access_manager.DimensionAccessManagerIntegration;
import de.geheimagentnr1.discordintegration.commands.discord.moremobgriefingoptions.MoreMobGriefingOptionsIntegration;
import net.dv8tion.jda.api.entities.Message;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;


public class DiscordCommandHandler {
	
	
	private final static CommandHandler[] command_handlers = init();
	
	private static CommandSource source;
	
	private static CommandHandler[] init() {
		
		ArrayList<CommandHandler> commandHandlers = new ArrayList<>();
		
		commandHandlers.add( new DifficultyCommand() );
		commandHandlers.add( new GamerulesCommand() );
		commandHandlers.add( new HelpCommand() );
		commandHandlers.add( new ModsCommand() );
		commandHandlers.add( new OnlineCommand() );
		commandHandlers.add( new SeedCommand() );
		commandHandlers.add( new TimeCommand() );
		commandHandlers.add( new TpsCommand() );
		DimensionAccessManagerIntegration.registerDiscordCommands( commandHandlers );
		MoreMobGriefingOptionsIntegration.registerDiscordCommands( commandHandlers );
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
		
		source = new CommandSource( new DiscordCommandSource(), Vector3d.ZERO, Vector2f.ZERO,
			server.func_241755_D_(), 2, "Discord Integration Mod",
			new StringTextComponent( "Discord Integration Mod" ), server, null );
	}
	
	public static CommandHandler[] getCommands() {
		
		return command_handlers;
	}
}
