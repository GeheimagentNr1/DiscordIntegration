package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;


@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER )
public class ModEventHandler {
	
	
	@SubscribeEvent
	public static void handleModConfigLoadingEvent( ModConfig.Loading event ) {
		
		ServerConfig.handleConfigEvent();
	}
	
	@SubscribeEvent
	public static void handleModConfigReloadingEvent( ModConfig.ConfigReloading event ) {
		
		ServerConfig.handleConfigEvent();
	}
}
