package de.geheimagentnr1.discordintegration.elements.discord.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import de.geheimagentnr1.minecraft_forge_api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
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

import java.util.Map;


@RequiredArgsConstructor
public class ChatMessageEventHandler extends ListenerAdapter {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	private final DiscordManager discordManager;
	
	@NotNull
	private final ChatManager chatManager;
	
	@NotNull
	private final DiscordCommandHandler discordCommandHandler;
	
	@NotNull
	private final DiscordMessageBuilder discordMessageBuilder;
	
	@Override
	public void onTextChannelDelete( @NotNull TextChannelDeleteEvent event ) {
		
		if( chatManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			chatManager.init();
		}
	}
	
	@Override
	public void onGuildMessageReceived( @NotNull GuildMessageReceivedEvent event ) {
		
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		User author = event.getAuthor();
		
		if( server == null ||
			!chatManager.isCorrectChannel( event.getChannel().getIdLong() ) ||
			author.getIdLong() == discordManager.getSelfUser().getIdLong() ) {
			return;
		}
		
		Member member = event.getMember();
		String message = event.getMessage().getContentDisplay();
		
		if( author.isBot() ) {
			handleBotMessage( message, server );
		} else {
			if( member != null ) {
				if( discordCommandHandler.isCommand( message ) ) {
					discordCommandHandler.handleCommand( member, message, server, chatManager::sendFeedbackMessage );
				} else {
					if( beginnsNotWithOtherCommandPrefix( message ) ) {
						handleUserMessage( member, message, server );
					}
				}
			}
		}
	}
	
	private boolean beginnsNotWithOtherCommandPrefix( @NotNull String message ) {
		
		return serverConfig.getCommandSettingsConfig().getOtherBotsCommandPrefixes()
			.stream()
			.noneMatch( message::startsWith );
	}
	
	private void handleBotMessage( @NotNull String message, @NotNull MinecraftServer server ) {
		
		if( serverConfig.getChatConfig().transmitBotMessages() &&
			discordMessageBuilder.isMessageBotFeedback( message ) ) {
			server.getPlayerList().broadcastSystemMessage(
				Component.literal( message ),
				false
			);
		}
	}
	
	private void handleUserMessage(
		@NotNull Member member,
		@NotNull String message,
		@NotNull MinecraftServer server ) {
		
		if( serverConfig.getChatConfig().getMaxCharCount() == -1 ||
			message.length() <= serverConfig.getChatConfig().getMaxCharCount() ) {
			String buildMessage = MessageUtil.replaceParameters(
				serverConfig.getChatConfig().getMessageFormatDiscordToMinecraft(),
				Map.of(
					"username", discordManager.getMemberAsTag( member ),
					"nickname", member.getEffectiveName(),
					"message", message
				)
			);
			if( serverConfig.getChatConfig().useRawMessageFormatDiscordToMinecraft() ) {
				try {
					server.getPlayerList().broadcastSystemMessage(
						ComponentArgument.textComponent().parse( new StringReader( buildMessage ) ),
						false
					);
				} catch( CommandSyntaxException exception ) {
					chatManager.sendFeedbackMessage(
						MessageUtil.replaceParameters(
							serverConfig.getChatConfig().getInvalidRawMessageFormatForDiscordToMinecraftErrorMessage(),
							Map.of(
								"username", discordManager.getMemberAsTag( member ),
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
			chatManager.sendFeedbackMessage(
				MessageUtil.replaceParameters(
					serverConfig.getChatConfig().getMaxCharCountErrorMessage(),
					Map.of(
						"username", discordManager.getMemberAsTag( member ),
						"nickname", member.getEffectiveName(),
						"max_char_count", String.valueOf( serverConfig.getChatConfig().getMaxCharCount() ),
						"actual_message_char_count", String.valueOf( message.length() ),
						"new_line", System.lineSeparator()
					)
				)
			);
		}
	}
}
