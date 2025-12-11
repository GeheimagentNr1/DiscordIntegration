package de.geheimagentnr1.discordintegration.elements.discord.management;

import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class ManagementMessageEventHandler extends ListenerAdapter {
	
	
	@NotNull
	private final ManagementManager managementManager;
	
	@NotNull
	private final DiscordCommandHandler discordCommandHandler;
	
	@Override
	public void onChannelDelete( @NotNull ChannelDeleteEvent event ) {
		
		if( event.getChannelType() == ChannelType.TEXT &&
			managementManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			managementManager.init();
		}
	}
	
	@Override
	public void onMessageReceived( @NotNull MessageReceivedEvent event ) {
		
		if( !event.isFromGuild() || event.getChannelType() != ChannelType.TEXT ) {
			return;
		}
		
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		User author = event.getAuthor();
		String message = event.getMessage().getContentDisplay();
		
		if( server == null ||
			author.isBot() ||
			!managementManager.isCorrectChannel( event.getChannel().getIdLong() ) ||
			!discordCommandHandler.isCommand( message ) ) {
			return;
		}
		
		Member member = event.getMember();
		
		discordCommandHandler.handleCommand( member, message, server, managementManager::sendFeedbackMessage );
	}
}
