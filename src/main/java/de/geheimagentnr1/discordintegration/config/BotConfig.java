package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class BotConfig extends AbstractSubConfig {
	
	
	@NotNull
	private static final String ACTIVE_KEY = "active";
	
	@NotNull
	private static final String BOT_TOKEN_KEY = "bot_token";
	
	@NotNull
	private static final String GUILD_ID_KEY = "guild_id";
	
	@NotNull
	private static final String DISCORD_PRESENCE_KEY = "discord_presence";
	
	BotConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( "Should the Discord Integration be active?", ACTIVE_KEY, false );
		registerConfigValue( "Token of your Discord bot:", BOT_TOKEN_KEY, "INSERT BOT TOKEN HERE" );
		registerConfigValue(
			"Guild/Server ID of the Discord server, where the Discord Integration should operate.",
			GUILD_ID_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, Long.MAX_VALUE )
		);
		registerSubConfig(
			"Discord Presence configuration",
			DISCORD_PRESENCE_KEY,
			new DiscordPresenceConfig( abstractMod )
		);
	}
	
	@Override
	protected void printValues( @NotNull String prefix ) {
		
		printValues(
			prefix,
			Map.of(
				BOT_TOKEN_KEY,
				botToken -> StringUtils.leftPad( "", botToken.length(), '*' )
			)
		);
	}
	
	public boolean isActive() {
		
		return getValue( Boolean.class, ACTIVE_KEY );
	}
	
	@NotNull
	public String getBotToken() {
		
		return getValue( String.class, BOT_TOKEN_KEY );
	}
	
	public long getGuildId() {
		
		return getValue( Long.class, GUILD_ID_KEY );
	}
	
	@NotNull
	public DiscordPresenceConfig getDiscordPresenceConfig() {
		
		return getSubConfig( DiscordPresenceConfig.class, DISCORD_PRESENCE_KEY );
	}
}
