package de.geheimagentnr1.discordintegration.commands.discord;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.ArrayList;


public class ModsCommand extends CommandHandler {
	
	
	public ModsCommand() {
		
		super( "mods" );
	}
	
	@Override
	protected void run( CommandSource source, MinecraftServer server ) {
		
		ArrayList<String> messages = new ArrayList<>();
		ModList.get().forEachModFile( modFile -> {
			for( IModInfo modInfo : modFile.getModInfos() ) {
				int messagePos = messages.size() - 1;
				String modText = String.format( "%s (%s) - %s", modInfo.getModId(), modInfo.getVersion(),
					modFile.getFileName() );
				if( !messages.isEmpty() && messages.get( messagePos ).length() + modText.length() + 1 < 2000 ) {
					//noinspection HardcodedLineSeparator
					messages.set( messagePos, messages.get( messagePos ) + "\n" + modText );
				} else {
					messages.add( modText );
				}
			}
		} );
		if( messages.size() == 1 ) {
			DiscordNet.sendMessage( "```" + messages.get( 0 ) + "```" );
		} else {
			for( String message : messages ) {
				DiscordNet.sendMessage( message );
			}
		}
	}
	
	@Override
	public String getDescription() {
		
		return "shows a list of the mods on the server.";
	}
}
