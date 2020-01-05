package de.geheimagentnr1.discordintegration.commands.discord;


import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public class OnlineCommand extends CommandHandler {
	
	
	public OnlineCommand() {
		
		super( "online" );
	}
	
	@Override
	public void run( CommandSource source, MinecraftServer server ) {
		
		server.getCommandManager().handleCommand( source, "/list" );
	}
}
