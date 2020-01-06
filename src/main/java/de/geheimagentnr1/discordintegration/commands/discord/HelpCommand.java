package de.geheimagentnr1.discordintegration.commands.discord;

import de.geheimagentnr1.discordintegration.handlers.DiscordCommandHandler;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public class HelpCommand extends CommandHandler {
	
	
	public HelpCommand() {
		
		super( "help" );
	}
	
	@Override
	protected void run( CommandSource source, MinecraftServer server ) {
		
		StringBuilder message = new StringBuilder();
		
		for( CommandHandler commandHandler : DiscordCommandHandler.getCommands() ) {
			//noinspection HardcodedLineSeparator
			message.append( String.format( "%s - %s\n", commandHandler.getCommand(),
				commandHandler.getDescription() ) );
		}
		DiscordNet.sendMessage( message.toString() );
	}
	
	@Override
	public String getDescription() {
		
		return "shows all commands with it's description.";
	}
}
