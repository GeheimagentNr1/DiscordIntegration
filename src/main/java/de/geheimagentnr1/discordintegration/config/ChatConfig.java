package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.slf4j.Logger;


public class ChatConfig {
	
	
	private final ForgeConfigSpec.BooleanValue enabled;
	
	private final ForgeConfigSpec.LongValue channel_id;
	
	private final ForgeConfigSpec.BooleanValue use_nickname;
	
	private final ForgeConfigSpec.IntValue max_char_count;
	
	private final ForgeConfigSpec.BooleanValue transmit_bot_messages;
	
	private final ChatMessagesConfig chat_messages_config;
	
	//package-private
	ChatConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Chat channel settings" )
			.push( "chat" );
		enabled = builder.comment( "Should the Minecraft and Discord chat be linked?" )
			.define( "enabled", false );
		channel_id = builder.comment( "Channel ID where the Minecraft and Discord chat will be linked." )
			.defineInRange( "channel_id", 0, 0, Long.MAX_VALUE );
		use_nickname = builder.comment( "Shall the nickname of the Discord user be shown in the Minecraft chat as " +
				"author name? (If not, the username of the Discord user is shown as author name.)" )
			.define( "use_nickname", true );
		max_char_count = builder.comment( "How long should Discord messages send to Minecraft Chat be at most? " +
				"If the value is -1, there is no limit to the length." )
			.defineInRange( "max_char_count", -1, -1, 2000 );
		transmit_bot_messages = builder.comment( "Should messages of other bots be sent to the Minecraft chat?" )
			.define( "transmit_bot_messages", false );
		chat_messages_config = new ChatMessagesConfig( builder );
		builder.pop();
	}
	
	public boolean isEnabled() {
		
		return enabled.get();
	}
	
	public long getChannelId() {
		
		return channel_id.get();
	}
	
	public boolean useNickname() {
		
		return use_nickname.get();
	}
	
	public int getMaxCharCount() {
		
		return max_char_count.get();
	}
	
	public boolean transmitBotMessages() {
		
		return transmit_bot_messages.get();
	}
	
	public ChatMessagesConfig getChatMessagesConfig() {
		
		return chat_messages_config;
	}
	
	//package-private
	@SuppressWarnings( "DuplicatedCode" )
	void printConfig( Logger logger ) {
		
		logger.info( "{} = {}", enabled.getPath(), enabled.get() );
		logger.info( "{} = {}", channel_id.getPath(), channel_id.get() );
		logger.info( "{} = {}", use_nickname.getPath(), use_nickname.get() );
		logger.info( "{} = {}", max_char_count.getPath(), max_char_count.get() );
		logger.info( "{} = {}", transmit_bot_messages.getPath(), transmit_bot_messages.get() );
		chat_messages_config.printConfig( logger );
	}
}
