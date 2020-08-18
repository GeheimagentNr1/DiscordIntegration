package de.geheimagentnr1.discordintegration;

import de.geheimagentnr1.discordintegration.config.MainConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;


@SuppressWarnings( { "UtilityClassWithPublicConstructor", "unused" } )
@Mod( DiscordIntegration.MODID )
public class DiscordIntegration {
	
	
	//package-private
	static final String MODID = "discordintegration";
	
	public DiscordIntegration() {
		
		ModLoadingContext.get().registerConfig( ModConfig.Type.COMMON, MainConfig.CONFIG, MODID + ".toml" );
	}
}
