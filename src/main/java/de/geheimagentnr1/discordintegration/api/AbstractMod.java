package de.geheimagentnr1.discordintegration.api;

import de.geheimagentnr1.discordintegration.api.config.AbstractConfig;
import de.geheimagentnr1.discordintegration.api.events.ForgeEventHandlerInterface;
import de.geheimagentnr1.discordintegration.api.events.ModEventHandlerInterface;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


@Log4j2
public abstract class AbstractMod {
	
	
	@Getter
	@NotNull
	private final String modName;
	
	@NotNull
	private final IEventBus modEventBus;
	
	@NotNull
	private final ModContainer modContainer;
	
	@NotNull
	private final Map<ModConfig.Type, AbstractConfig> configs = new EnumMap<>( ModConfig.Type.class );
	
	protected AbstractMod( @NotNull IEventBus modEventBus, @NotNull ModContainer modContainer ) {
		
		this.modEventBus = modEventBus;
		this.modContainer = modContainer;
		this.modName = modContainer.getModInfo().getDisplayName();
		init();
	}
	
	private void init() {
		
		initMod();
	}
	
	@NotNull
	public abstract String getModId();
	
	protected abstract void initMod();
	
	@NotNull
	protected <T extends ForgeEventHandlerInterface> T registerEventHandler( @NotNull T handler ) {
		
		// Register events using addListener to avoid NeoForge restriction on @SubscribeEvent in superclasses
		IEventBus bus = forgeEventBus();
		bus.addListener( handler::handleServerStartingEvent );
		bus.addListener( handler::handleServerStartedEvent );
		bus.addListener( handler::handleServerStoppedEvent );
		bus.addListener( handler::handleRegisterCommandsEvent );
		bus.addListener( handler::handlePlayerLoggedInEvent );
		bus.addListener( handler::handlePlayerLoggedOutEvent );
		bus.addListener( handler::handleServerChatEvent );
		bus.addListener( handler::handleLivingDeathEvent );
		bus.addListener( handler::handleAdvancementEarnEvent );
		return handler;
	}
	
	@NotNull
	protected <T extends ModEventHandlerInterface> T registerEventHandler( @NotNull T handler ) {
		
		// Register events using addListener to avoid NeoForge restriction on @SubscribeEvent in superclasses
		modEventBus().register( handler );
		return handler;
	}
	
	@NotNull
	protected IEventBus forgeEventBus() {
		
		return NeoForge.EVENT_BUS;
	}
	
	@NotNull
	protected IEventBus modEventBus() {
		
		return modEventBus;
	}
	
	@NotNull
	protected <T extends AbstractConfig> T registerConfig( @NotNull Function<AbstractMod, T> builder ) {
		
		T config = builder.apply( this );
		ModConfig.Type configType = config.type();
		configs.put( configType, config );
		modContainer.registerConfig( configType, config.getSpec() );
		// Register config events using addListener instead of registering the object
		// This avoids the NeoForge restriction on @SubscribeEvent in superclasses
		modEventBus.addListener( config::handleModConfigLoadingEvent );
		modEventBus.addListener( config::handleModConfigReloadingEvent );
		NeoForge.EVENT_BUS.addListener( config::handleServerStartingEvent );
		if( config.isEarlyLoad() && configType != ModConfig.Type.SERVER ) {
			config.load();
		}
		return config;
	}
	
	@NotNull
	public <T> Optional<T> getConfig( @NotNull ModConfig.Type type, @NotNull Class<T> configClass ) {
		
		AbstractConfig config = configs.get( type );
		if( configClass.isInstance( config ) ) {
			return Optional.of( configClass.cast( config ) );
		}
		return Optional.empty();
	}
}
