package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter
@RequiredArgsConstructor
public class LinkingMessageRequestCounter {
	
	
	private final String discordName;
	
	private final String minecraftName;
	
	private final List<String> requests = new ArrayList<>();
	
	private long count = 0;
	
	public void addRequest( String request ) {
		
		count++;
		requests.add( request );
	}
	
	public String getRequestsString() {
		
		return String.join( ", ", requests );
	}
}
