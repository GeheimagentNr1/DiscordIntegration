package de.geheimagentnr1.discordintegration.commands.discord.moremobgriefingoptions;

import de.geheimagentnr1.discordintegration.commands.discord.CommandHandler;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import de.geheimagentnr1.moremobgriefingoptions.config.Config;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.Locale;


public class MobgriefingCommand extends CommandHandler {
	
	
	protected MobgriefingCommand() {
		
		super( "mobgriefing" );
	}
	
	@Override
	protected void run( CommandSource source, MinecraftServer server ) {
		
		StringBuilder message = new StringBuilder();
		
		message.append( String.format( "**Mobgriefing Settings**%n" ) );
		message.append( String.format( "vanilla mobGriefing = %s%n",
			server.getGameRules().getBoolean( GameRules.MOB_GRIEFING ) ) );
		for( int i = 0; i < Config.OPTIONS.length; i++ ) {
			message.append( String.format( "%s = %s%n", Config.OPTIONS[i].getKey(),
				Config.OPTIONS[i].getValue().name().toLowerCase( Locale.ENGLISH ) ) );
		}
		DiscordNet.sendMessage( message.toString() );
	}
	
	@Override
	protected String getDescription() {
		
		return "shows all mobgriefing options of the mobs.";
	}
}
