package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class DiscordMessageBuilder {
	
	
	@NotNull
	private static final String FEEDBACK_START = "**```";
	
	@NotNull
	private static final String FEEDBACK_END = "```**";
	
	@NotNull
	private final ServerConfig serverConfig;
	
	public boolean isMessageBotFeedback( @NotNull String message ) {
		
		return message.startsWith( FEEDBACK_START ) || message.endsWith( FEEDBACK_END );
	}
	
	@NotNull
	public String getEntityName( @NotNull Entity entity ) {
		
		return entity.getDisplayName().getString();
	}
	
	@NotNull
	private String getCommandSourceStackName( @NotNull CommandSourceStack source ) {
		
		return source.getDisplayName().getString();
	}
	
	@NotNull
	public String buildDeathMessage(
		@NotNull LivingDeathEvent event,
		@NotNull LivingEntity entity,
		@NotNull String name ) {
		
		return event.getSource()
			.getLocalizedDeathMessage( entity )
			.getString()
			.replace( name, "**" + name + "**" );
	}
	
	@NotNull
	public String buildMeChatMessage( @NotNull CommandSourceStack source, @NotNull String action ) {
		
		return MessageUtil.replaceParameters(
			serverConfig.getChatConfig().getMessageFormatMinecraftToDiscordMeMessage(),
			Map.of(
				"player", getCommandSourceStackName( source ),
				"message", action
			)
		);
	}
	
	@NotNull
	public String buildChatMessage( @NotNull Player player, @NotNull String message ) {
		
		return buildChatMessage( getEntityName( player ), message );
	}
	
	@NotNull
	public String buildChatMessage( @NotNull CommandSourceStack source, @NotNull Component message ) {
		
		return buildChatMessage( getCommandSourceStackName( source ), message.getString() );
	}
	
	@NotNull
	private String buildChatMessage( @NotNull String name, @NotNull String message ) {
		
		return MessageUtil.replaceParameters(
			serverConfig.getChatConfig().getMessageFormatMinecraftToDiscord(),
			Map.of(
				"player", name,
				"message", message
			)
		);
	}
	
	@NotNull
	public List<String> buildFeedbackMessage( @NotNull String message ) {
		
		List<String> messages = new ArrayList<>();
		for( int start = 0; start < message.length(); start += 1990 ) {
			messages.add( FEEDBACK_START + message.substring( start, Math.min( message.length(), start + 1990 ) ) +
				FEEDBACK_END );
		}
		return messages;
	}
}
