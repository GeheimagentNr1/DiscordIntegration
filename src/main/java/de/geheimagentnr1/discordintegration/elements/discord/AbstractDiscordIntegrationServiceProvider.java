package de.geheimagentnr1.discordintegration.elements.discord;


import de.geheimagentnr1.discordintegration.DiscordIntegration;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsFileManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManagementMessageManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.WhitelistManager;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings( "ClassReferencesSubclass" )
public abstract class AbstractDiscordIntegrationServiceProvider {
	
	
	@NotNull
	private static final String SERVER_CONFIG_NOT_FOUND_ERROR_MESSAGE = "DiscordIntegration ServerConfig not found";
	
	@Nullable
	private ServerConfig serverConfig;
	
	@NotNull
	protected abstract DiscordIntegration getDiscordIntegration();
	
	@NotNull
	protected ServerConfig serverConfig() {
		
		if( serverConfig == null ) {
			serverConfig = getDiscordIntegration().getConfig( ModConfig.Type.SERVER, ServerConfig.class )
				.orElseThrow( () -> new IllegalStateException( SERVER_CONFIG_NOT_FOUND_ERROR_MESSAGE ) );
		}
		return serverConfig;
	}
	
	// Discord: Chat
	@NotNull
	ChatManager chatManager() {
		
		return getDiscordIntegration().getChatManager();
	}
	
	// Discord: Commands
	@NotNull
	DiscordCommandHandler discordCommandHandler() {
		
		return getDiscordIntegration().getDiscordCommandHandler();
	}
	
	// Discord: Linkings
	@NotNull
	protected LinkingsFileManager linkingsFileManager() {
		
		return getDiscordIntegration().getLinkingsFileManager();
	}
	
	@NotNull
	protected LinkingsManagementMessageManager linkingsManagementMessageManager() {
		
		return getDiscordIntegration().getLinkingsManagementMessageManager();
	}
	
	@NotNull
	protected LinkingsManager linkingsManager() {
		
		return getDiscordIntegration().getLinkingsManager();
	}
	
	@NotNull
	protected WhitelistManager whitelistManager() {
		
		return getDiscordIntegration().getWhitelistManager();
	}
	
	// Discord: Management
	@NotNull
	protected ManagementManager managementManager() {
		
		return getDiscordIntegration().getManagementManager();
	}
	
	// Discord
	@NotNull
	protected DiscordManager discordManager() {
		
		return getDiscordIntegration().getDiscordManager();
	}
	
	@NotNull
	protected DiscordMessageBuilder discordMessageBuilder() {
		
		return getDiscordIntegration().getDiscordMessageBuilder();
	}
	
	@NotNull
	protected DiscordMessageSender discordMessageSender() {
		
		return getDiscordIntegration().getDiscordMessageSender();
	}
}
