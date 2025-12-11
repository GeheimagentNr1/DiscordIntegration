package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.api.AbstractMod;
import de.geheimagentnr1.discordintegration.api.config.AbstractConfig;
import lombok.extern.log4j.Log4j2;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;


@Log4j2
public class ServerConfig extends AbstractConfig {
	
	
	@NotNull
	private static final String BOT_CONFIG_KEY = "bot";
	
	@NotNull
	private static final String CHAT_CONFIG_KEY = "chat";
	
	@NotNull
	private static final String MANAGEMENT_CONFIG_KEY = "management";
	
	@NotNull
	private static final String WHITELIST_CONFIG_KEY = "whitelist";
	
	@NotNull
	private static final String COMMAND_SETTINGS_CONFIG_KEY = "command_settings";
	
	@NotNull
	private final DiscordManager discordManager;
	
	public ServerConfig( @NotNull AbstractMod _abstractMod, @NotNull DiscordManager _discordManager ) {
		
		super( _abstractMod );
		discordManager = _discordManager;
	}
	
	@NotNull
	@Override
	public ModConfig.Type type() {
		
		return ModConfig.Type.SERVER;
	}
	
	@Override
	public boolean isEarlyLoad() {
		
		return false;
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerSubConfig( "General bot configuration", BOT_CONFIG_KEY, new BotConfig( abstractMod, this ) );
		registerSubConfig( "Chat channel settings", CHAT_CONFIG_KEY, new ChatConfig( abstractMod, this ) );
		registerSubConfig(
			"Management channel settings",
			MANAGEMENT_CONFIG_KEY,
			new ManagementConfig( abstractMod, this )
		);
		registerSubConfig(
			"Settings for whitelist management in Discord",
			WHITELIST_CONFIG_KEY,
			new WhitelistConfig( abstractMod, this )
		);
		registerSubConfig(
			"Command settings",
			COMMAND_SETTINGS_CONFIG_KEY,
			new CommandSettingsConfig( abstractMod, this )
		);
	}
	
	@Override
	protected void handleConfigChanging() {
		
		// Re-initialize Discord connection when config is reloaded
		// Note: Initial init is done in ServerStartedEvent, not here
		if( discordManager.isInitialized() ) {
			discordManager.init();
		}
	}
	
	@NotNull
	public BotConfig getBotConfig() {
		
		return getSubConfig( BotConfig.class, BOT_CONFIG_KEY );
	}
	
	@NotNull
	public ChatConfig getChatConfig() {
		
		return getSubConfig( ChatConfig.class, CHAT_CONFIG_KEY );
	}
	
	@NotNull
	public ManagementConfig getManagementConfig() {
		
		return getSubConfig( ManagementConfig.class, MANAGEMENT_CONFIG_KEY );
	}
	
	@NotNull
	public WhitelistConfig getWhitelistConfig() {
		
		return getSubConfig( WhitelistConfig.class, WHITELIST_CONFIG_KEY );
	}
	
	@NotNull
	public CommandSettingsConfig getCommandSettingsConfig() {
		
		return getSubConfig( CommandSettingsConfig.class, COMMAND_SETTINGS_CONFIG_KEY );
	}
}
