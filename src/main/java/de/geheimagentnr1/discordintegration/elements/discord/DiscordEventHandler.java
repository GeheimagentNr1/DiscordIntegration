package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;


public class DiscordEventHandler extends ListenerAdapter {
	
	
	private static MinecraftServer server;
	
	public static void setServer( MinecraftServer _server ) {
		
		server = _server;
	}
	
	@Override
	public void onGuildMessageReceived( @NotNull GuildMessageReceivedEvent event ) {
		
		User author = event.getAuthor();
		if( isInitialized() && DiscordNet.feedBackAllowed( event.getChannel(), author ) ) {
			String message = event.getMessage().getContentDisplay();
			if( author.isBot() ) {
				handleBotMessage( message );
			} else {
				if( message.startsWith( ServerConfig.getCommandPrefix() ) ) {
					handleCommands( author, message );
				} else {
					if( beginnsNotWithOtherCommandPrefix( message ) ) {
						handleUserMessage( message, author );
					}
				}
			}
		}
	}
	
	private boolean isInitialized() {
		
		return server != null && DiscordNet.isInitialized();
	}
	
	private boolean beginnsNotWithOtherCommandPrefix( String message ) {
		
		for( String prefix : ServerConfig.getOtherBotsCommandPrefixes() ) {
			if( message.startsWith( prefix ) ) {
				return false;
			}
		}
		return true;
	}
	
	private void handleCommands( User author, String command ) {
		
		if( !author.isBot() ) {
			if( !DiscordCommandHandler.handleCommand( command, server ) ) {
				DiscordNet.sendFeedbackMessage( String.format( "%n%s%nError: Unknown Command", author.getName() ) );
			}
		}
	}
	
	private void handleBotMessage( String message ) {
		
		if( ServerConfig.isTransmitBotMessages() ) {
			if( !message.startsWith( DiscordNet.FEEDBACK_START ) || !message.endsWith( DiscordNet.FEEDBACK_END ) ) {
				server.getPlayerList().sendMessage( new StringTextComponent( message ), true );
			}
		}
	}
	
	private void handleUserMessage( String message, User author ) {
		
		if( ServerConfig.getMaxCharCount() == -1 || message.length() <= ServerConfig.getMaxCharCount() ) {
			server.getPlayerList().sendMessage(
				new StringTextComponent( String.format(
					"[%s] %s",
					author.getName(),
					message
				) ),
				true
			);
		} else {
			DiscordNet.sendFeedbackMessage( String.format(
				"%n%s%nError: Message to long.%n" +
					"Messages can only be up to %d characters long.%n" +
					"Your message is %d characters long.",
				author.getName(),
				ServerConfig.getMaxCharCount(),
				message.length()
			) );
		}
	}
}
