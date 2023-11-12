package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class ChatMessagesConfig extends AbstractSubConfig {
	
	
	@NotNull
	private static final String SERVER_STARTED_KEY = "server_started";
	
	@NotNull
	private static final String SERVER_STOPPED_KEY = "server_stopped";
	
	@NotNull
	private static final String SERVER_CRASHED_KEY = "server_crashed";
	
	@NotNull
	private static final String PLAYER_JOINED_KEY = "player_joined";
	
	@NotNull
	private static final String PLAYER_LEFT_KEY = "player_left";
	
	@NotNull
	private static final String PLAYER_DIED_KEY = "player_died";
	
	@NotNull
	private static final String TAMED_MOB_DIED_KEY = "tamed_mob_died";
	
	@NotNull
	private static final String PLAYER_GOT_ADVANCEMENT_KEY = "player_got_advancement";
	
	ChatMessagesConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerSubConfig(
			"Options for the server start message",
			SERVER_STARTED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord chat, if the server started?",
				"Message sent to the Discord chat, if the Minecraft server started.",
				"Server started"
			)
		);
		registerSubConfig(
			"Options for the server stop message",
			SERVER_STOPPED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord chat, if the server stopped?",
				"Message sent to the Discord chat, if the Minecraft server stopped.",
				"Server stopped"
			)
		);
		registerSubConfig(
			"Options for the server crash message",
			SERVER_CRASHED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord chat, if the server crashed?",
				"Message sent to the Discord chat, if the Minecraft server crashed.",
				"Server crashed"
			)
		);
		registerSubConfig(
			"Options for the player joined message",
			PLAYER_JOINED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord chat, if a player joined?",
				"Message sent to the Discord chat, if a player joined. " +
					"(Available parameters: %player% = Player name)",
				"**%player%** joined the game."
			)
		);
		registerSubConfig(
			"Options for the player left message",
			PLAYER_LEFT_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord chat, if a player left?",
				"Message sent to the Discord chat, if a player left the server. " +
					"(Available parameters: %player% = Player name)",
				"**%player%** disconnected."
			)
		);
		registerSubConfig(
			"Options for the player died message",
			PLAYER_DIED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord chat, if a player died?",
				"Message sent to the Discord chat, if a player died. " +
					"(Available parameters: %player% = Player name, " +
					"%default_message% = Default death message shown in the chat with bold player name)",
				"%default_message%"
			)
		);
		registerSubConfig(
			"Options for the tamed mob died message",
			TAMED_MOB_DIED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord chat, if a tamed mob left?",
				"Message sent to the Discord chat, if a tamed mob died. " +
					"(Available parameters: %tamed_mob% = Tamed mob name, " +
					"%default_message% = Default death message shown in the chat with bold tamed mob name)",
				"%default_message%"
			)
		);
		registerSubConfig(
			"Options for the player got advancement message",
			PLAYER_GOT_ADVANCEMENT_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord chat, if a player got an advancement?",
				"Message sent to the Discord chat, if a player got an advancement. " +
					"(Available parameters: %player% = Player name, " +
					"%advancement_title% = Advancement title, " +
					"%advancement_description% = Advancement description, " +
					"%new_line% = New line)",
				"**%player%** has made the advancement%new_line%" +
					"**%advancement_title%**%new_line%" +
					"*%advancement_description%*"
			)
		);
	}
	
	@NotNull
	public MessageConfig getServerStarted() {
		
		return getSubConfig( MessageConfig.class, SERVER_STARTED_KEY );
	}
	
	@NotNull
	public MessageConfig getServerStopped() {
		
		return getSubConfig( MessageConfig.class, SERVER_STOPPED_KEY );
	}
	
	@NotNull
	public MessageConfig getServerCrashed() {
		
		return getSubConfig( MessageConfig.class, SERVER_CRASHED_KEY );
	}
	
	@NotNull
	public MessageConfig getPlayerJoined() {
		
		return getSubConfig( MessageConfig.class, PLAYER_JOINED_KEY );
	}
	
	@NotNull
	public MessageConfig getPlayerLeft() {
		
		return getSubConfig( MessageConfig.class, PLAYER_LEFT_KEY );
	}
	
	@NotNull
	public MessageConfig getPlayerDied() {
		
		return getSubConfig( MessageConfig.class, PLAYER_DIED_KEY );
	}
	
	@NotNull
	public MessageConfig getTamedMobDied() {
		
		return getSubConfig( MessageConfig.class, TAMED_MOB_DIED_KEY );
	}
	
	@NotNull
	public MessageConfig getPlayerGotAdvancement() {
		
		return getSubConfig( MessageConfig.class, PLAYER_GOT_ADVANCEMENT_KEY );
	}
}
