package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import com.mantledillusion.essentials.json.patch.ignore.NoPatch;
import com.mojang.authlib.GameProfile;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode( onlyExplicitlyIncluded = true )
public class MinecraftGameProfile {
	
	
	@NoPatch
	@EqualsAndHashCode.Include
	@NotNull
	private UUID uuid;
	
	@NotNull
	private String name;
	
	public MinecraftGameProfile( GameProfile gameProfile ) {
		
		uuid = gameProfile.getId();
		name = gameProfile.getName();
	}
	
	public GameProfile buildGameProfile() {
		
		return new GameProfile( uuid, name );
	}
}
