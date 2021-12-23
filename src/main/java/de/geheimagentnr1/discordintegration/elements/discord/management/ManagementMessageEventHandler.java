package de.geheimagentnr1.discordintegration.elements.discord.management;

import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class ManagementMessageEventHandler extends ListenerAdapter {
	
	
	@Override
	public void onTextChannelDelete( @Nonnull TextChannelDeleteEvent event ) {
		
		if( ManagementManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			ManagementManager.init();
		}
	}
	
	@Override
	public void onGuildMessageReceived( @NotNull GuildMessageReceivedEvent event ) {
		
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
