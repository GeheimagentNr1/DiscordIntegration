package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.elements.discord.AbstractDiscordIntegrationServiceProvider;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.MinecraftGameProfile;
import de.geheimagentnr1.minecraft_forge_api.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


@Getter( AccessLevel.PROTECTED )
@Log4j2
@RequiredArgsConstructor
public class WhitelistManager extends AbstractDiscordIntegrationServiceProvider {
	
	
	@NotNull
	private final DiscordIntegration discordIntegration;
	
	void addToWhitelist( @NotNull MinecraftGameProfile minecraftGameProfile ) {
		
		MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
		PlayerList playerList = minecraftServer.getPlayerList();
		UserWhiteList userWhiteList = playerList.getWhiteList();
		GameProfile gameProfile = minecraftGameProfile.buildGameProfile();
		if( !userWhiteList.isWhiteListed( gameProfile ) ) {
			userWhiteList.add( new UserWhiteListEntry( gameProfile ) );
			log.info( "Added {} to whitelist", minecraftGameProfile.getName() );
			if( serverConfig().getManagementConfig()
				.getManagementMessagesConfig()
				.getPlayerWhitelistAdded()
				.isEnabled() ) {
				managementManager().sendMessage(
					MessageUtil.replaceParameters(
						serverConfig().getManagementConfig().getManagementMessagesConfig()
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
	
	void removeFromWhitelist( @NotNull MinecraftGameProfile minecraftGameProfile ) {
		
		MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
		PlayerList playerList = minecraftServer.getPlayerList();
		UserWhiteList userWhiteList = playerList.getWhiteList();
		GameProfile gameProfile = minecraftGameProfile.buildGameProfile();
		if( userWhiteList.isWhiteListed( gameProfile ) ) {
			userWhiteList.remove( new UserWhiteListEntry( gameProfile ) );
			log.info( "Removed {} from whitelist", minecraftGameProfile.getName() );
			if( serverConfig().getManagementConfig()
				.getManagementMessagesConfig()
				.getPlayerWhitelistRemoved()
				.isEnabled() ) {
				managementManager().sendMessage(
					MessageUtil.replaceParameters(
						serverConfig().getManagementConfig().getManagementMessagesConfig()
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
	
	private void kickNotWhitelistedPlayers(
		@NotNull MinecraftServer minecraftServer,
		@NotNull PlayerList playerList,
		@NotNull UserWhiteList userWhiteList ) {
		
		minecraftServer.submitAsync( () -> {
			if( minecraftServer.isEnforceWhitelist() ) {
				for( ServerPlayer serverPlayer : Lists.newArrayList( playerList.getPlayers() ) ) {
					if( !userWhiteList.isWhiteListed( serverPlayer.getGameProfile() ) ) {
						serverPlayer.connection.disconnect(
							Component.translatable( "multiplayer.disconnect.not_whitelisted" )
						);
					}
				}
			}
		} ).join();
	}
}
