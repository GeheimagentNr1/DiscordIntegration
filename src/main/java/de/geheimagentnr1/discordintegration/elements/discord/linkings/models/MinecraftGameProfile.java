package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import com.mantledillusion.essentials.json.patch.ignore.NoPatch;
import com.mojang.authlib.GameProfile;
import lombok.*;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode( onlyExplicitlyIncluded = true )
public class MinecraftGameProfile {
	
	
	@NoPatch
	@EqualsAndHashCode.Include
	private UUID uuid;
	
	private String name;
	
	public MinecraftGameProfile( GameProfile gameProfile ) {
		
		uuid = gameProfile.getId();
		name = gameProfile.getName();
	}
	
	public GameProfile buildGameProfile() {
		
		return new GameProfile( uuid, name );
	}
}
