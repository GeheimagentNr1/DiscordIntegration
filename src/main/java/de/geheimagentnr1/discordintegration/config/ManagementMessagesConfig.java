package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;


public class ManagementMessagesConfig {
	
	
	private final MessageConfig server_started;
	
	private final MessageConfig server_stopped;
	
	private final MessageConfig server_crashed;
	
	private final MessageConfig player_joined;
	
	private final MessageConfig player_left;
	
	private final MessageConfig linking_created;
	
	private final MessageConfig linking_removed;
	
	private final MessageConfig whitelist_update_with_forced_message_update_start;
	
	private final MessageConfig whitelist_update_with_forced_message_update_finished;
	
	private final MessageConfig player_whitelist_added;
	
	private final MessageConfig player_whitelist_removed;
	
	//package-private
	ManagementMessagesConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Messages shown on Discord in the management channel" )
			.push( "messages" );
		server_started = new MessageConfig(
			builder,
			"server_started",
			"Options for the server start message",
			"Should a message be sent to the Discord management channel, if the server started?",
			"Message sent to the Discord management channel, if the Minecraft server started.",
			"Server started"
		);
		server_stopped = new MessageConfig(
			builder,
			"server_stopped",
			"Options for the server stop message",
			"Should a message be sent to the Discord management channel, if the server stopped?",
			"Message sent to the Discord management channel, if the Minecraft server stopped.",
			"Server stopped"
		);
		server_crashed = new MessageConfig(
			builder,
			"server_crashed",
			"Options for the server crash message",
			"Should a message be sent to the Discord management channel, if the server crashed?",
			"Message sent to the Discord management channel, if the Minecraft server crashed.",
			"Server crashed"
		);
		player_joined = new MessageConfig(
			builder,
			"player_joined",
			"Options for the player joined message",
			"Should a message be sent to the Discord management channel, if a player joined?",
			"Message sent to the Discord management channel, if a player joined. " +
				"(Available parameters: %player% = Player name)",
			"**%player%** joined the game."
		);
		player_left = new MessageConfig(
			builder,
			"player_left",
			"Options for the player left message",
			"Should a message be sent to the Discord management channel, if a player left?",
			"Message sent to the Discord management channel, if a player left the server. " +
				"(Available parameters: %player% = Player name)",
			"**%player%** disconnected."
		);
		linking_created = new MessageConfig(
			builder,
			"linking_created",
			"Options for the linking created message",
			"Should a message be sent to the Discord management channel, if the a linking is created?",
			"Message sent to the Discord management channel, if a linking is created. " +
				"(Available parameters: %player% = Player name, %username% = Discord username, " +
				"%nickname% = Discord nickname)",
			"**%player%** added to linkings of discord user **%username%**"
		);
		linking_removed = new MessageConfig(
			builder,
			"linking_removed",
			"Options for the linking removed message",
			"Should a message be sent to the Discord management channel, if the a linking is removed?",
			"Message sent to the Discord management channel, if a linking is removed. " +
				"(Available parameters: %player% = Player name, %username% = Discord username, " +
				"%nickname% = Discord nickname)",
			"**%player%** removed from linkings of discord user **%username%**"
		);
		whitelist_update_with_forced_message_update_start = new MessageConfig(
			builder,
			"whitelist_update_with_forced_message_update_start",
			"Options for the whitelist update with forced message update start message",
			"Should a message be sent to the Discord management channel, if the process of updating the whitelist  " +
				"with a forced update of all linking messages is started?",
			"Message sent to the Discord management channel, if the process of updating the whitelist  with a " +
				"forced update of all linking messages is started.",
			"Start whitelist update with forced message update"
		);
		whitelist_update_with_forced_message_update_finished = new MessageConfig(
			builder,
			"whitelist_update_with_forced_message_update_finished",
			"Options for the whitelist update with forced message update finished message",
			"Should a message be sent to the Discord management channel, if the process of updating the whitelist  " +
				"with a forced update of all linking messages is finished?",
			"Message sent to the Discord management channel, if the process of updating the whitelist  with a " +
				"forced update of all linking messages is finished.",
			"Finished whitelist update with forced message update"
		);
		player_whitelist_added = new MessageConfig(
			builder,
			"player_whitelist_added",
			"Options for the player to whitelist added message",
			"Should a message be sent to the Discord management channel, if a player is added to the whitelist, " +
				"because of a linking?",
			"Message sent to the Discord management channel, if a player is added to the whitelist, " +
				"because of a linking. (Available parameters: %player% = Player name)",
			"**%player%** added to whitelist"
		);
		player_whitelist_removed = new MessageConfig(
			builder,
			"player_whitelist_removed",
			"Options for the player from whitelist removed message",
			"Should a message be sent to the Discord management channel, if a player is removed from the " +
				"whitelist, because of a linking?",
			"Message sent to the Discord management channel, if a player is added to the whitelist, " +
				"because of a linking. (Available parameters: %player% = Player name)",
			"**%player%** removed from the whitelist"
		);
		builder.pop();
	}
	
	public MessageConfig getServerStarted() {
		
		return server_started;
	}
	
	public MessageConfig getServerStopped() {
		
		return server_stopped;
	}
	
	public MessageConfig getServerCrashed() {
		
		return server_crashed;
	}
	
	public MessageConfig getPlayerJoined() {
		
		return player_joined;
	}
	
	public MessageConfig getPlayerLeft() {
		
		return player_left;
	}
	
	public MessageConfig getLinkingCreated() {
		
		return linking_created;
	}
	
	public MessageConfig getLinkingRemoved() {
		
		return linking_removed;
	}
	
	public MessageConfig getWhitelistUpdateWithForcedMessageUpdateStart() {
		
		return whitelist_update_with_forced_message_update_start;
	}
	
	public MessageConfig getWhitelistUpdateWithForcedMessageUpdateFinished() {
		
		return whitelist_update_with_forced_message_update_finished;
	}
	
	public MessageConfig getPlayerWhitelistAdded() {
		
		return player_whitelist_added;
	}
	
	public MessageConfig getPlayerWhitelistRemoved() {
		
		return player_whitelist_removed;
	}
	
	//package-private
	@SuppressWarnings( "DuplicatedCode" )
	void printConfig( Logger logger ) {
		
		server_started.printConfig( logger );
		server_stopped.printConfig( logger );
		server_crashed.printConfig( logger );
		player_joined.printConfig( logger );
		player_left.printConfig( logger );
		linking_created.printConfig( logger );
		linking_removed.printConfig( logger );
		whitelist_update_with_forced_message_update_start.printConfig( logger );
		whitelist_update_with_forced_message_update_finished.printConfig( logger );
		player_whitelist_added.printConfig( logger );
		player_whitelist_removed.printConfig( logger );
	}
}