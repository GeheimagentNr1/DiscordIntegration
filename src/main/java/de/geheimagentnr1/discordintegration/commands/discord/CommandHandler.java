package de.geheimagentnr1.discordintegration.commands.discord;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public abstract class CommandHandler {
	
	
	private final String command;
	
	//package-private
	CommandHandler( String name ) {
		
		command = "!" + name;
	}
	
	public boolean handleCommand( CommandSource source, String _command, MinecraftServer server ) {
		
		if( command.equals( _command ) ) {
			run( source, server );
			return true;
		}
		return false;
	}
	
	protected abstract void run( CommandSource source, MinecraftServer server );
	
	//package-private
	String getCommand() {
		
		return command;
	}
	
	protected abstract String getDescription();
}
