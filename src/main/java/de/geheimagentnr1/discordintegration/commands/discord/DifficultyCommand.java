package de.geheimagentnr1.discordintegration.commands.discord;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public class DifficultyCommand extends CommandHandler {
	
	
	public DifficultyCommand() {
		
		super( "difficulty" );
	}
	
	@Override
	public void run( CommandSource source, MinecraftServer server ) {
		
		server.getCommandManager().handleCommand( source, "/difficulty" );
	}
}
