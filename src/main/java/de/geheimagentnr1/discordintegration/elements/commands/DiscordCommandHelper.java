package de.geheimagentnr1.discordintegration.elements.commands;

import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;


//package-private
class DiscordCommandHelper {
	
	
	//package-private
	static boolean isNotDiscordSource( CommandSourceStack source ) {
		
		if( source instanceof DiscordCommandSourceStack ) {
			source.sendFailure( new TextComponent( "Invalid Command Configuration" ) );
			return false;
		}
		return true;
	}
	
	//package-private
	static void sendInvalidMember( CommandSourceStack source ) {
		
		source.sendFailure( new TextComponent( "Discord Member does not exists or Discord context unloadable" ) );//TODO
	}
	
	//package-private
	static void handleError( CommandSourceStack source, Throwable throwable ) {
		
		source.sendFailure( new TextComponent( throwable.getMessage() ) );
	}
}
