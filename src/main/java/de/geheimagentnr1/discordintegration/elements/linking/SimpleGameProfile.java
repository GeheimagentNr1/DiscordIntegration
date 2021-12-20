package de.geheimagentnr1.discordintegration.elements.linking;

import com.mojang.authlib.GameProfile;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
public class SimpleGameProfile {
	
	
	private UUID uuid;
	
	private String name;
	
	public SimpleGameProfile( GameProfile gameProfile ) {
		
		uuid = gameProfile.getId();
		name = gameProfile.getName();
	}
	
	public GameProfile buildGameProfile() {
		
		return new GameProfile( uuid, name );
	}
}
