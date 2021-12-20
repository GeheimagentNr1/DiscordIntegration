package de.geheimagentnr1.discordintegration.elements.commands.arguments;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.elements.commands.arguments.single_game_profile.SingleGameProfileArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;


public class ModArgumentTypes {
	
	
	public static void registerArgumentTypes() {
		
		ArgumentTypes.register(
			DiscordIntegration.MODID + ":" + SingleGameProfileArgument.registry_name,
			SingleGameProfileArgument.class,
			new EmptyArgumentSerializer<>( SingleGameProfileArgument::new )
		);
	}
}
