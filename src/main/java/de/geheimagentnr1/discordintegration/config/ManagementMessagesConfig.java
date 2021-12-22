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
			"Message send to the Discord management channel, if the Minecraft server started.",
			"Server started"
		);
		server_stopped = new MessageConfig(
			builder,
			"server_stopped",
			"Options for the server stop message",
			"Should a message be sent to the Discord management channel, if the server stopped?",
			"Message send to the Discord management channel, if the Minecraft server stopped.",
			"Server stopped"
		);
		server_crashed = new MessageConfig(
			builder,
			"server_crashed",
			"Options for the server crash message",
			"Should a message be sent to the Discord management channel, if the server crashed?",
			"Message send to the Discord management channel, if the Minecraft server crashed.",
			"Server crashed"
		);
		player_joined = new MessageConfig(
			builder,
			"player_joined",
			"Options for the player joined message",
			"Should a message be sent to the Discord management channel, if a player joined?",
			"Message send to the Discord management channel, if a player joined. (<player name> <message>)",
			"joined the game."
		);
		player_left = new MessageConfig(
			builder,
			"player_left",
			"Options for the player left message",
			"Should a message be sent to the Discord management channel, if a player left?",
			"Message send to the Discord management channel, if a player left the server. (<player name> <message>)",
			"disconnected."
		);
		linking_created = new MessageConfig(
			builder,
			"linking_created",
			"Options for the linking created message",
			"Should a message be sent to the Discord management channel, if the a linking is created?",
			"Message send to the Discord management channel, if a linking is created. " +
				"(<player name> <message> <discord user name>)",
			"added to linking for discord user"
		);
		linking_removed = new MessageConfig(
			builder,
			"linking_removed",
			"Options for the linking removed message",
			"Should a message be sent to the Discord management channel, if the a linking is removed?",
			"Message send to the Discord management channel, if a linking is removed. " +
				"(<player name> <message> <discord user name>)",
			"removed from linking for discord user"
		);
		player_whitelist_added = new MessageConfig(
			builder,
			"player_whitelist_added",
			"Options for the player to whitelist added message",
			"Should a message be sent to the Discord management channel, if the a player is added to the whitelist, " +
				"because of a linking?",
			"Message send to the Discord management channel, if a player is added to the whitelist, " +
				"because of a linking. (<discord user name>: <player name> <message>)",
			"added to whitelist"
		);
		player_whitelist_removed = new MessageConfig(
			builder,
			"player_whitelist_removed",
			"Options for the player from whitelist removed message",
			"Should a message be sent to the Discord management channel, if the a player is removed from the " +
				"whitelist, because of a linking?",
			"Message send to the Discord management channel, if a player is added to the whitelist, " +
				"because of a linking. (<discord user name>: <player name> <message>)",
			"removed from the whitelist"
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
		player_whitelist_added.printConfig( logger );
		player_whitelist_removed.printConfig( logger );
	}
}
