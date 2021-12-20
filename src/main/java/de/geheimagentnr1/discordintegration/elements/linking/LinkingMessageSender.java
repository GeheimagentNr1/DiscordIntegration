package de.geheimagentnr1.discordintegration.elements.linking;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;


public class LinkingMessageSender {
	
	
	private static TextChannel channel;
	
	public static synchronized void init() {
		
		channel = DiscordNet.getGuild().getTextChannelById( ServerConfig.getLinkingManagementChannelId() );
	}
	
	public static synchronized Message sendMessage( Member member, Linking linking ) {
		
		Message message = channel.sendMessageEmbeds( buildMessage( member, linking ) ).complete();
		message.addReaction( "\u2705" ).complete();
		message.addReaction( "\u274C" ).complete();
		return message;
	}
	
	public static void onYesReaction( Long messageId, TextChannel textChannel ) {
		
		if( textChannel.getIdLong() == ServerConfig.getLinkingManagementChannelId() ) {
			Linking linking = LinkingManager.activateLinking( messageId );
			if( linking != null ) {
				updateMessage( linking );
			}
		}
	}
	
	public static void onNoReaction( Long messageId, TextChannel textChannel ) {
		
		if( textChannel.getIdLong() == ServerConfig.getLinkingManagementChannelId() ) {
			Linking linking = LinkingManager.deactivateLinking( messageId );
			if( linking != null ) {
				updateMessage( linking );
			}
		}
	}
	
	public static synchronized void updateMessage( Linking linking ) {
		
		Member member = DiscordNet.getGuild().getMemberById( linking.getDiscordMemberId() );
		if( member == null ) {
			//TODO
		} else {
			Message message = channel.editMessageEmbedsById( linking.getMessageId(), buildMessage( member, linking ) )
				.complete();
			message.addReaction( "\u2705" ).complete();
			message.addReaction( "\u274C" ).complete();
		}
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
		
		return value ? "\u2705" : "\u274C";
	}
	
	public static synchronized void deleteMessage( Linking linking ) {
		
		channel.deleteMessageById( linking.getMessageId() ).complete();
	}
}
