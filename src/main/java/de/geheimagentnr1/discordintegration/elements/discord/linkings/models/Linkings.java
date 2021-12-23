package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Data
public class Linkings {
	
	
	private HashSet<Linking> linkings;
	
	public boolean add( Linking linking ) {
		
		return linkings.add( linking );
	}
	
	public boolean remove( long discordMemberId ) {
		
		return linkings.removeIf( linking -> linking.getDiscordMemberId() == discordMemberId );
	}
	
	public boolean remove( Linking linking ) {
		
		return linkings.remove( linking );
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
