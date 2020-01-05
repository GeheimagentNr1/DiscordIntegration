package de.geheimagentnr1.discordintegration.commands.discord;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public abstract class CommandHandler {
	
	
	private final String command;
	
	protected CommandHandler( String name ) {
		
		command = "!" + name;
	}
	
	public boolean handleCommand( CommandSource source, String _command, MinecraftServer server ) {
		
		if( command.equals( _command ) ) {
			run( source, server );
			return true;
		}
		return false;
	}
	
	public abstract void run( CommandSource source, MinecraftServer server );
}
