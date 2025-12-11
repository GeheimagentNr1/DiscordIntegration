package de.geheimagentnr1.discordintegration.api.config;

import de.geheimagentnr1.discordintegration.api.AbstractMod;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;


@Log4j2
public abstract class AbstractConfig extends AbstractSubConfig {
	
	
	@NotNull
	@Getter
	private final ModConfigSpec spec;
	
	protected AbstractConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod, null, new ModConfigSpec.Builder() );
		spec = getBuilder().build();
	}
	
	@Override
	public boolean isLoaded() {
		
		return spec.isLoaded();
	}
	
	public void load() {
		
		// In NeoForge, configs are loaded automatically by the framework
		// This method is kept for API compatibility but does nothing
	}
	
	@NotNull
	public abstract ModConfig.Type type();
	
	public abstract boolean isEarlyLoad();
	
	public void handleServerStartingEvent( @NotNull ServerStartingEvent event ) {
		
		if( isEarlyLoad() && type() == ModConfig.Type.SERVER ) {
			load();
		}
	}
	
	public void handleModConfigLoadingEvent( @NotNull ModConfigEvent.Loading event ) {
		
		log.info( "\"{}\" {} Config loaded", abstractMod.getModName(), type() );
		// Note: Config values are not accessible during the Loading event in NeoForge
		// handleConfigChanging() and handleConfigLoading() should be called later
		// (e.g., in ServerStartedEvent) when config values are actually available
		handleConfigLoading();
	}
	
	public void handleModConfigReloadingEvent( @NotNull ModConfigEvent.Reloading event ) {
		
		log.info( "\"{}\" {} Config reloaded", abstractMod.getModName(), type() );
		handleConfigChanging();
		handleConfigReloading();
	}
	
	protected void handleConfigChanging() {
		
		//NOOP
	}
	
	protected void handleConfigLoading() {
		
		//NOOP
	}
	
	protected void handleConfigReloading() {
		
		//NOOP
	}
}
