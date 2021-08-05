package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;


public class DiscordEventHandler extends ListenerAdapter {
	
	
	private static MinecraftServer server;
	
	public static void setServer( MinecraftServer _server ) {
		
		server = _server;
	}
	
	@Override
	public void onGuildMessageReceived( @Nonnull GuildMessageReceivedEvent event ) {
		
		User author = event.getAuthor();
		Member member = event.getMember();
		if( isInitialized() && DiscordNet.feedBackAllowed( event.getChannel(), author ) ) {
			String message = event.getMessage().getContentDisplay();
			if( author.isBot() ) {
				handleBotMessage( message );
			} else {
				if( message.startsWith( ServerConfig.getCommandPrefix() ) ) {
					handleCommands( author, message );
				} else {
					if( beginnsNotWithOtherCommandPrefix( message ) ) {
						if( ServerConfig.isUseNickname() && member != null ) {
							handleUserMessage( member.getEffectiveName(), message );
						} else {
							handleUserMessage( author.getName(), message );
						}
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
				server.getPlayerList().broadcastMessage(
					new TextComponent( message ),
					ChatType.CHAT,
					Util.NIL_UUID
				);
			}
		}
	}
	
	private void handleUserMessage( String author, String message ) {
		
		if( ServerConfig.getMaxCharCount() == -1 || message.length() <= ServerConfig.getMaxCharCount() ) {
			server.getPlayerList().broadcastMessage(
				new TextComponent( String.format(
					"[%s] %s",
					author,
					message
				) ),
				ChatType.CHAT,
				Util.NIL_UUID
			);
		} else {
			DiscordNet.sendFeedbackMessage( String.format(
				"%n%s%nError: Message to long.%n" +
					"Messages can only be up to %d characters long.%n" +
					"Your message is %d characters long.",
				author,
				ServerConfig.getMaxCharCount(),
				message.length()
			) );
		}
	}
}
