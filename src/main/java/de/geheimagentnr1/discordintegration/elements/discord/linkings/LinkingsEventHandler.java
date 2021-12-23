package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEmoteEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Log4j2
public class LinkingsEventHandler extends ListenerAdapter {
	
	
	@Override
	public void onTextChannelDelete( @Nonnull TextChannelDeleteEvent event ) {
		
		if( LinkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			LinkingsManagementMessageManager.init();
		}
	}
	
	@Override
	public void onGuildMemberRemove( @Nonnull GuildMemberRemoveEvent event ) {
		
		if( LinkingsManager.isEnabled() ) {
			if( event.getMember() == null ) {
				log.error( "Failed to remove Linkings for discord user, who leaved the Discord server." );
			} else {
				
				Consumer<Throwable> errorHandler = throwable ->
					log.error(
						"Failed to remove Linkings for discord user {}, who leaved the Discord server.",
						event.getMember().getEffectiveName(),
						throwable
					);
				
				try {
					LinkingsManager.removeLinkings( event.getMember(), errorHandler );
					log.info(
						"Remove Linkings for discord user {}, who leaved the Discord server.",
						event.getMember().getEffectiveName()
					);
				} catch( IOException exception ) {
					errorHandler.accept( exception );
				}
			}
		}
	}
	
	@Override
	public void onRoleDelete( @Nonnull RoleDeleteEvent event ) {
		
		if( LinkingsManager.isEnabled() &&
			LinkingsManager.isCorrectRole( event.getRole() ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error( "Failed to update Whitelist, after the Discord whitelistrole has been deleted", throwable );
			
			try {
				log.info( "Update whiteliste, because the Discord whitelist role has been deleted" );
				LinkingsManager.updateWhitelist( errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMemberRoleAdd( @Nonnull GuildMemberRoleAddEvent event ) {
		
		if( LinkingsManager.isEnabled() &&
			event.getRoles().stream().anyMatch( LinkingsManager::isCorrectRole ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error(
					"Failed to Whitelist, after Discord user {} has been added to roles {}",
					event.getMember().getEffectiveName(),
					event.getRoles()
						.stream()
						.map( Role::getName )
						.collect( Collectors.joining( ", " ) ),
					throwable
				);
			
			try {
				LinkingsManager.updateWhitelist( errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMemberRoleRemove( @Nonnull GuildMemberRoleRemoveEvent event ) {
		
		if( LinkingsManager.isEnabled() &&
			event.getRoles().stream().anyMatch( LinkingsManager::isCorrectRole ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error(
					"Failed to Whitelist, after Discord user {} has been removed from roles {}",
					event.getMember().getEffectiveName(),
					event.getRoles()
						.stream()
						.map( Role::getName )
						.collect( Collectors.joining( ", " ) ),
					throwable
				);
			
			try {
				LinkingsManager.updateWhitelist( errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMessageDelete( @Nonnull GuildMessageDeleteEvent event ) {
		
		if( LinkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error( "Failed to resend message, after message has been deleted", throwable );
			
			try {
				LinkingsManager.resendMessage( event.getMessageIdLong(), errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMessageReactionAdd( @Nonnull GuildMessageReactionAddEvent event ) {
		
		User user = event.getUser();
		if( !LinkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ||
			user.isBot() ) {
			return;
		}
		long messageId = event.getMessageIdLong();
		TextChannel channel = event.getChannel();
		MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
		
		Boolean shouldActive =
			LinkingsManagementMessageManager.reactionCodeToBool( reactionEmote.getAsReactionCode() );
		
		if( shouldActive != null ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error(
					"Linking could not be {}",
					shouldActive ? "activated" : "deactivated",
					throwable
				);
			
			try {
				LinkingsManager.changeActiveStateOfLinking( messageId, shouldActive, errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
		if( reactionEmote.isEmoji() ) {
			channel.removeReactionById( messageId, reactionEmote.getEmoji(), user ).queue();
		} else {
			channel.removeReactionById( messageId, reactionEmote.getEmote(), user ).queue();
		}
	}
	
	@Override
	public void onGuildMessageReactionRemoveAll( @Nonnull GuildMessageReactionRemoveAllEvent event ) {
		
		if( LinkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error(
					"Failed to resend message, after all reactions have been fully removed from message",
					throwable
				);
			
			try {
				LinkingsManager.resendMessage( event.getMessageIdLong(), errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMessageReactionRemoveEmote( @Nonnull GuildMessageReactionRemoveEmoteEvent event ) {
		
		if( LinkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error( "Failed to resend message, after reaction has been fully removed from message", throwable );
			
			try {
				LinkingsManager.resendMessage( event.getMessageIdLong(), errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
}
