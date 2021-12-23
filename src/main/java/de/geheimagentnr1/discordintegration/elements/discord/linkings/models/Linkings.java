package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Data
public class Linkings {
	
	
	private HashSet<Linking> linkings;
	
	public void add( Linking linking ) {
		
		linkings.add( linking );
	}
	
	public boolean remove( long discordMemberId ) {
		
		return linkings.removeIf( linking -> linking.getDiscordMemberId() == discordMemberId );
	}
	
	public void remove( Linking linking ) {
		
		linkings.remove( linking );
	}
	
	public Optional<Linking> findLinking( Linking searchLinking ) {
		
		return linkings.stream()
			.filter( linking -> linking.equals( searchLinking ) )
			.findFirst();
	}
	
	public Optional<Linking> findLinking( long messageId ) {
		
		return linkings.stream()
			.filter( linking -> linking.getMessageId().equals( messageId ) )
			.findFirst();
	}
	
	public List<Linking> findLinkings( long discordMemberId ) {
		
		return linkings.stream()
			.filter( linking -> linking.getDiscordMemberId().equals( discordMemberId ) )
			.toList();
	}
}
