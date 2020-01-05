package de.geheimagentnr1.discordintegration.commands.discord;


import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public class TpsCommand extends CommandHandler {
	
	
	public TpsCommand() {
		
		super( "tps" );
	}
	
	@Override
	public void run( CommandSource source, MinecraftServer server ) {
		
		server.getCommandManager().handleCommand( source, "/forge tps" );
	}
}
