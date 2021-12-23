package de.geheimagentnr1.discordintegration.elements.discord.linkings.models;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode( onlyExplicitlyIncluded = true )
public class Linking {
	
	
	@EqualsAndHashCode.Include
	private Long discordMemberId;
	
	private String discordName;
	
	private boolean active;
	
	private Long messageId;
	
	@EqualsAndHashCode.Include
	private MinecraftGameProfile minecraftGameProfile;
}
