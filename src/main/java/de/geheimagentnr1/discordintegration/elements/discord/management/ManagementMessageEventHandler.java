package de.geheimagentnr1.discordintegration.elements.discord.management;

import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class ManagementMessageEventHandler extends ListenerAdapter {
	
	
	@NotNull
	private final ManagementManager managementManager;
	
	@NotNull
	private final DiscordCommandHandler discordCommandHandler;
	
	@Override
	public void onTextChannelDelete( @NotNull TextChannelDeleteEvent event ) {
		
		if( managementManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			managementManager.init();
		}
	}
	
	@Override
	public void onGuildMessageReceived( @NotNull GuildMessageReceivedEvent event ) {
		
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		User author = event.getAuthor();
		String message = event.getMessage().getContentDisplay();
		
		if( server == null ||
			!managementManager.isCorrectChannel( event.getChannel().getIdLong() ) ||
			author.isBot() ||
			!discordCommandHandler.isCommand( message ) ) {
			return;
		}
		
		Member member = event.getMember();
		
		discordCommandHandler.handleCommand( member, message, server, managementManager::sendFeedbackMessage );
	}
}
