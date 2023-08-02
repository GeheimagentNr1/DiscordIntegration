package de.geheimagentnr1.discordintegration.elements.commands;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;


//package-private
class DiscordCommandHelper {
	
	
	static void sendInvalidCommandConfigurationErrorMessage( CommandSourceStack source ) {
		
		source.sendFailure( new TextComponent( "Invalid Command Configuration" ) );
	}
	
	//package-private
	static boolean isDiscordSource( CommandSourceStack source ) {
		
		if( source instanceof DiscordCommandSourceStack ) {
			sendInvalidCommandConfigurationErrorMessage( source );
			return true;
		}
		return false;
	}
	
	//package-private
	static void sendInvalidMember( CommandSourceStack source ) {
		
		source.sendFailure( new TextComponent(
			ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandMessagesConfig().getLinkInvalidDiscordMemberIdErrorMessage()
		) );
	}
	
	//package-private
	static void handleError( CommandSourceStack source, Throwable throwable ) {
		
		source.sendFailure( new TextComponent( throwable.getMessage() ) );
	}
}
