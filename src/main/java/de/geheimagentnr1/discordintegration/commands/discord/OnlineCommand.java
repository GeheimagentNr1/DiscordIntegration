package de.geheimagentnr1.discordintegration.commands.discord;


import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public class OnlineCommand extends CommandHandler {
	
	
	public OnlineCommand() {
		
		super( "online" );
	}
	
	@Override
	protected void run( CommandSource source, MinecraftServer server ) {
		
		server.getCommandManager().handleCommand( source, "/list" );
	}
	
	@Override
	public String getDescription() {
		
		return "shows how many and which players are on the server.";
	}
}
