package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import com.mantledillusion.essentials.json.patch.ignore.NoPatch;
import lombok.*;
import org.jetbrains.annotations.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode( onlyExplicitlyIncluded = true )
public class Linking {
	
	
	@NoPatch
	@EqualsAndHashCode.Include
	@NotNull
	private Long discordMemberId;
	
	private String discordUsername;
	
	private String discordNickname;
	
	private boolean hasRole;
	
	private boolean active;
	
	private Long messageId;
	
	@EqualsAndHashCode.Include
	@NotNull
	private MinecraftGameProfile minecraftGameProfile;
}
