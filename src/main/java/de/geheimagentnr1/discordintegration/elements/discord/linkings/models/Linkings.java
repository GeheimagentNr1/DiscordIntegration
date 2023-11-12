package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Data
public class Linkings {
	
	
	@NotNull
	private HashSet<Linking> linkings = new HashSet<>();
	
	public void add( @NotNull Linking linking ) {
		
		linkings.add( linking );
	}
	
	public boolean remove( long discordMemberId ) {
		
		return linkings.removeIf( linking -> linking.getDiscordMemberId() == discordMemberId );
	}
	
	public void remove( @NotNull Linking linking ) {
		
		linkings.remove( linking );
	}
	
	@NotNull
	public Optional<Linking> findLinking( @NotNull Linking searchLinking ) {
		
		return linkings.stream()
			.filter( linking -> linking.equals( searchLinking ) )
			.findFirst();
	}
	
	@NotNull
	public Optional<Linking> findLinking( long messageId ) {
		
		return linkings.stream()
			.filter( linking -> linking.getMessageId().equals( messageId ) )
			.findFirst();
	}
	
	@NotNull
	public List<Linking> findLinkings( long discordMemberId ) {
		
		return linkings.stream()
			.filter( linking -> linking.getDiscordMemberId().equals( discordMemberId ) )
			.toList();
	}
}
