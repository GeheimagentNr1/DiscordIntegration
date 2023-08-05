package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;


public class ChatConfig {
	
	
	private final ForgeConfigSpec.BooleanValue enabled;
	
	private final ForgeConfigSpec.LongValue channel_id;
	
	private final ForgeConfigSpec.BooleanValue use_raw_message_format_discord_to_minecraft;
	
	private final ForgeConfigSpec.ConfigValue<String> invalid_raw_message_format_for_discord_to_minecraft_error_message;
	
	private final ForgeConfigSpec.ConfigValue<String> message_format_discord_to_minecraft;
	
	private final ForgeConfigSpec.ConfigValue<String> message_format_minecraft_to_discord;
	
	private final ForgeConfigSpec.ConfigValue<String> message_format_minecraft_to_discord_me_message;
	
	private final ForgeConfigSpec.IntValue max_char_count;
	
	private final ForgeConfigSpec.ConfigValue<String> max_char_count_error_message;
	
	private final ForgeConfigSpec.BooleanValue transmit_bot_messages;
	
	private final ForgeConfigSpec.BooleanValue suppress_server_messages;
	
	private final ChatMessagesConfig chat_messages_config;
	
	//package-private
	ChatConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Chat channel settings" )
			.push( "chat" );
		enabled = builder.comment( "Should the Minecraft and Discord chat be linked?" )
			.define( "enabled", false );
		channel_id = builder.comment( "Channel ID where the Minecraft and Discord chat will be linked." )
			.defineInRange( "channel_id", 0, 0, Long.MAX_VALUE );
		use_raw_message_format_discord_to_minecraft = builder.comment(
				"Use raw message format for Discord to Minecraft messages " +
					"(examples: https://minecraft.fandom.com/wiki/Commands/tellraw#Examples)?" )
			.define( "use_raw_message_format_discord_to_minecraft", false );
		invalid_raw_message_format_for_discord_to_minecraft_error_message = builder.comment(
				"Error message shown in Discord, if the raw message format is malformed JSON. " +
					"(Available parameters: %username% = Discord username, " +
					"%nickname% = Discord nickname, " +
					"%error_message% = Error message, " +
					"%new_line% = New line)" )
			.define(
				"invalid_raw_message_format_for_discord_to_minecraft_error_message",
				"%username%%new_line%Error: Invalid JSON format for raw message for Discord to Minecraft message" +
					".%new_line%%error_message%"
			);
		message_format_discord_to_minecraft = builder.comment(
				"Message format for Discord to Minecraft messages. " +
					"(Available parameters: %username% = Discord username, " +
					"%nickname% = Discord nickname, " +
					"%message% = Message)" )
			.define( "message_format_discord_to_minecraft", "[%username%] %message%" );
		message_format_minecraft_to_discord = builder.comment(
				"Message format for Minecraft to Discord messages. " +
					"(Available parameters: %player% = Player name, " +
					"%message% = Message)" )
			.define( "message_format_minecraft_to_discord", "**%player%** %message%" );
		message_format_minecraft_to_discord_me_message = builder.comment(
				"Message format for Minecraft's /me command to Discord messages. " +
					"(Available parameters: %player% = Player name, " +
					"%message% = Message)" )
			.define( "message_format_minecraft_to_discord_me_message", "**%player%** *%message%*" );
		max_char_count = builder.comment( "How long should Discord messages sent to Minecraft chat be at most? " +
				"If the value is -1, there is no limit to the length." )
			.defineInRange( "max_char_count", -1, -1, 2000 );
		max_char_count_error_message = builder.comment( "Error message shown in Discord, if the message is too long." +
				" " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%max_char_count% = Max message length (value of " + String.join( ".", max_char_count.getPath() ) + ")" +
				", " +
				"%actual_message_char_count% = Actual length of the send message, " +
				"%new_line% = New line)" )
			.define( "max_char_count_error_message", "%username%%new_line%Error: Message too long.%new_line%" +
				"Messages can only be up to %max_char_count% characters long.%new_line%" +
				"Your message is %actual_message_char_count% characters long." );
		transmit_bot_messages = builder.comment( "Should messages of other bots be sent to the Minecraft chat?" )
			.define( "transmit_bot_messages", false );
		suppress_server_messages = builder.comment( "Suppress messages sent from the Minecraft server " +
				"(for example, with the say command)?" )
			.define( "suppress_server_messages", false );
		chat_messages_config = new ChatMessagesConfig( builder );
		builder.pop();
	}
	
	public boolean isEnabled() {
		
		return enabled.get();
	}
	
	public long getChannelId() {
		
		return channel_id.get();
	}
	
	public boolean useRawMessageFormatDiscordToMinecraft() {
		
		return use_raw_message_format_discord_to_minecraft.get();
	}
	
	public String getInvalidRawMessageFormatForDiscordToMinecraftErrorMessage() {
		
		return invalid_raw_message_format_for_discord_to_minecraft_error_message.get();
	}
	
	public String getMessageFormatDiscordToMinecraft() {
		
		return message_format_discord_to_minecraft.get();
	}
	
	public String getMessageFormatMinecraftToDiscord() {
		
		return message_format_minecraft_to_discord.get();
	}
	
	public String getMessageFormatMinecraftToDiscordMeMessage() {
		
		return message_format_minecraft_to_discord_me_message.get();
	}
	
	public int getMaxCharCount() {
		
		return max_char_count.get();
	}
	
	public String getMaxCharCountErrorMessage() {
		
		return max_char_count_error_message.get();
	}
	
	public boolean transmitBotMessages() {
		
		return transmit_bot_messages.get();
	}
	
	public boolean suppressServerMessages() {
		
		return suppress_server_messages.get();
	}
	
	public ChatMessagesConfig getChatMessagesConfig() {
		
		return chat_messages_config;
	}
	
	//package-private
	@SuppressWarnings( "DuplicatedCode" )
	void printConfig( Logger logger ) {
		
		logger.info( "{} = {}", enabled.getPath(), enabled.get() );
		logger.info( "{} = {}", channel_id.getPath(), channel_id.get() );
		logger.info(
			"{} = {}",
			use_raw_message_format_discord_to_minecraft.getPath(),
			use_raw_message_format_discord_to_minecraft.get()
		);
		logger.info(
			"{} = {}",
			invalid_raw_message_format_for_discord_to_minecraft_error_message.getPath(),
			invalid_raw_message_format_for_discord_to_minecraft_error_message.get()
		);
		logger.info(
			"{} = {}",
			message_format_discord_to_minecraft.getPath(),
			message_format_discord_to_minecraft.get()
		);
		logger.info(
			"{} = {}",
			message_format_minecraft_to_discord.getPath(),
			message_format_minecraft_to_discord.get()
		);
		logger.info(
			"{} = {}",
			message_format_minecraft_to_discord_me_message.getPath(),
			message_format_minecraft_to_discord_me_message.get()
		);
		logger.info( "{} = {}", max_char_count.getPath(), max_char_count.get() );
		logger.info( "{} = {}", max_char_count_error_message.getPath(), max_char_count_error_message.get() );
		logger.info( "{} = {}", transmit_bot_messages.getPath(), transmit_bot_messages.get() );
		logger.info( "{} = {}", suppress_server_messages.getPath(), suppress_server_messages.get() );
		chat_messages_config.printConfig( logger );
	}
}
