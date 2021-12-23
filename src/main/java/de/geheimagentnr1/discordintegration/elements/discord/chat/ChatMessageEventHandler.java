package de.geheimagentnr1.discordintegration.elements.discord.chat;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class ChatMessageEventHandler extends ListenerAdapter {
	
	
	@Override
	public void onTextChannelDelete( @Nonnull TextChannelDeleteEvent event ) {
		
		if( ChatManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			ChatManager.init();
		}
	}
	
	@Override
	public void onGuildMessageReceived( @NotNull GuildMessageReceivedEvent event ) {
		
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		User author = event.getAuthor();
		
		if( server == null ||
			!ChatManager.isCorrectChannel( event.getChannel().getIdLong() ) ||
			author.getIdLong() == DiscordManager.getSelfUser().getIdLong() ) {
			return;
		}
		
		Member member = event.getMember();
		String message = event.getMessage().getContentDisplay();
		
		if( author.isBot() ) {
			handleBotMessage( message, server );
		} else {
			if( DiscordCommandHandler.isCommand( message ) ) {
				DiscordCommandHandler.handleCommand( member, message, server, ChatManager::sendFeedbackMessage );
			} else {
				if( beginnsNotWithOtherCommandPrefix( message ) ) {
					if( ServerConfig.CHAT_CONFIG.useNickname() && member != null ) {
						handleUserMessage( member.getEffectiveName(), message, server );
					} else {
						handleUserMessage( author.getName(), message, server );
					}
				}
			}
		}
	}
	
	private boolean beginnsNotWithOtherCommandPrefix( String message ) {
		
		return ServerConfig.COMMAND_SETTINGS_CONFIG.getOtherBotsCommandPrefixes()
			.stream()
			.noneMatch( message::startsWith );
	}
	
	private void handleBotMessage( String message, MinecraftServer server ) {
		
		if( ServerConfig.CHAT_CONFIG.transmitBotMessages() &&
			DiscordMessageBuilder.isMessageBotFeedback( message ) ) {
			server.getPlayerList().broadcastMessage(
				new TextComponent( message ),
				ChatType.CHAT,
				Util.NIL_UUID
			);
		}
	}
	
	private void handleUserMessage( String author, String message, MinecraftServer server ) {
		
		if( ServerConfig.CHAT_CONFIG.getMaxCharCount() == -1 ||
			message.length() <= ServerConfig.CHAT_CONFIG.getMaxCharCount() ) {
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
			ChatManager.sendFeedbackMessage( String.format(
				"%n%s%nError: Message to long.%n" +
					"Messages can only be up to %d characters long.%n" +
					"Your message is %d characters long.",
				author,
				ServerConfig.CHAT_CONFIG.getMaxCharCount(),
				message.length()
			) );
		}
	}
}
