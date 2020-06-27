package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;


public class DiscordEventHandler extends ListenerAdapter {
	
	
	//package-private
	private static MinecraftServer server;
	
	public static void setServer( MinecraftServer _server ) {
		
		server = _server;
		DiscordCommandHandler.initSource( _server );
	}
	
	public static boolean isServerNotDedicated() {
		
		return !server.isDedicatedServer();
	}
	
	@Override
	public void onMessageReceived( @NotNull MessageReceivedEvent event ) {
		
		if( server != null && event.getChannel().getId().equals( DiscordNet.getChannel().getId() ) &&
			!event.getAuthor().isBot() ) {
			if( !DiscordCommandHandler.handleCommand( event.getMessage(), server ) ) {
				String message = event.getMessage().getContentRaw();
				for( Member member : event.getMessage().getMentionedMembers() ) {
					message = message.replace( "<@!" + member.getId() + ">", "@" + member.getEffectiveName() );
				}
				for( Role role : event.getMessage().getMentionedRoles() ) {
					message = message.replace( "<@&" + role.getId() + ">", "@" + role.getName() );
				}
				for( TextChannel channel : event.getMessage().getMentionedChannels() ) {
					message = message.replace( "<#" + channel.getId() + ">", "#" + channel.getName() );
				}
				server.getPlayerList().func_232641_a_( new StringTextComponent( "[" )
					.func_240702_b_( event.getAuthor().getName() ).func_240702_b_( "] " )
					.func_240702_b_( message ), ChatType.SYSTEM, Util.field_240973_b_ );
			}
		}
	}
}
