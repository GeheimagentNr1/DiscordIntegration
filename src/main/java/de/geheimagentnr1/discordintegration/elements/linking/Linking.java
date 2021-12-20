package de.geheimagentnr1.discordintegration.elements.linking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Linking {
	
	
	private Long discordMemberId;
	
	private String discordName;
	
	@EqualsAndHashCode.Exclude
	private Long messageId;
	
	@EqualsAndHashCode.Exclude
	private boolean active;
	
	private SimpleGameProfile minecraftGameProfile;
}
