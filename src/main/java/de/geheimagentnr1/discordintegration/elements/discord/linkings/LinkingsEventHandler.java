package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Log4j2
public class LinkingsEventHandler extends ListenerAdapter {
	
	
	@Override
	public void onChannelDelete( @Nonnull ChannelDeleteEvent event ) {
		
		if( event.getChannelType() != ChannelType.TEXT ) {
			return;
		}
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
	public void onMessageDelete( @Nonnull MessageDeleteEvent event ) {
		
		if( !event.isFromGuild() ) {
			return;
		}
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
	public void onMessageReactionAdd( @Nonnull MessageReactionAddEvent event ) {
		
		if( !event.isFromGuild() || event.getUser() == null ) {
			return;
		}
		User user = event.getUser();
		if( !LinkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ||
			user.isBot() ) {
			return;
		}
		Member member = event.getMember();
		long messageId = event.getMessageIdLong();
		GuildMessageChannel channel = event.getGuildChannel();
		EmojiUnion reactionEmote = event.getEmoji();
		
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
				LinkingsManager.changeActiveStateOfLinking( member, messageId, shouldActive, errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
		channel.removeReactionById( messageId, reactionEmote, user ).queue();
	}
	
	@Override
	public void onMessageReactionRemove( @NotNull MessageReactionRemoveEvent event ) {
		
		if( !event.isFromGuild() ) {
			return;
		}
		if( LinkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) &&
			DiscordManager.getSelfUser().getIdLong() == event.getUserIdLong() ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error(
					"Failed to resend message, after a reaction have been removed from message",
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
	public void onMessageReactionRemoveAll( @Nonnull MessageReactionRemoveAllEvent event ) {
		
		if( !event.isFromGuild() ) {
			return;
		}
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
	public void onMessageReactionRemoveEmoji( @Nonnull MessageReactionRemoveEmojiEvent event ) {
		
		if( !event.isFromGuild() ) {
			return;
		}
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
