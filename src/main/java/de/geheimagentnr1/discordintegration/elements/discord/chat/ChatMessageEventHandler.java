package de.geheimagentnr1.discordintegration.elements.discord.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import de.geheimagentnr1.discordintegration.util.MessageUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;


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
			if( member != null ) {
				if( DiscordCommandHandler.isCommand( message ) ) {
					DiscordCommandHandler.handleCommand( member, message, server, ChatManager::sendFeedbackMessage );
				} else {
					if( beginnsNotWithOtherCommandPrefix( message ) ) {
						handleUserMessage( member, message, server );
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
			server.getPlayerList().broadcastSystemMessage(
				Component.literal( message ),
				false
			);
		}
	}
	
	private void handleUserMessage( Member member, String message, MinecraftServer server ) {
		
		if( ServerConfig.CHAT_CONFIG.getMaxCharCount() == -1 ||
			message.length() <= ServerConfig.CHAT_CONFIG.getMaxCharCount() ) {
			String buildMessage = MessageUtil.replaceParameters(
				ServerConfig.CHAT_CONFIG.getMessageFormatDiscordToMinecraft(),
				Map.of(
					"username", DiscordManager.getMemberAsTag( member ),
					"nickname", member.getEffectiveName(),
					"message", message
				)
			);
			if( ServerConfig.CHAT_CONFIG.useRawMessageFormatDiscordToMinecraft() ) {
				try {
					server.getPlayerList().broadcastSystemMessage(
						ComponentArgument.textComponent().parse( new StringReader( buildMessage ) ),
						false
					);
				} catch( CommandSyntaxException exception ) {
					ChatManager.sendFeedbackMessage(
						MessageUtil.replaceParameters(
							ServerConfig.CHAT_CONFIG.getInvalidRawMessageFormatForDiscordToMinecraftErrorMessage(),
							Map.of(
								"username", DiscordManager.getMemberAsTag( member ),
								"nickname", member.getEffectiveName(),
								"error_message", exception.getMessage(),
								"new_line", System.lineSeparator()
							)
						)
					);
				}
			} else {
				server.getPlayerList().broadcastSystemMessage(
					Component.literal( buildMessage ),
					false
				);
			}
		} else {
			ChatManager.sendFeedbackMessage(
				MessageUtil.replaceParameters(
					ServerConfig.CHAT_CONFIG.getMaxCharCountErrorMessage(),
					Map.of(
						"username", DiscordManager.getMemberAsTag( member ),
						"nickname", member.getEffectiveName(),
						"max_char_count", String.valueOf( ServerConfig.CHAT_CONFIG.getMaxCharCount() ),
						"actual_message_char_count", String.valueOf( message.length() ),
						"new_line", System.lineSeparator()
					)
				)
			);
		}
	}
}
