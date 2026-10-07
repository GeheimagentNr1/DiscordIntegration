package de.geheimagentnr1.discordintegration.elements.discord.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import de.geheimagentnr1.discordintegration.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
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
	public void onChannelDelete( @NotNull ChannelDeleteEvent event ) {
		
		if( event.getChannelType() == ChannelType.TEXT &&
			chatManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			chatManager.init();
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
			!serverConfig.isLoaded() ||
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
		
		// Other bots' messages are transmitted, the feedback messages of this mod's own commands are not
		if( serverConfig.getChatConfig().transmitBotMessages() &&
			!discordMessageBuilder.isMessageBotFeedback( message ) ) {
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
			boolean useRawMessageFormat = serverConfig.getChatConfig().useRawMessageFormatDiscordToMinecraft();
			String buildMessage = MessageUtil.replaceParameters(
				serverConfig.getChatConfig().getMessageFormatDiscordToMinecraft(),
				Map.of(
					"username", escapeForRawMessageFormat( useRawMessageFormat, discordManager.getMemberAsTag( member ) ),
					"nickname", escapeForRawMessageFormat( useRawMessageFormat, member.getEffectiveName() ),
					"message", escapeForRawMessageFormat( useRawMessageFormat, message )
				)
			);
			if( useRawMessageFormat ) {
				try {
					// Called via the brigadier interface: ComponentArgument.parse has a different signature since 1.21.5
					ArgumentType<Component> componentArgument = ComponentArgument.textComponent(
						CommandBuildContext.simple(
							server.registryAccess(),
							server.getWorldData().enabledFeatures()
						)
					);
					server.getPlayerList().broadcastSystemMessage(
						componentArgument.parse( new StringReader( buildMessage ) ),
						false
					);
				} catch( CommandSyntaxException exception ) {
					chatManager.sendFeedbackMessage(
						MessageUtil.replaceParameters(
							serverConfig.getChatConfig()
								.getInvalidRawMessageFormatForDiscordToMinecraftErrorMessage(),
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
