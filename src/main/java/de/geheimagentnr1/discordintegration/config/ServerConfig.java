package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractConfig;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.config.ModConfig;
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
		
		registerSubConfig( "General bot configuration", BOT_CONFIG_KEY, new BotConfig( abstractMod ) );
		registerSubConfig( "Chat channel settings", CHAT_CONFIG_KEY, new ChatConfig( abstractMod ) );
		registerSubConfig( "Management channel settings", MANAGEMENT_CONFIG_KEY, new ManagementConfig( abstractMod ) );
		registerSubConfig(
			"Settings for whitelist management in Discord",
			WHITELIST_CONFIG_KEY,
			new WhitelistConfig( abstractMod )
		);
		registerSubConfig( "Command settings", COMMAND_SETTINGS_CONFIG_KEY, new CommandSettingsConfig( abstractMod ) );
	}
	
	@Override
	protected void handleConfigChanging() {
		
		discordManager.init();
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
