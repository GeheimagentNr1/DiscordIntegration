package de.geheimagentnr1.discordintegration.elements.linking;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class Linkings {
	
	
	private List<Linking> linkings = new ArrayList<>();
	
	//package-private
	boolean remove( Linking linking ) {
		
		return linkings.remove( linking );
	}
	
	//package-private
	void add( Linking linking ) {
		
		if( linkings.stream().noneMatch( testLinking -> testLinking.equals( linking ) ) ) {
			linkings.add( linking );
		}
	}
	
	public Linking find( Linking linking ) {
		
		return linkings.stream().filter( testLinking -> testLinking.equals( linking ) ).findFirst().orElseGet( null );
	}
	
	public Linking find( Long messageId ) {
		
		return linkings.stream()
			.filter( testLinking -> testLinking.getMessageId().equals( messageId ) )
			.findFirst()
			.orElseGet( null );
	}
	
	public List<Linking> removeLinkings( long discordMemberId ) {
		
		List<Linking> removedLinkings = linkings.stream()
			.filter( linking -> linking.getDiscordMemberId().equals( discordMemberId ) )
			.collect( Collectors.toList() );
		linkings.removeAll( removedLinkings );
		return removedLinkings;
	}
}
