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
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
	public void onChannelDelete( @Nonnull ChannelDeleteEvent event ) {
		
		if( event.getChannelType() != ChannelType.TEXT ) {
			return;
		}
		if( ChatManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			ChatManager.init();
		}
	}
	
	@Override
	public void onMessageReceived( @NotNull MessageReceivedEvent event ) {
		
		if( !event.isFromGuild() || event.getChannelType() != ChannelType.TEXT ) {
			return;
		}
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
			!DiscordMessageBuilder.isMessageBotFeedback( message ) ) {
			server.getPlayerList().broadcastSystemMessage(
				Component.literal( message ),
				false
			);
		}
	}
	
	private void handleUserMessage( Member member, String message, MinecraftServer server ) {
		
		if( ServerConfig.CHAT_CONFIG.getMaxCharCount() == -1 ||
			message.length() <= ServerConfig.CHAT_CONFIG.getMaxCharCount() ) {
			boolean useRawMessageFormat = ServerConfig.CHAT_CONFIG.useRawMessageFormatDiscordToMinecraft();
			String buildMessage = MessageUtil.replaceParameters(
				ServerConfig.CHAT_CONFIG.getMessageFormatDiscordToMinecraft(),
				Map.of(
					"username", escapeForRawMessageFormat( useRawMessageFormat, DiscordManager.getMemberAsTag( member ) ),
					"nickname", escapeForRawMessageFormat( useRawMessageFormat, member.getEffectiveName() ),
					"message", escapeForRawMessageFormat( useRawMessageFormat, message )
				)
			);
			if( useRawMessageFormat ) {
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

	//With the raw message format the values from Discord are inserted into a text component (JSON, SNBT since 1.21.5),
	//so they must not be able to add own elements, for example click events running commands.
	//Unicode escapes work in both formats and in single and double quoted strings.
	@NotNull
	private static String escapeForRawMessageFormat( boolean useRawMessageFormat, @NotNull String value ) {

		if( !useRawMessageFormat ) {
			return value;
		}
		StringBuilder escaped = new StringBuilder( value.length() );
		for( char character : value.toCharArray() ) {
			switch( character ) {
				case '\\' -> escaped.append( "\\u005c" );
				case '"' -> escaped.append( "\\u0022" );
				case '\'' -> escaped.append( "\\u0027" );
				case '\n' -> escaped.append( "\\n" );
				case '\r' -> escaped.append( "\\r" );
				case '\t' -> escaped.append( "\\t" );
				default -> escaped.append( character );
			}
		}
		return escaped.toString();
	}
}
