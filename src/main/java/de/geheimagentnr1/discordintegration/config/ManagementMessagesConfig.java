package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class ManagementMessagesConfig extends AbstractSubConfig {
	
	
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
	private static final String LINKING_CREATED_KEY = "linking_created";
	
	@NotNull
	private static final String LINKING_REMOVED_KEY = "linking_removed";
	
	@NotNull
	private static final String WHITELIST_UPDATE_WITH_FORCED_MESSAGE_UPDATE_START_KEY =
		"whitelist_update_with_forced_message_update_start";
	
	@NotNull
	private static final String WHITELIST_UPDATE_WITH_FORCED_MESSAGE_UPDATE_FINISHED_KEY =
		"whitelist_update_with_forced_message_update_finished";
	
	@NotNull
	private static final String PLAYER_WHITELIST_ADDED_KEY = "player_whitelist_added";
	
	@NotNull
	private static final String PLAYER_WHITELIST_REMOVED_KEY = "player_whitelist_removed";
	
	ManagementMessagesConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerSubConfig(
			"Options for the server start message",
			SERVER_STARTED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if the server started?",
				"Message sent to the Discord management channel, if the Minecraft server started.",
				"Server started"
			)
		);
		registerSubConfig(
			"Options for the server stop message",
			SERVER_STOPPED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if the server stopped?",
				"Message sent to the Discord management channel, if the Minecraft server stopped.",
				"Server stopped"
			)
		);
		registerSubConfig(
			"Options for the server crash message",
			SERVER_CRASHED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if the server crashed?",
				"Message sent to the Discord management channel, if the Minecraft server crashed.",
				"Server crashed"
			)
		);
		registerSubConfig(
			"Options for the player joined message",
			PLAYER_JOINED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if a player joined?",
				"Message sent to the Discord management channel, if a player joined. " +
					"(Available parameters: %player% = Player name)",
				"**%player%** joined the game."
			)
		);
		registerSubConfig(
			"Options for the player left message",
			PLAYER_LEFT_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if a player left?",
				"Message sent to the Discord management channel, if a player left the server. " +
					"(Available parameters: %player% = Player name)",
				"**%player%** disconnected."
			)
		);
		registerSubConfig(
			"Options for the linking created message",
			LINKING_CREATED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if the a linking is created?",
				"Message sent to the Discord management channel, if a linking is created. " +
					"(Available parameters: %player% = Player name, %username% = Discord username, " +
					"%nickname% = Discord nickname)",
				"**%player%** added to linkings of discord user **%username%**"
			)
		);
		registerSubConfig(
			"Options for the linking removed message",
			LINKING_REMOVED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if the a linking is removed?",
				"Message sent to the Discord management channel, if a linking is removed. " +
					"(Available parameters: %player% = Player name, %username% = Discord username, " +
					"%nickname% = Discord nickname)",
				"**%player%** removed from linkings of discord user **%username%**"
			)
		);
		registerSubConfig(
			"Options for the whitelist update with forced message update start message",
			WHITELIST_UPDATE_WITH_FORCED_MESSAGE_UPDATE_START_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if the process of updating the " +
					"whitelist" +
					" " +
					" " +
					"with a forced update of all linking messages is started?",
				"Message sent to the Discord management channel, if the process of updating the whitelist  with a " +
					"forced update of all linking messages is started.",
				"Start whitelist update with forced message update"
			)
		);
		registerSubConfig(
			"Options for the whitelist update with forced message update finished message",
			WHITELIST_UPDATE_WITH_FORCED_MESSAGE_UPDATE_FINISHED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if the process of updating the " +
					"whitelist" +
					" " +
					" " +
					"with a forced update of all linking messages is finished?",
				"Message sent to the Discord management channel, if the process of updating the whitelist  with a " +
					"forced update of all linking messages is finished.",
				"Finished whitelist update with forced message update"
			)
		);
		registerSubConfig(
			"Options for the player to whitelist added message",
			PLAYER_WHITELIST_ADDED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if a player is added to the whitelist, " +
					"because of a linking?",
				"Message sent to the Discord management channel, if a player is added to the whitelist, " +
					"because of a linking. (Available parameters: %player% = Player name)",
				"**%player%** added to whitelist"
			)
		);
		registerSubConfig(
			"Options for the player from whitelist removed message",
			PLAYER_WHITELIST_REMOVED_KEY,
			new MessageConfig(
				abstractMod,
				"Should a message be sent to the Discord management channel, if a player is removed from the " +
					"whitelist, because of a linking?",
				"Message sent to the Discord management channel, if a player is added to the whitelist, " +
					"because of a linking. (Available parameters: %player% = Player name)",
				"**%player%** removed from the whitelist"
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
	public MessageConfig getLinkingCreated() {
		
		return getSubConfig( MessageConfig.class, LINKING_CREATED_KEY );
	}
	
	@NotNull
	public MessageConfig getLinkingRemoved() {
		
		return getSubConfig( MessageConfig.class, LINKING_REMOVED_KEY );
	}
	
	@NotNull
	public MessageConfig getWhitelistUpdateWithForcedMessageUpdateStart() {
		
		return getSubConfig( MessageConfig.class, WHITELIST_UPDATE_WITH_FORCED_MESSAGE_UPDATE_START_KEY );
	}
	
	@NotNull
	public MessageConfig getWhitelistUpdateWithForcedMessageUpdateFinished() {
		
		return getSubConfig( MessageConfig.class, WHITELIST_UPDATE_WITH_FORCED_MESSAGE_UPDATE_FINISHED_KEY );
	}
	
	@NotNull
	public MessageConfig getPlayerWhitelistAdded() {
		
		return getSubConfig( MessageConfig.class, PLAYER_WHITELIST_ADDED_KEY );
	}
	
	@NotNull
	public MessageConfig getPlayerWhitelistRemoved() {
		
		return getSubConfig( MessageConfig.class, PLAYER_WHITELIST_REMOVED_KEY );
	}
}
