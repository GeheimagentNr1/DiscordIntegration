package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEmoteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Log4j2
@RequiredArgsConstructor
public class LinkingsEventHandler extends ListenerAdapter {
	
	
	@NotNull
	private final DiscordManager discordManager;
	
	@NotNull
	private final LinkingsManagementMessageManager linkingsManagementMessageManager;
	
	@NotNull
	private final LinkingsManager linkingsManager;
	
	@Override
	public void onTextChannelDelete( @NotNull TextChannelDeleteEvent event ) {
		
		if( linkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			linkingsManagementMessageManager.init();
		}
	}
	
	@Override
	public void onGuildMemberRemove( @NotNull GuildMemberRemoveEvent event ) {
		
		if( linkingsManager.isEnabled() ) {
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
					linkingsManager.removeLinkings( event.getMember(), errorHandler );
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
	public void onRoleDelete( @NotNull RoleDeleteEvent event ) {
		
		if( linkingsManager.isEnabled() &&
			linkingsManager.isCorrectRole( event.getRole() ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error( "Failed to update Whitelist, after the Discord whitelistrole has been deleted", throwable );
			
			try {
				log.info( "Update whiteliste, because the Discord whitelist role has been deleted" );
				linkingsManager.updateWhitelist( errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMemberRoleAdd( @NotNull GuildMemberRoleAddEvent event ) {
		
		if( linkingsManager.isEnabled() &&
			event.getRoles().stream().anyMatch( linkingsManager::isCorrectRole ) ) {
			
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
				linkingsManager.updateWhitelist( errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMemberRoleRemove( @NotNull GuildMemberRoleRemoveEvent event ) {
		
		if( linkingsManager.isEnabled() &&
			event.getRoles().stream().anyMatch( linkingsManager::isCorrectRole ) ) {
			
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
				linkingsManager.updateWhitelist( errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMessageDelete( @NotNull GuildMessageDeleteEvent event ) {
		
		if( linkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error( "Failed to resend message, after message has been deleted", throwable );
			
			try {
				linkingsManager.resendMessage( event.getMessageIdLong(), errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMessageReactionAdd( @NotNull GuildMessageReactionAddEvent event ) {
		
		User user = event.getUser();
		if( !linkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ||
			user.isBot() ) {
			return;
		}
		Member member = event.getMember();
		long messageId = event.getMessageIdLong();
		TextChannel channel = event.getChannel();
		MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
		
		Boolean shouldActive =
			linkingsManagementMessageManager.reactionCodeToBool( reactionEmote.getAsReactionCode() );
		
		if( shouldActive != null ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error(
					"Linking could not be {}",
					shouldActive ? "activated" : "deactivated",
					throwable
				);
			
			try {
				linkingsManager.changeActiveStateOfLinking( member, messageId, shouldActive, errorHandler );
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
	public void onGuildMessageReactionRemove( @NotNull GuildMessageReactionRemoveEvent event ) {
		
		if( linkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) &&
			discordManager.getSelfUser().getIdLong() == event.getUserIdLong() ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error(
					"Failed to resend message, after a reaction have been removed from message",
					throwable
				);
			
			try {
				linkingsManager.resendMessage( event.getMessageIdLong(), errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMessageReactionRemoveAll( @NotNull GuildMessageReactionRemoveAllEvent event ) {
		
		if( linkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error(
					"Failed to resend message, after all reactions have been fully removed from message",
					throwable
				);
			
			try {
				linkingsManager.resendMessage( event.getMessageIdLong(), errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
	
	@Override
	public void onGuildMessageReactionRemoveEmote( @NotNull GuildMessageReactionRemoveEmoteEvent event ) {
		
		if( linkingsManagementMessageManager.isCorrectChannel( event.getChannel().getIdLong() ) ) {
			
			Consumer<Throwable> errorHandler = throwable ->
				log.error( "Failed to resend message, after reaction has been fully removed from message", throwable );
			
			try {
				linkingsManager.resendMessage( event.getMessageIdLong(), errorHandler );
			} catch( IOException exception ) {
				errorHandler.accept( exception );
			}
		}
	}
}
