package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linking;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.LinkingMessageRequestCounter;
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

import java.util.function.Consumer;


@SuppressWarnings( { "SynchronizeOnThis", "NestedSynchronizedStatement" } )
@Log4j2
public class LinkingsManagementMessageManager {
	
	
	private static final String TRUE_EMOJI = "\u2705";
	
	private static final String FALSE_EMOJI = "\u274C";
	
	private static TextChannel channel;
	
	public static void init() {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				stop();
				if( shouldInitialize() ) {
					long channelId = ServerConfig.WHITELIST_CONFIG.getLinkingManagementChannelId();
					JDA jda = DiscordManager.getJda();
					channel = jda.getTextChannelById( channelId );
					if( channel == null ) {
						log.error( "Discord Linking Management Text Channel {} not found", channelId );
					}
				}
			}
		}
	}
	
	public static synchronized void stop() {
		
		channel = null;
	}
	
	private static boolean shouldInitialize() {
		
		return LinkingsManager.isEnabled() && ServerConfig.WHITELIST_CONFIG.useSingleLinkingManagement();
	}
	
	private static boolean isInitialized() {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				return shouldInitialize() && channel != null;
			}
		}
	}
	
	//package-private
	static boolean isCorrectChannel( long channelId ) {
		
		return isInitialized() && ServerConfig.WHITELIST_CONFIG.getLinkingManagementChannelId() == channelId;
	}
	
	private static String boolToEmoji( boolean value ) {
		
		return value ? TRUE_EMOJI : FALSE_EMOJI;
	}
	
	//package-private
	static Boolean reactionCodeToBool( String reactionCode ) {
		
		return switch( reactionCode ) {
			case TRUE_EMOJI -> true;
			case FALSE_EMOJI -> false;
			default -> null;
		};
	}
	
	//package-private
	static void sendOrEditMessage(
		Member member,
		Linking linking,
		boolean hasChanged,
		Consumer<Long> messageIdHandler ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				if( isInitialized() ) {
					LinkingMessageRequestCounter linkingMessageRequestCounter = new LinkingMessageRequestCounter(
						linking.getDiscordName(),
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
							ServerConfig.WHITELIST_CONFIG.getLinkingManagementChannelId(),
							exception
						);
					}
				} else {
					if( !ServerConfig.WHITELIST_CONFIG.useSingleLinkingManagement() ) {
						messageIdHandler.accept( null );
					}
				}
			}
		}
	}
	
	private static void sendOrEditMessage(
		Message oldMessage,
		Member member,
		Linking linking,
		boolean hasChanged,
		Consumer<Long> messageIdHandler,
		LinkingMessageRequestCounter linkingMessageRequestCounter ) {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				if( isInitialized() ) {
					try {
						MessageEmbed newMessage = buildMessage( member, linking );
						if( oldMessage == null ) {
							linkingMessageRequestCounter.addRequest( "sm" );
							channel.sendMessageEmbeds( newMessage )
								.queue( message -> handleMessageSentOrEdited( message, messageIdHandler,
									linkingMessageRequestCounter
								) );
						} else {
							if( hasChanged ) {
								linkingMessageRequestCounter.addRequest( "em" );
								channel.editMessageEmbedsById( linking.getMessageId(), newMessage )
									.queue( message -> handleMessageSentOrEdited( message, messageIdHandler,
										linkingMessageRequestCounter
									) );
							} else {
								handleMessageSentOrEdited( oldMessage, messageIdHandler,
									linkingMessageRequestCounter );
							}
						}
					} catch( Exception exception ) {
						log.error(
							"Message could not be send or edited to Linking Management Channel {}",
							ServerConfig.WHITELIST_CONFIG.getLinkingManagementChannelId(),
							exception
						);
					}
				}
			}
		}
	}
	
	private static void handleMessageSentOrEdited(
		Message message,
		Consumer<Long> messageIdHandler,
		LinkingMessageRequestCounter linkingMessageRequestCounter ) {
		
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
	
	private static MessageEmbed buildMessage( Member member, Linking linking ) {
		
		String discordName = linking.getDiscordName();
		String minecraftName = linking.getMinecraftGameProfile().getName();
		boolean hasRole = LinkingsManager.hasCorrectRole( member );
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
	static void deleteMessage( Linking linking ) {
		
		if( isInitialized() ) {
			synchronized( LinkingsManagementMessageManager.class ) {
				channel.deleteMessageById( linking.getMessageId() ).complete();
			}
		}
	}
}
