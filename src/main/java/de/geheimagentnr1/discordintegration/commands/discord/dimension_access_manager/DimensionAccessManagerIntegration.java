package de.geheimagentnr1.discordintegration.commands.discord.dimension_access_manager;

import de.geheimagentnr1.discordintegration.commands.discord.CommandHandler;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;


public class DimensionAccessManagerIntegration {
    
    
    public static void registerDiscordCommands( ArrayList<CommandHandler> commandHandlers ) {
    
        if( ModList.get().isLoaded( "dimension_access_manager" ) ) {
            commandHandlers.add( new DimensionsCommand() );
        }
    }
}
