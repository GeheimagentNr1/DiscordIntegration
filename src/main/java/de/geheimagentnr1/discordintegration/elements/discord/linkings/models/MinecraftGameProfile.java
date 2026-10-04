package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import com.mantledillusion.essentials.json.patch.ignore.NoPatch;
import lombok.*;
import net.minecraft.server.players.NameAndId;
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
	
	public MinecraftGameProfile( NameAndId nameAndId ) {
		
		uuid = nameAndId.id();
		name = nameAndId.name();
	}
	
	public NameAndId buildNameAndId() {
		
		return new NameAndId( uuid, name );
	}
}
