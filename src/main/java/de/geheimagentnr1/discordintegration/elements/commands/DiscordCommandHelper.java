package de.geheimagentnr1.discordintegration.elements.commands;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;


//package-private
class DiscordCommandHelper {
	
	
	private static void sendErrorMessage( CommandSourceStack source, String message ) {
		
		source.sendFailure( new TextComponent( message ) );
	}
	
	static void sendInvalidCommandConfigurationErrorMessage( CommandSourceStack source ) {
		
		sendErrorMessage( source, "Invalid Command Configuration" );
	}
	
	static boolean isDiscordSource( CommandSourceStack source ) {
		
		if( source instanceof DiscordCommandSourceStack ) {
			sendInvalidCommandConfigurationErrorMessage( source );
			return true;
		}
		return false;
	}
	
	static void sendInvalidMember( CommandSourceStack source ) {
		
		sendErrorMessage(
			source,
			ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandMessagesConfig().getLinkInvalidDiscordMemberIdErrorMessage()
		);
	}
	
	static void sendLinkCommandsUseIfWhitelistIsDisabledErrorMessage( CommandSourceStack source ) {
		
		sendErrorMessage( source,
			ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandMessagesConfig()
				.getLinkCommandsUseIfWhitelistIsDisabledErrorMessage()
		);
	}
	
	static void handleError( CommandSourceStack source, Throwable throwable ) {
		
		sendErrorMessage( source, throwable.getMessage() );
	}
}
