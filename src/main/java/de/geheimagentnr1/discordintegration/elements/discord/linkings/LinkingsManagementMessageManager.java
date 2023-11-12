package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.elements.discord.AbstractDiscordIntegrationServiceProvider;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linking;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.LinkingMessageRequestCounter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;


@SuppressWarnings( { "SynchronizeOnThis", "NestedSynchronizedStatement" } )
@Log4j2
@RequiredArgsConstructor
public class LinkingsManagementMessageManager extends AbstractDiscordIntegrationServiceProvider {
	
	
	@SuppressWarnings( "UnnecessaryUnicodeEscape" )
	@NotNull
	private static final String TRUE_EMOJI = "\u2705";
	
	@SuppressWarnings( "UnnecessaryUnicodeEscape" )
	@NotNull
	private static final String FALSE_EMOJI = "\u274C";
	
	@Getter( AccessLevel.PROTECTED )
	@NotNull
	private final DiscordIntegration discordIntegration;
	
	private TextChannel channel;
	
	public void init() {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				stop();
				if( shouldInitialize() ) {
					long channelId = serverConfig().getWhitelistConfig().getLinkingManagementChannelId();
					JDA jda = discordManager().getJda();
					channel = jda.getTextChannelById( channelId );
					if( channel == null ) {
						log.error( "Discord Linking Management Text Channel {} not found", channelId );
					}
				}
			}
		}
	}
	
	public synchronized void stop() {
		
		channel = null;
	}
	
	private boolean shouldInitialize() {
		
		return linkingsManager().isEnabled() && serverConfig().getWhitelistConfig().useSingleLinkingManagement();
	}
	
	private boolean isInitialized() {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				return shouldInitialize() && channel != null;
			}
		}
	}
	
	//package-private
	boolean isCorrectChannel( long channelId ) {
		
		return isInitialized() && serverConfig().getWhitelistConfig().getLinkingManagementChannelId() == channelId;
	}
	
	@NotNull
	private String boolToEmoji( boolean value ) {
		
		return value ? TRUE_EMOJI : FALSE_EMOJI;
	}
	
	@Nullable
	Boolean reactionCodeToBool( @NotNull String reactionCode ) {
		
		return switch( reactionCode ) {
			case TRUE_EMOJI -> true;
			case FALSE_EMOJI -> false;
			default -> null;
		};
	}
	
	//package-private
	void sendOrEditMessage(
		@NotNull Member member,
		@NotNull Linking linking,
		boolean hasChanged,
		@NotNull Consumer<Long> messageIdHandler ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				if( isInitialized() ) {
					LinkingMessageRequestCounter linkingMessageRequestCounter = new LinkingMessageRequestCounter(
						linking.getDiscordUsername(),
						linking.getMinecraftGameProfile().getName()
					);
					
					try {
						Long messageId = linking.getMessageId();
						
						if( messageId == null ) {
							sendOrEditMessage(
								null,
								member,
								linking,
								true,
								messageIdHandler,
								linkingMessageRequestCounter
							);
						} else {
							linkingMessageRequestCounter.addRequest( "rm" );
							channel.retrieveMessageById( messageId ).queue(
								retrievedMessage -> sendOrEditMessage(
									retrievedMessage,
									member,
									linking,
									hasChanged,
									messageIdHandler,
									linkingMessageRequestCounter
								),
								throwable -> {
									if( throwable instanceof ErrorResponseException errorResponseException &&
										errorResponseException.getErrorResponse() ==
											ErrorResponse.UNKNOWN_MESSAGE ) {
										sendOrEditMessage(
											null,
											member,
											linking,
											true,
											messageIdHandler,
											linkingMessageRequestCounter
										);
									} else {
										RestAction.getDefaultFailure().accept( throwable );
									}
								}
							);
						}
					} catch( Exception exception ) {
						log.error(
							"Message could not be retrieved from Linking Management Channel {}",
							serverConfig().getWhitelistConfig().getLinkingManagementChannelId(),
							exception
						);
					}
				} else {
					if( !serverConfig().getWhitelistConfig().useSingleLinkingManagement() ) {
						messageIdHandler.accept( null );
					}
				}
			}
		}
	}
	
	private void sendOrEditMessage(
		@Nullable Message oldMessage,
		@NotNull Member member,
		@NotNull Linking linking,
		boolean hasChanged,
		@NotNull Consumer<Long> messageIdHandler,
		@NotNull LinkingMessageRequestCounter linkingMessageRequestCounter ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				if( isInitialized() ) {
					try {
						MessageEmbed newMessage = buildMessage( member, linking );
						if( oldMessage == null ) {
							linkingMessageRequestCounter.addRequest( "sm" );
							channel.sendMessageEmbeds( newMessage )
								.queue( message -> handleMessageSentOrEdited(
									message,
									messageIdHandler,
									linkingMessageRequestCounter
								) );
						} else {
							if( hasChanged ) {
								linkingMessageRequestCounter.addRequest( "em" );
								channel.editMessageEmbedsById( linking.getMessageId(), newMessage )
									.queue( message -> handleMessageSentOrEdited(
										message,
										messageIdHandler,
										linkingMessageRequestCounter
									) );
							} else {
								handleMessageSentOrEdited(
									oldMessage,
									messageIdHandler,
									linkingMessageRequestCounter
								);
							}
						}
					} catch( Exception exception ) {
						log.error(
							"Message could not be send or edited to Linking Management Channel {}",
							serverConfig().getWhitelistConfig().getLinkingManagementChannelId(),
							exception
						);
					}
				}
			}
		}
	}
	
	private void handleMessageSentOrEdited(
		@NotNull Message message,
		@NotNull Consumer<Long> messageIdHandler,
		@NotNull LinkingMessageRequestCounter linkingMessageRequestCounter ) {
		
		synchronized( DiscordManager.class ) {
			if( isInitialized() ) {
				if( message.getReactionByUnicode( TRUE_EMOJI ) == null ) {
					linkingMessageRequestCounter.addRequest( "ar_t" );
					message.addReaction( TRUE_EMOJI ).queue();
				}
				if( message.getReactionByUnicode( FALSE_EMOJI ) == null ) {
					linkingMessageRequestCounter.addRequest( "ar_f" );
					message.addReaction( FALSE_EMOJI ).queue();
				}
				log.debug(
					"Run {} request for linking discord user \"{}\" and Minecraft user \"{}\" requests: {}",
					linkingMessageRequestCounter.getCount(),
					linkingMessageRequestCounter.getDiscordName(),
					linkingMessageRequestCounter.getMinecraftName(),
					linkingMessageRequestCounter.getRequestsString()
				);
				
				messageIdHandler.accept( message.getIdLong() );
			}
		}
	}
	
	@NotNull
	private MessageEmbed buildMessage( @NotNull Member member, @NotNull Linking linking ) {
		
		String discordName = linking.getDiscordUsername();
		String minecraftName = linking.getMinecraftGameProfile().getName();
		boolean hasRole = linkingsManager().hasCorrectRole( member );
		boolean isActive = linking.isActive();
		
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.addField( "Discord Name", discordName, true );
		builder.addField( "Minecraft Name", minecraftName, true );
		builder.addBlankField( true );
		builder.addField( "Has Role", boolToEmoji( hasRole ), true );
		builder.addField( "Is Active", boolToEmoji( isActive ), true );
		builder.addBlankField( true );
		
		return builder.build();
	}
	
	//package-private
	void deleteMessage( @NotNull Linking linking ) {
		
		if( isInitialized() ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				channel.deleteMessageById( linking.getMessageId() ).complete();
			}
		}
	}
}
