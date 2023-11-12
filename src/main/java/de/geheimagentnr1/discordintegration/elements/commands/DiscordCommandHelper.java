package de.geheimagentnr1.discordintegration.elements.commands;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;


class DiscordCommandHelper {
	
	
	private static void sendErrorMessage( @NotNull CommandSourceStack source, @NotNull String message ) {
		
		source.sendFailure( Component.literal( message ) );
	}
	
	static void sendInvalidCommandConfigurationErrorMessage( @NotNull CommandSourceStack source ) {
		
		sendErrorMessage( source, "Invalid Command Configuration" );
	}
	
	static boolean isDiscordSource( @NotNull CommandSourceStack source ) {
		
		if( source instanceof DiscordCommandSourceStack ) {
			sendInvalidCommandConfigurationErrorMessage( source );
			return true;
		}
		return false;
	}
	
	static void sendInvalidMember( @NotNull ServerConfig serverConfig, @NotNull CommandSourceStack source ) {
		
		sendErrorMessage(
			source,
			serverConfig.getCommandSettingsConfig()
				.getCommandMessagesConfig()
				.getLinkInvalidDiscordMemberIdErrorMessage()
		);
	}
	
	static void sendLinkCommandsUseIfWhitelistIsDisabledErrorMessage(
		@NotNull ServerConfig serverConfig,
		@NotNull CommandSourceStack source ) {
		
		sendErrorMessage(
			source,
			serverConfig.getCommandSettingsConfig()
				.getCommandMessagesConfig()
				.getLinkCommandsUseIfWhitelistIsDisabledErrorMessage()
		);
	}
	
	static void handleError( @NotNull CommandSourceStack source, @NotNull Throwable throwable ) {
		
		sendErrorMessage( source, throwable.getMessage() );
	}
}
