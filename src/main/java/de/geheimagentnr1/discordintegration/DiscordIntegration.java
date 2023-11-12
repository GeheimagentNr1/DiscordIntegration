package de.geheimagentnr1.discordintegration;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.commands.ModCommandsRegisterFactory;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageBuilder;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordMessageSender;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.discordintegration.elements.discord.commands.DiscordCommandHandler;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsFileManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManagementMessageManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.WhitelistManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.patch.PatchUtilClassLoadFixer;
import de.geheimagentnr1.discordintegration.elements.discord.management.ManagementManager;
import de.geheimagentnr1.discordintegration.handlers.DiscordMessageHandler;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;


@Getter
@Mod( DiscordIntegration.MODID )
public class DiscordIntegration extends AbstractMod {
	
	
	@NotNull
	static final String MODID = "discordintegration";
	
	// Discord: Chat
	private ChatManager chatManager;
	
	// Discord: Commands
	private DiscordCommandHandler discordCommandHandler;
	
	// Discord: Linkings
	private LinkingsFileManager linkingsFileManager;
	
	private LinkingsManagementMessageManager linkingsManagementMessageManager;
	
	private LinkingsManager linkingsManager;
	
	private WhitelistManager whitelistManager;
	
	// Discord: Management
	private ManagementManager managementManager;
	
	// Discord
	private DiscordManager discordManager;
	
	private DiscordMessageBuilder discordMessageBuilder;
	
	private DiscordMessageSender discordMessageSender;
	
	@NotNull
	@Override
	public String getModId() {
		
		return MODID;
	}
	
	@Override
	protected void initMod() {
		
		PatchUtilClassLoadFixer.fixPatchUtilClassLoading();
		
		DistExecutor.safeRunWhenOn(
			Dist.DEDICATED_SERVER,
			() -> () -> {
				
				// Discord
				discordManager = new DiscordManager( this );
				
				ServerConfig serverConfig = registerConfig(
					abstractMod -> new ServerConfig( abstractMod, discordManager )
				);
				
				// Discord: Chat
				chatManager = new ChatManager( this );
				
				// Discord: Commands
				discordCommandHandler = new DiscordCommandHandler( this );
				
				// Discord: Linkings
				linkingsFileManager = new LinkingsFileManager();
				linkingsManagementMessageManager = new LinkingsManagementMessageManager( this );
				linkingsManager = new LinkingsManager( this );
				whitelistManager = new WhitelistManager( this );
				
				// Discord: Management
				managementManager = new ManagementManager( this );
				
				// Discord
				discordMessageBuilder = new DiscordMessageBuilder( serverConfig );
				discordMessageSender = new DiscordMessageSender();
				
				// Commands
				registerEventHandler( new ModCommandsRegisterFactory(
					serverConfig,
					discordManager,
					chatManager,
					linkingsManager
				) );
				
				// Handels
				registerEventHandler( new DiscordMessageHandler(
					serverConfig,
					discordManager,
					chatManager,
					managementManager,
					discordMessageBuilder
				) );
			}
		);
	}
}
