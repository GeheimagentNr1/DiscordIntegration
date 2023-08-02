package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.util.MessageUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DiscordMessageBuilder {
	
	
	private static final String FEEDBACK_START = "**```";
	
	private static final String FEEDBACK_END = "```**";
	
	public static boolean isMessageBotFeedback( String message ) {
		
		return message.startsWith( FEEDBACK_START ) || message.endsWith( FEEDBACK_END );
	}
	
	public static String getEntityName( Entity entity ) {
		
		return entity.getDisplayName().getString();
	}
	
	private static String getCommandSourceStackName( CommandSourceStack source ) {
		
		return source.getDisplayName().getString();
	}
	
	public static String buildDeathMessage( LivingDeathEvent event, LivingEntity entity, String name ) {
		
		return event.getSource()
			.getLocalizedDeathMessage( entity )
			.getString()
			.replace( name, "**" + name + "**" );
	}
	
	public static String buildMeChatMessage( CommandSourceStack source, String action ) {
		
		return MessageUtil.replaceParameters(
			ServerConfig.CHAT_CONFIG.getMessageFormatMinecraftToDiscordMeMessage(),
			Map.of(
				"player", getCommandSourceStackName( source ),
				"message", action
			)
		);
	}
	
	public static String buildChatMessage( Player player, String message ) {
		
		return buildChatMessage( getEntityName( player ), message );
	}
	
	public static String buildChatMessage( CommandSourceStack source, Component message ) {
		
		return buildChatMessage( getCommandSourceStackName( source ), message.getString() );
	}
	
	private static String buildChatMessage( String name, String message ) {
		
		return MessageUtil.replaceParameters(
			ServerConfig.CHAT_CONFIG.getMessageFormatMinecraftToDiscord(),
			Map.of(
				"player", name,
				"message", message
			)
		);
	}
	
	public static List<String> buildFeedbackMessage( String message ) {
		
		List<String> messages = new ArrayList<>();
		for( int start = 0; start < message.length(); start += 1990 ) {
			messages.add( FEEDBACK_START + message.substring( start, Math.min( message.length(), start + 1990 ) ) +
				FEEDBACK_END );
		}
		return messages;
	}
}
