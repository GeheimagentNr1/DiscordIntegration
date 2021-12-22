package de.geheimagentnr1.discordintegration.elements.linking;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;


public class LinkingMessageSender {
	
	
	private static final String TRUE_EMOJI = "\u2705";
	
	private static final String FALSE_EMOJI = "\u274C";
	
	private static TextChannel channel;
	
	public static synchronized void init() {
		
		channel = DiscordNet.getGuild().getTextChannelById( ServerConfig.WHITELIST_CONFIG.getLinkingManagementChannelId() );
	}
	
	public static synchronized Message createOrSendMessage( Member member, Linking linking ) {
		
		Message message;
		if( channel.retrieveMessageById( linking.getMessageId() ).complete() != null ) {
			message = channel.editMessageEmbedsById( linking.getMessageId(), buildMessage( member, linking ) )
				.complete();
		} else {
			message = channel.sendMessageEmbeds( buildMessage( member, linking ) ).complete();
		}
		message.addReaction( TRUE_EMOJI ).complete();
		message.addReaction( FALSE_EMOJI ).complete();
		linking.setMessageId( message.getIdLong() );
		return message;
	}
	
	public static void onYesReaction( Long messageId, TextChannel textChannel ) {
		
		if( textChannel.getIdLong() == ServerConfig.WHITELIST_CONFIG.getLinkingManagementChannelId() ) {
			Linking linking = LinkingManager.activateLinking( messageId );
			if( linking != null ) {
				updateMessage( linking );
			}
		}
	}
	
	public static void onNoReaction( Long messageId, TextChannel textChannel ) {
		
		if( textChannel.getIdLong() == ServerConfig.WHITELIST_CONFIG.getLinkingManagementChannelId() ) {
			Linking linking = LinkingManager.deactivateLinking( messageId );
			if( linking != null ) {
				updateMessage( linking );
			}
		}
	}
	
	public static synchronized void updateMessage( Linking linking ) {
		
		Member member = DiscordNet.getGuild().getMemberById( linking.getDiscordMemberId() );
		createOrSendMessage( member, linking );
		//TODO: Save ggf. new data
	}
	
	private static synchronized MessageEmbed buildMessage( Member member, Linking linking ) {
		
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.addField( "Discord Name", linking.getDiscordName(), true );
		builder.addField( "Minecraft Name", linking.getMinecraftGameProfile().getName(), true );
		builder.addBlankField( true );
		builder.addField( "Has Role", boolToEmoji( LinkingManager.hasCorrectRole( member ) ), true );
		builder.addField( "Active", boolToEmoji( linking.isActive() ), true );
		builder.addBlankField( true );
		
		return builder.build();
	}
	
	private static String boolToEmoji( boolean value ) {
		
		return value ? TRUE_EMOJI : FALSE_EMOJI;
	}
	
	public static synchronized void deleteMessage( Linking linking ) {
		
		channel.deleteMessageById( linking.getMessageId() ).complete();
	}
}
