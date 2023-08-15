package de.geheimagentnr1.discordintegration.elements.discord.commands.models;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;


public class DiscordCommandSourceStack extends CommandSourceStack {
	
	
	private final DiscordCommandSource source;
	
	private final Member member;
	
	public DiscordCommandSourceStack(
		DiscordCommandSource _source,
		int permissionLevel,
		MinecraftServer server,
		Member _member ) {
		
		super(
			_source,
			Vec3.ZERO,
			Vec2.ZERO,
			Objects.requireNonNull( server.overworld() ),
			permissionLevel,
			ServerConfig.MOD_NAME,
			Component.literal( ServerConfig.MOD_NAME ),
			server,
			null
		);
		source = _source;
		member = _member;
	}
	
	public DiscordCommandSource getSource() {
		
		return source;
	}
	
	public Member getMember() {
		
		return member;
	}
}