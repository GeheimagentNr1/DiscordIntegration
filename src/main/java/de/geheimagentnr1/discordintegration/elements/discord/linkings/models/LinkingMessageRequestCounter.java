package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Getter
@RequiredArgsConstructor
public class LinkingMessageRequestCounter {
	
	
	@NotNull
	private final String discordName;
	
	@NotNull
	private final String minecraftName;
	
	@NotNull
	private final List<String> requests = new ArrayList<>();
	
	private long count = 0;
	
	public void addRequest( @NotNull String request ) {
		
		count++;
		requests.add( request );
	}
	
	@NotNull
	public String getRequestsString() {
		
		return String.join( ", ", requests );
	}
}
