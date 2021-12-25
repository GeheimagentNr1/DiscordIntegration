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
	
	private String discordName;
	
	private boolean hasRole;
	
	private boolean active;
	
	private Long messageId;
	
	@EqualsAndHashCode.Include
	private MinecraftGameProfile minecraftGameProfile;
	
	/**
	 * Dummy fix because {@link com.mantledillusion.essentials.json.patch,PatchUtil#apply} didn't work with shadowing
	 */
	public static void applyPatches( Linking newlinking, Linking existingLinking, List<Patch> patches ) {
		
		for( Patch patch : patches ) {
			switch( patch.getPath() ) {
				case "/discordName" -> existingLinking.setDiscordName( newlinking.getDiscordName() );
				case "/hasRole" -> existingLinking.setHasRole( newlinking.isHasRole() );
				case "/active" -> existingLinking.setActive( newlinking.isActive() );
				case "/messageId" -> existingLinking.setMessageId( newlinking.getMessageId() );
				case "/minecraftGameProfile/name" -> existingLinking.getMinecraftGameProfile()
					.setName( newlinking.getMinecraftGameProfile().getName() );
			}
		}
	}
}
