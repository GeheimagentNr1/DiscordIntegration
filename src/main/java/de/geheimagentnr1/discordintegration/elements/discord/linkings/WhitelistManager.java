package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.MinecraftGameProfile;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import de.geheimagentnr1.discordintegration.util.MessageUtil;
import lombok.extern.log4j.Log4j2;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;


//package-private
@Log4j2
class WhitelistManager {
	
	
	//package-private
	static void addToWhitelist( MinecraftGameProfile minecraftGameProfile ) {
		
		MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
		PlayerList playerList = minecraftServer.getPlayerList();
		UserWhiteList userWhiteList = playerList.getWhiteList();
		GameProfile gameProfile = minecraftGameProfile.buildGameProfile();
		if( !userWhiteList.isWhiteListed( gameProfile ) ) {
			userWhiteList.add( new UserWhiteListEntry( gameProfile ) );
			log.info( "Added {} to whitelist", minecraftGameProfile.getName() );
			if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getPlayerWhitelistAdded().isEnabled() ) {
				ManagementManager.sendMessage(
					MessageUtil.replaceParameters(
						ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig()
							.getPlayerWhitelistAdded()
							.getMessage(),
						Map.of(
							"player", minecraftGameProfile.getName()
						)
					)
				);
			}
			kickNotWhitelistedPlayers( minecraftServer, playerList, userWhiteList );
		}
	}
	
	//package-private
	static void removeFromWhitelist( MinecraftGameProfile minecraftGameProfile ) {
		
		MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
		PlayerList playerList = minecraftServer.getPlayerList();
		UserWhiteList userWhiteList = playerList.getWhiteList();
		GameProfile gameProfile = minecraftGameProfile.buildGameProfile();
		if( userWhiteList.isWhiteListed( gameProfile ) ) {
			userWhiteList.remove( new UserWhiteListEntry( gameProfile ) );
			log.info( "Removed {} from whitelist", minecraftGameProfile.getName() );
			if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getPlayerWhitelistRemoved().isEnabled() ) {
				ManagementManager.sendMessage(
					MessageUtil.replaceParameters(
						ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig()
							.getPlayerWhitelistRemoved()
							.getMessage(),
						Map.of(
							"player", minecraftGameProfile.getName()
						)
					)
				);
			}
			kickNotWhitelistedPlayers( minecraftServer, playerList, userWhiteList );
		}
	}
	
	private static void kickNotWhitelistedPlayers(
		MinecraftServer minecraftServer,
		PlayerList playerList,
		UserWhiteList userWhiteList ) {
		
		minecraftServer.submitAsync( () -> {
			if( minecraftServer.isEnforceWhitelist() ) {
				for( ServerPlayer serverPlayer : Lists.newArrayList( playerList.getPlayers() ) ) {
					if( !userWhiteList.isWhiteListed( serverPlayer.getGameProfile() ) ) {
						serverPlayer.connection.disconnect(
							new TranslatableComponent( "multiplayer.disconnect.not_whitelisted" )
						);
					}
				}
			}
		} ).join();
	}
}
