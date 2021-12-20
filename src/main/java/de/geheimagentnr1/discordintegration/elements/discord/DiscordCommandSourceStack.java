package de.geheimagentnr1.discordintegration.elements.discord;

import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;


public class DiscordCommandSourceStack extends CommandSourceStack {
	
	
	private final Member member;
	
	//package-private
	DiscordCommandSourceStack(
		CommandSource source,
		Vec3 worldPosition,
		Vec2 rotation,
		ServerLevel level,
		int permissionLevel,
		String textName,
		Component displayName,
		MinecraftServer server,
		@Nullable Entity entity,
		Member _member ) {
		
		super( source, worldPosition, rotation, level, permissionLevel, textName, displayName, server, entity );
		member = _member;
	}
	
	public Member getMember() {
		
		return member;
	}
}