package de.geheimagentnr1.discordintegration.handlers;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import de.geheimagentnr1.minecraft_forge_api.events.ForgeEventHandlerInterface;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class DiscordMessageHandler implements ForgeEventHandlerInterface {
	
	
	@NotNull
	private final DiscordNet discordNet;
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@SubscribeEvent
	@Override
	public void handleServerStartedEvent( @NotNull ServerStartedEvent event ) {
		
		if( serverConfig.getServerStartedMessageEnabled() ) {
			discordNet.sendMessage( serverConfig.getServerStartedMessage() );
		}
	}
	
	@SubscribeEvent
	@Override
	public void handleServerStoppedEvent( @NotNull ServerStoppedEvent event ) {
		
		if( event.getServer().isRunning() ) {
			if( serverConfig.getServerCrashedMessageEnabled() ) {
				discordNet.sendMessage( serverConfig.getServerCrashedMessage() );
			}
		} else {
			if( serverConfig.getServerStoppedMessageEnabled() ) {
				discordNet.sendMessage( serverConfig.getServerStoppedMessage() );
			}
		}
	}
	
	@SubscribeEvent
	@Override
	public void handlePlayerLoggedInEvent( @NotNull PlayerEvent.PlayerLoggedInEvent event ) {
		
		if( serverConfig.getPlayerJoinedMessageEnabled() ) {
			discordNet.sendPlayerMessage( event.getEntity(), serverConfig.getPlayerJoinedMessage() );
		}
	}
	
	@SubscribeEvent
	@Override
	public void handlePlayerLoggedOutEvent( @NotNull PlayerEvent.PlayerLoggedOutEvent event ) {
		
		if( serverConfig.getPlayerLeftMessageEnabled() ) {
			discordNet.sendPlayerMessage( event.getEntity(), serverConfig.getPlayerLeftMessage() );
		}
	}
	
	@SubscribeEvent
	@Override
	public void handleServerChatEvent( @NotNull ServerChatEvent event ) {
		
		if( !event.isCanceled() ) {
			discordNet.sendChatMessage( event.getPlayer(), event.getRawText() );
		}
	}
	
	@SubscribeEvent
	@Override
	public void handleLivingDeathEvent( @NotNull LivingDeathEvent event ) {
		
		LivingEntity entity = event.getEntity();
		
		if( entity instanceof Player ) {
			if( serverConfig.getPlayerDiedMessageEnabled() ) {
				discordNet.sendDeathMessage( event, serverConfig.getPlayerDiedMessage() );
			}
		} else {
			if( entity instanceof TamableAnimal && ( (TamableAnimal)entity ).getOwnerUUID() != null ) {
				if( serverConfig.getTamedMobDiedMessageEnabled() ) {
					discordNet.sendDeathMessage( event, serverConfig.getTamedMobDiedMessage() );
				}
			}
		}
	}
	
	@SubscribeEvent
	@Override
	public void handleAdvancementEarnEvent( @NotNull AdvancementEvent.AdvancementEarnEvent event ) {
		
		event.getAdvancement().value().display().ifPresent( displayInfo -> {
			if( displayInfo.shouldAnnounceChat() &&
				serverConfig.getPlayerGotAdvancementMessageEnabled() ) {
				discordNet.sendPlayerMessage(
					event.getEntity(),
					String.format(
						"%s **%s**%n*%s*",
						serverConfig.getPlayerGotAdvancementMessage(),
						displayInfo.getTitle().getString(),
						displayInfo.getDescription().getString()
					)
				);
			}
		} );
	}
}
