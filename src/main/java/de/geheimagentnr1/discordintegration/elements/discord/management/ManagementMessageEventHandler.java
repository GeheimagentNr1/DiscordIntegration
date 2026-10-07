package de.geheimagentnr1.discordintegration.elements.discord.management;

import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class ManagementMessageEventHandler extends ListenerAdapter {
	
	
	@Override
	public void onChannelDelete( @Nonnull ChannelDeleteEvent event ) {
		
		if( event.getChannelType() != ChannelType.TEXT ) {
			return;
		}
		if( ManagementManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			ManagementManager.init();
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
			!ManagementManager.isCorrectChannel( event.getChannel().getIdLong() ) ||
			author.isBot() ||
			!DiscordCommandHandler.isCommand( message ) ) {
			return;
		}
		
		Member member = event.getMember();
		
		DiscordCommandHandler.handleCommand( member, message, server, ManagementManager::sendFeedbackMessage );
	}
}
