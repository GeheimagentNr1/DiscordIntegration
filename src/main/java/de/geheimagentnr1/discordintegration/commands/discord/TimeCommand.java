package de.geheimagentnr1.discordintegration.commands.discord;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public class TimeCommand extends CommandHandler {
	
	
	public TimeCommand() {
		
		super( "time" );
	}
	
	@Override
	protected void run( CommandSource source, MinecraftServer server ) {
		
		server.getCommandManager().handleCommand( source, "/time query daytime" );
	}
}
