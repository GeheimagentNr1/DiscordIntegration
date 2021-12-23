package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import com.mojang.authlib.GameProfile;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.MinecraftGameProfile;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraftforge.server.ServerLifecycleHooks;


@Slf4j
//package-private
class WhitelistManager {
	
	
	//package-private
	static void addToWhitelist( MinecraftGameProfile minecraftGameProfile ) {
		
		MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
		UserWhiteList userWhiteList = minecraftServer.getPlayerList().getWhiteList();
		GameProfile gameProfile = minecraftGameProfile.buildGameProfile();
		if( !userWhiteList.isWhiteListed( gameProfile ) ) {
			userWhiteList.add( new UserWhiteListEntry( gameProfile ) );
			log.info( "Added {} to whitelist", minecraftGameProfile.getName() );
			if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getPlayerWhitelistAdded().isEnabled() ) {
				ManagementManager.sendWhitelistMessage(
					minecraftGameProfile,
					ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getPlayerWhitelistAdded().getMessage()
				);
			}
		}
	}
	
	//package-private
	static void removeFromWhitelist( MinecraftGameProfile minecraftGameProfile ) {
		
		MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
		UserWhiteList userWhiteList = minecraftServer.getPlayerList().getWhiteList();
		GameProfile gameProfile = minecraftGameProfile.buildGameProfile();
		if( userWhiteList.isWhiteListed( gameProfile ) ) {
			userWhiteList.remove( new UserWhiteListEntry( gameProfile ) );
			log.info( "Removed {} from whitelist", minecraftGameProfile.getName() );
			if( ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig().getPlayerWhitelistRemoved().isEnabled() ) {
				ManagementManager.sendWhitelistMessage(
					minecraftGameProfile,
					ServerConfig.MANAGEMENT_CONFIG.getManagementMessagesConfig()
						.getPlayerWhitelistRemoved()
						.getMessage()
				);
			}
		}
	}
}
