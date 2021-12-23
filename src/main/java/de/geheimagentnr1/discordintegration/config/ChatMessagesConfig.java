package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;


public class ChatMessagesConfig {
	
	
	private final MessageConfig server_started;
	
	private final MessageConfig server_stopped;
	
	private final MessageConfig server_crashed;
	
	private final MessageConfig player_joined;
	
	private final MessageConfig player_left;
	
	private final MessageConfig player_died;
	
	private final MessageConfig tamed_mob_died;
	
	private final MessageConfig player_got_advancement;
	
	//package-private
	ChatMessagesConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Messages shown on Discord in the chat channel" )
			.push( "messages" );
		server_started = new MessageConfig(
			builder,
			"server_started",
			"Options for the server start message",
			"Should a message be sent to the Discord chat, if the server started?",
			"Message send to the Discord chat, if the Minecraft server started.",
			"Server started"
		);
		server_stopped = new MessageConfig(
			builder,
			"server_stopped",
			"Options for the server stop message",
			"Should a message be sent to the Discord chat, if the server stopped?",
			"Message send to the Discord chat, if the Minecraft server stopped.",
			"Server stopped"
		);
		server_crashed = new MessageConfig(
			builder,
			"server_crashed",
			"Options for the server crash message",
			"Should a message be sent to the Discord chat, if the server crashed?",
			"Message send to the Discord chat, if the Minecraft server crashed.",
			"Server crashed"
		);
		player_joined = new MessageConfig(
			builder,
			"player_joined",
			"Options for the player joined message",
			"Should a message be sent to the Discord chat, if a player joined?",
			"Message send to the Discord chat, if a player joined. (<player name> <message>)",
			"joined the game."
		);
		player_left = new MessageConfig(
			builder,
			"player_left",
			"Options for the player left message",
			"Should a message be sent to the Discord chat, if a player left?",
			"Message send to the Discord chat, if a player left the server. (<player name> <message>)",
			"disconnected."
		);
		player_died = new MessageConfig(
			builder,
			"player_died",
			"Options for the player died message",
			"Should a message be sent to the Discord chat, if a player died?",
			"Message send to the Discord chat, if a player died. (<player name> <message>) " +
				"If left empty, the default Minecraft message is send.",
			""
		);
		tamed_mob_died = new MessageConfig(
			builder,
			"tamed_mob_died",
			"Options for the tamed mob died message",
			"Should a message be sent to the Discord chat, if a tamed mob left?",
			"Message send to the Discord chat, if a tamed mob died. (<player name> <message>)" +
				" If left empty, the default Minecraft message is send.",
			""
		);
		player_got_advancement = new MessageConfig(
			builder,
			"player_got_advancement",
			"Options for the player got advancement message",
			"Should a message be sent to the Discord chat, if a player got an advancement?",
			"Message send to the Discord chat, if a player got an advancement. (<player name> <message>) " +
				"**<advancement title>**<new line>*<advancement description>*",
			"has made the advancement"
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
	
	public MessageConfig getPlayerDied() {
		
		return player_died;
	}
	
	public MessageConfig getTamedMobDied() {
		
		return tamed_mob_died;
	}
	
	public MessageConfig getPlayerGotAdvancement() {
		
		return player_got_advancement;
	}
	
	//package-private
	@SuppressWarnings( "DuplicatedCode" )
	void printConfig( Logger logger ) {
		
		server_started.printConfig( logger );
		server_stopped.printConfig( logger );
		server_crashed.printConfig( logger );
		player_joined.printConfig( logger );
		player_left.printConfig( logger );
		player_died.printConfig( logger );
		tamed_mob_died.printConfig( logger );
		player_got_advancement.printConfig( logger );
	}
}