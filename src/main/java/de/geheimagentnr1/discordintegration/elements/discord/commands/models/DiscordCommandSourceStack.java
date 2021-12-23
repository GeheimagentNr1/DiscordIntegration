package de.geheimagentnr1.discordintegration.elements.discord.commands.models;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;


public class DiscordCommandSourceStack extends CommandSourceStack {
	
	
	private final Member member;
	
	public DiscordCommandSourceStack(
		CommandSource source,
		int permissionLevel,
		MinecraftServer server,
		Member _member ) {
		
		super(
			source,
			Vec3.ZERO,
			Vec2.ZERO,
			Objects.requireNonNull( server.overworld() ),
			permissionLevel,
			ServerConfig.MOD_NAME,
			new TextComponent( ServerConfig.MOD_NAME ),
			server,
			null
		);
		member = _member;
	}
	
	public Member getMember() {
		
		return member;
	}
}