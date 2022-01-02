package de.geheimagentnr1.discordintegration;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.patch.PatchUtilClassLoadFixer;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;


@SuppressWarnings( "UtilityClassWithPublicConstructor" )
@Mod( DiscordIntegration.MODID )
public class DiscordIntegration {
	
	
	public static final String MODID = "discordintegration";
	
	public DiscordIntegration() {
		
		PatchUtilClassLoadFixer.fixPatchUtilClassLoading();
		
		ModLoadingContext.get().registerConfig( ModConfig.Type.SERVER, ServerConfig.CONFIG );
		ModLoadingContext.get().registerExtensionPoint(
			IExtensionPoint.DisplayTest.class,
			() -> new IExtensionPoint.DisplayTest(
				() -> NetworkConstants.IGNORESERVERONLY,
				( remote, isServer ) -> true
			)
		);
	}
}
