package de.geheimagentnr1.discordintegration.commands.discord;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;


public class SeedCommand extends CommandHandler {
	
	
	public SeedCommand() {
		
		super( "seed" );
	}
	
	@Override
	protected void run( CommandSource source, MinecraftServer server ) {
		
		DiscordNet.sendMessage( "Seed: " + source.func_197023_e().getSeed() );
	}
	
	@Override
	public String getDescription() {
		
		return "shows the seed of the active world.";
	}
}
