package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class ChatConfig extends AbstractSubConfig {
	
	
	@NotNull
	private static final String ENABLED_KEY = "enabled";
	
	@NotNull
	private static final String CHANNEL_ID_KEY = "channel_id";
	
	@NotNull
	private static final String USE_RAW_MESSAGE_FORMAT_DISCORD_TO_MINECRAFT_KEY =
		"use_raw_message_format_discord_to_minecraft";
	
	@NotNull
	private static final String INVALID_RAW_MESSAGE_FORMAT_FOR_DISCORD_TO_MINECRAFT_ERROR_MESSAGE_KEY =
		"invalid_raw_message_format_for_discord_to_minecraft_error_message";
	
	@NotNull
	private static final String MESSAGE_FORMAT_DISCORD_TO_MINECRAFT_KEY = "message_format_discord_to_minecraft";
	
	@NotNull
	private static final String MESSAGE_FORMAT_MINECRAFT_TO_DISCORD_KEY = "message_format_minecraft_to_discord";
	
	@NotNull
	private static final String MESSAGE_FORMAT_MINECRAFT_TO_DISCORD_ME_MESSAGE_KEY =
		"message_format_minecraft_to_discord_me_message";
	
	@NotNull
	private static final String MAX_CHAR_COUNT_KEY = "max_char_count";
	
	@NotNull
	private static final String MAX_CHAR_COUNT_ERROR_MESSAGE_KEY = "max_char_count_error_message";
	
	@NotNull
	private static final String TRANSMIT_BOT_MESSAGES_KEY = "transmit_bot_messages";
	
	@NotNull
	private static final String SUPPRESS_SERVER_MESSAGES_KEY = "suppress_server_messages";
	
	@NotNull
	private static final String CHAT_MESSAGES_CONFIG_KEY = "messages";
	
	ChatConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( "Should the Minecraft and Discord chat be linked?", ENABLED_KEY, false );
		registerConfigValue(
			"Channel ID where the Minecraft and Discord chat will be linked.",
			CHANNEL_ID_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, Long.MAX_VALUE )
		);
		registerConfigValue(
			"Use raw message format for Discord to Minecraft messages " +
				"(examples: https://minecraft.fandom.com/wiki/Commands/tellraw#Examples)?",
			USE_RAW_MESSAGE_FORMAT_DISCORD_TO_MINECRAFT_KEY,
			false
		);
		registerConfigValue(
			"Error message shown in Discord, if the raw message format is malformed JSON. " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%error_message% = Error message, " +
				"%new_line% = New line)",
			INVALID_RAW_MESSAGE_FORMAT_FOR_DISCORD_TO_MINECRAFT_ERROR_MESSAGE_KEY,
			"%username%%new_line%Error: Invalid JSON format for raw message for Discord to Minecraft message" +
				".%new_line%%error_message%"
		);
		registerConfigValue(
			"Message format for Discord to Minecraft messages. " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%message% = Message)",
			MESSAGE_FORMAT_DISCORD_TO_MINECRAFT_KEY,
			"[%username%] %message%"
		);
		registerConfigValue(
			"Message format for Minecraft to Discord messages. " +
				"(Available parameters: %player% = Player name, " +
				"%message% = Message)",
			MESSAGE_FORMAT_MINECRAFT_TO_DISCORD_KEY,
			"**%player%** %message%"
		);
		registerConfigValue(
			"Message format for Minecraft's /me command to Discord messages. " +
				"(Available parameters: %player% = Player name, " +
				"%message% = Message)", MESSAGE_FORMAT_MINECRAFT_TO_DISCORD_ME_MESSAGE_KEY,
			"**%player%** *%message%*"
		);
		registerConfigValue(
			"How long should Discord messages sent to Minecraft chat be at most? " +
				"If the value is -1, there is no limit to the length.",
			MAX_CHAR_COUNT_KEY,
			( builder, path ) -> builder.defineInRange( path, -1, -1, 2000 )
		);
		registerConfigValue(
			"Error message shown in Discord, if the message is too long. " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%max_char_count% = Max message length (value of " + getFullPath( MAX_CHAR_COUNT_KEY ) +
				"), " +
				"%actual_message_char_count% = Actual length of the send message, " +
				"%new_line% = New line)",
			MAX_CHAR_COUNT_ERROR_MESSAGE_KEY,
			"%username%%new_line%Error: Message too long.%new_line%" +
				"Messages can only be up to %max_char_count% characters long.%new_line%" +
				"Your message is %actual_message_char_count% characters long."
		);
		registerConfigValue(
			"Should messages of other bots be sent to the Minecraft chat?",
			TRANSMIT_BOT_MESSAGES_KEY,
			false
		);
		registerConfigValue(
			"Suppress messages sent from the Minecraft server " +
				"(for example, with the say command)?",
			SUPPRESS_SERVER_MESSAGES_KEY,
			false
		);
		registerSubConfig(
			"Messages shown on Discord in the chat channel",
			CHAT_MESSAGES_CONFIG_KEY,
			new ChatMessagesConfig( abstractMod )
		);
	}
	
	public boolean isEnabled() {
		
		return getValue( Boolean.class, ENABLED_KEY );
	}
	
	public long getChannelId() {
		
		return getValue( Long.class, CHANNEL_ID_KEY );
	}
	
	public boolean useRawMessageFormatDiscordToMinecraft() {
		
		return getValue( Boolean.class, USE_RAW_MESSAGE_FORMAT_DISCORD_TO_MINECRAFT_KEY );
	}
	
	@NotNull
	public String getInvalidRawMessageFormatForDiscordToMinecraftErrorMessage() {
		
		return getValue( String.class, INVALID_RAW_MESSAGE_FORMAT_FOR_DISCORD_TO_MINECRAFT_ERROR_MESSAGE_KEY );
	}
	
	@NotNull
	public String getMessageFormatDiscordToMinecraft() {
		
		return getValue( String.class, MESSAGE_FORMAT_DISCORD_TO_MINECRAFT_KEY );
	}
	
	@NotNull
	public String getMessageFormatMinecraftToDiscord() {
		
		return getValue( String.class, MESSAGE_FORMAT_MINECRAFT_TO_DISCORD_KEY );
	}
	
	@NotNull
	public String getMessageFormatMinecraftToDiscordMeMessage() {
		
		return getValue( String.class, MESSAGE_FORMAT_MINECRAFT_TO_DISCORD_ME_MESSAGE_KEY );
	}
	
	public int getMaxCharCount() {
		
		return getValue( Integer.class, MAX_CHAR_COUNT_KEY );
	}
	
	@NotNull
	public String getMaxCharCountErrorMessage() {
		
		return getValue( String.class, MAX_CHAR_COUNT_ERROR_MESSAGE_KEY );
	}
	
	public boolean transmitBotMessages() {
		
		return getValue( Boolean.class, TRANSMIT_BOT_MESSAGES_KEY );
	}
	
	public boolean suppressServerMessages() {
		
		return getValue( Boolean.class, SUPPRESS_SERVER_MESSAGES_KEY );
	}
	
	@NotNull
	public ChatMessagesConfig getChatMessagesConfig() {
		
		return getSubConfig( ChatMessagesConfig.class, CHAT_MESSAGES_CONFIG_KEY );
	}
}
