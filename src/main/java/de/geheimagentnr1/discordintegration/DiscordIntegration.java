package de.geheimagentnr1.discordintegration;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;


@SuppressWarnings( "UtilityClassWithPublicConstructor" )
@Mod( DiscordIntegration.MODID )
public class DiscordIntegration {
	
	
	//package-private
	static final String MODID = "discordintegration";
	
	public DiscordIntegration() {
		
		ModLoadingContext.get().registerConfig( ModConfig.Type.SERVER, ServerConfig.CONFIG );
		ModLoadingContext.get().registerExtensionPoint(
			ExtensionPoint.DISPLAYTEST,
			() -> Pair.of(
				() -> FMLNetworkConstants.IGNORESERVERONLY,
				( remote, isServer ) -> true
			)
		);
	}
}
