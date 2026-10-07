package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import com.mantledillusion.essentials.json.patch.PatchUtil;
import com.mantledillusion.essentials.json.patch.ignore.NoPatch;
import com.mantledillusion.essentials.json.patch.model.Patch;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode( onlyExplicitlyIncluded = true )
public class Linking {
	
	
	@NoPatch
	@EqualsAndHashCode.Include
	private Long discordMemberId;
	
	private String discordUsername;
	
	private String discordNickname;
	
	private boolean hasRole;
	
	private boolean active;
	
	private Long messageId;
	
	@EqualsAndHashCode.Include
	private MinecraftGameProfile minecraftGameProfile;
	
	public static Linking applyPatches( Linking existingLinking, List<Patch> patches ) {
		
		return PatchUtil.apply( existingLinking, patches );
	}
}
