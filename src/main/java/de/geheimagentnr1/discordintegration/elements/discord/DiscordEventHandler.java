package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class DiscordEventHandler extends ListenerAdapter {
	
	
	@NotNull
	private final DiscordCommandHandler discordCommandHandler = new DiscordCommandHandler();
	
	@NotNull
	private final AbstractMod abstractMod;
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	private final DiscordNet discordNet;
	
	private MinecraftServer server() {
		
		return ServerLifecycleHooks.getCurrentServer();
	}
	
	@Override
	public void onGuildMessageReceived( @NotNull GuildMessageReceivedEvent event ) {
		
		User author = event.getAuthor();
		Member member = event.getMember();
		if( isInitialized() && discordNet.feedBackAllowed( event.getChannel(), author ) ) {
			String message = event.getMessage().getContentDisplay();
			if( author.isBot() ) {
				handleBotMessage( message );
			} else {
				if( message.startsWith( serverConfig.getCommandPrefix() ) ) {
					handleCommands( author, message );
				} else {
					if( beginnsNotWithOtherCommandPrefix( message ) ) {
						if( serverConfig.isUseNickname() && member != null ) {
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
		
		return server() != null && discordNet.isInitialized();
	}
	
	private boolean beginnsNotWithOtherCommandPrefix( @NotNull String message ) {
		
		for( String prefix : serverConfig.getOtherBotsCommandPrefixes() ) {
			if( message.startsWith( prefix ) ) {
				return false;
			}
		}
		return true;
	}
	
	private void handleCommands( @NotNull User author, @NotNull String command ) {
		
		if( !author.isBot() ) {
			if( !discordCommandHandler.handleCommand( command, abstractMod, serverConfig, discordNet, server() ) ) {
				discordNet.sendFeedbackMessage( String.format( "%n%s%nError: Unknown Command", author.getName() ) );
			}
		}
	}
	
	private void handleBotMessage( @NotNull String message ) {
		
		if( serverConfig.isTransmitBotMessages() ) {
			if( !message.startsWith( DiscordNet.FEEDBACK_START ) || !message.endsWith( DiscordNet.FEEDBACK_END ) ) {
				server().getPlayerList().broadcastSystemMessage(
					Component.literal( message ),
					false
				);
			}
		}
	}
	
	private void handleUserMessage( @NotNull String author, @NotNull String message ) {
		
		if( serverConfig.getMaxCharCount() == -1 || message.length() <= serverConfig.getMaxCharCount() ) {
			server().getPlayerList().broadcastSystemMessage(
				Component.literal( String.format(
					"[%s] %s",
					author,
					message
				) ),
				false
			);
		} else {
			discordNet.sendFeedbackMessage( String.format(
				"%n%s%nError: Message to long.%n" +
					"Messages can only be up to %d characters long.%n" +
					"Your message is %d characters long.",
				author,
				serverConfig.getMaxCharCount(),
				message.length()
			) );
		}
	}
}
