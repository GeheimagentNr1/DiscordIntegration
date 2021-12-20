package de.geheimagentnr1.discordintegration.elements.linking;

import com.mojang.authlib.GameProfile;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


//package-private
class WhitelistManager {
	
	
	private static final Logger LOGGER = LogManager.getLogger( WhitelistManager.class );
	
	
	//package-private
	static synchronized void addToWhitelist( SimpleGameProfile minecraftGameProfile ) {
		
		MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
		UserWhiteList userWhiteList = minecraftServer.getPlayerList().getWhiteList();
		GameProfile gameProfile = minecraftGameProfile.buildGameProfile();
		if( !userWhiteList.isWhiteListed( gameProfile ) ) {
			LOGGER.info( "Add {} to whitelist", minecraftGameProfile.getName() );
			userWhiteList.add( new UserWhiteListEntry( gameProfile ) );
			DiscordNet.sendMessage( String.format(
				"Add %s to whitelist",
				minecraftGameProfile.getName()
			) );
		}
	}
	
	//package-private
	static synchronized void removeFromWhitelist( SimpleGameProfile minecraftGameProfile ) {
		
		MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
		UserWhiteList userWhiteList = minecraftServer.getPlayerList().getWhiteList();
		GameProfile gameProfile = minecraftGameProfile.buildGameProfile();
		if( userWhiteList.isWhiteListed( gameProfile ) ) {
			LOGGER.info( "Removed {} from whitelist", minecraftGameProfile.getName() );
			userWhiteList.remove( new UserWhiteListEntry( gameProfile ) );
			DiscordNet.sendMessage( String.format(
				"Removed %s from whitelist",
				minecraftGameProfile.getName()
			) );
		}
	}
}
