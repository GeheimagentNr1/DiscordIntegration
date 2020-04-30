package de.geheimagentnr1.discordintegration.commands.discord.dimension_access_manager;

import de.geheimagentnr1.discordintegration.commands.discord.CommandHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public class DimensionsCommand extends CommandHandler {
	
	
	//package-private
	DimensionsCommand() {
		
		super( "dimensions" );
	}
	
	@Override
	protected void run( CommandSource source, MinecraftServer server ) {
		
		server.getCommandManager().handleCommand( source, "dimensions_status" );
	}
	
	@Override
	protected String getDescription() {
		
		return "shows the access states of all dimensions.";
	}
}
