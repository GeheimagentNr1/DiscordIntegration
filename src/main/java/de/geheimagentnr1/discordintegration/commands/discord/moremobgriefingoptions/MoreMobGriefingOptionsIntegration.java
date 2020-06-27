package de.geheimagentnr1.discordintegration.commands.discord.moremobgriefingoptions;

import de.geheimagentnr1.discordintegration.commands.discord.CommandHandler;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;


public class MoreMobGriefingOptionsIntegration {
	
	
	public static void registerDiscordCommands( ArrayList<CommandHandler> commandHandlers ) {
		
		if( ModList.get().isLoaded( "moremobgriefingoptions" ) ) {
			commandHandlers.add( new MobgriefingCommand() );
		}
	}
}
