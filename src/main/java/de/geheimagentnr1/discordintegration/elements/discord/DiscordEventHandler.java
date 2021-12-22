package de.geheimagentnr1.discordintegration.elements.discord;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.linking.LinkingManager;
import de.geheimagentnr1.discordintegration.elements.linking.LinkingMessageSender;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEmoteEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class DiscordEventHandler extends ListenerAdapter {
	
	
	private static MinecraftServer server;
	
	public static void setServer( MinecraftServer _server ) {
		
		server = _server;
	}
	
	@Override
	public void onGuildMessageReceived( @Nonnull GuildMessageReceivedEvent event ) {
		
		User author = event.getAuthor();
		Member member = event.getMember();
		if( isInitialized() && DiscordNet.feedBackAllowed( event.getChannel(), author ) ) {
			String message = event.getMessage().getContentDisplay();
			if( author.isBot() ) {
				handleBotMessage( message );
			} else {
				if( message.startsWith( ServerConfig.getCommandPrefix() ) ) {
					handleCommands( member, message );
				} else {
					if( beginnsNotWithOtherCommandPrefix( message ) ) {
						if( ServerConfig.isUseNickname() && member != null ) {
							handleUserMessage( member.getEffectiveName(), message );
						} else {
							handleUserMessage( author.getName(), message );
						}
					}
				}
			}
		}
	}
	
	private boolean isInitialized() {
		
		return server != null && DiscordNet.isInitialized();
	}
	
	private boolean beginnsNotWithOtherCommandPrefix( String message ) {
		
		for( String prefix : ServerConfig.getOtherBotsCommandPrefixes() ) {
			if( message.startsWith( prefix ) ) {
				return false;
			}
		}
		return true;
	}
	
	private void handleCommands( Member author, String command ) {
		
		if( author != null && !author.getUser().isBot() ) {
			if( !DiscordCommandHandler.handleCommand( author, command, server ) ) {
				DiscordNet.sendFeedbackMessage( String.format(
					"%n%s%nError: Unknown Command",
					author.getUser().getName()
				) );
			}
		}
	}
	
	private void handleBotMessage( String message ) {
		
		if( ServerConfig.isTransmitBotMessages() ) {
			if( !message.startsWith( DiscordNet.FEEDBACK_START ) || !message.endsWith( DiscordNet.FEEDBACK_END ) ) {
				server.getPlayerList().broadcastMessage(
					new TextComponent( message ),
					ChatType.CHAT,
					Util.NIL_UUID
				);
			}
		}
	}
	
	private void handleUserMessage( String author, String message ) {
		
		if( ServerConfig.getMaxCharCount() == -1 || message.length() <= ServerConfig.getMaxCharCount() ) {
			server.getPlayerList().broadcastMessage(
				new TextComponent( String.format(
					"[%s] %s",
					author,
					message
				) ),
				ChatType.CHAT,
				Util.NIL_UUID
			);
		} else {
			DiscordNet.sendFeedbackMessage( String.format(
				"%n%s%nError: Message to long.%n" +
					"Messages can only be up to %d characters long.%n" +
					"Your message is %d characters long.",
				author,
				ServerConfig.CHAT_CONFIG.getMaxCharCount(),
				message.length()
			) );
		}
	}
	
	@Override
	public void onGuildMemberRoleAdd( @Nonnull GuildMemberRoleAddEvent event ) {
		
		LinkingManager.handleRoleAdded( event.getMember() );
	}
	
	@Override
	public void onGuildMemberRoleRemove( @Nonnull GuildMemberRoleRemoveEvent event ) {
		
		LinkingManager.handleRoleRemoved( event.getMember() );
	}
	
	@Override
	public void onRoleDelete( @Nonnull RoleDeleteEvent event ) {
		
		LinkingManager.init();
	}
	
	@Override
	public void onTextChannelDelete( @NotNull TextChannelDeleteEvent event ) {
		
		//TODO:
	}
	
	@Override
	public void onGuildMessageReactionAdd( @NotNull GuildMessageReactionAddEvent event ) {
		
		User user = event.getMember().getUser();
		if( user.isBot() ) {
			return;
		}
		long messageId = event.getMessageIdLong();
		TextChannel textChannel = event.getChannel();
		MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
		
		switch( reactionEmote.getAsReactionCode() ) {
			case "\u2705" -> LinkingMessageSender.onYesReaction( messageId, textChannel );
			case "\u274C" -> LinkingMessageSender.onNoReaction( messageId, textChannel );
		}
		if( reactionEmote.isEmoji() ) {
			textChannel.removeReactionById( messageId, reactionEmote.getEmoji(), user ).queue();
		} else {
			textChannel.removeReactionById( messageId, reactionEmote.getEmote(), user ).queue();
		}
	}
	
	@Override
	public void onGuildMessageReactionRemoveAll( @NotNull GuildMessageReactionRemoveAllEvent event ) {
		
		//TODO: Resend Message
	}
	
	@Override
	public void onGuildMessageReactionRemoveEmote( @NotNull GuildMessageReactionRemoveEmoteEvent event ) {
		
		//TODO: Resend Message
	}
	
	@Override
	public void onGuildMessageDelete( @NotNull GuildMessageDeleteEvent event ) {
		
		//TODO: Resend Message + Save new message Id
	}
	
	@Override
	public void onGuildMemberRemove( @NotNull GuildMemberRemoveEvent event ) {
		
		LinkingManager.removeLinkings( event.getMember() );
	}
}
