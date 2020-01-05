package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;


public class DiscordEventHandler extends ListenerAdapter {
	
	
	//package-private
	private static MinecraftServer server;
	
	public static void setServer( MinecraftServer _server ) {
		
		server = _server;
		DiscordCommandHandler.initSource( _server );
	}
	
	@Override
	public void onMessageReceived( @NotNull MessageReceivedEvent event ) {
		
		if( server != null && event.getChannel().getId().equals( DiscordNet.getChannel().getId() ) &&
			!event.getAuthor().isBot() ) {
			if( !DiscordCommandHandler.handleCommand( event.getMessage(), server ) ) {
				server.getPlayerList().sendMessage( new StringTextComponent( "[" )
					.appendText( event.getAuthor().getName() ).appendText( "] " )
					.appendText( event.getMessage().getContentRaw() ), true );
			}
		}
	}
}
