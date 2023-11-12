package de.geheimagentnr1.discordintegration.elements.discord.commands.models;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


@Getter
public class DiscordCommandSourceStack extends CommandSourceStack {
	
	
	@NotNull
	private final DiscordCommandSource discordCommandSource;
	
	@NotNull
	private final Member member;
	
	public DiscordCommandSourceStack(
		@NotNull DiscordIntegration discordIntegration,
		@NotNull DiscordCommandSource _discordCommandSource,
		int permissionLevel,
		@NotNull MinecraftServer server,
		@NotNull Member _member ) {
		
		super(
			_discordCommandSource,
			Vec3.ZERO,
			Vec2.ZERO,
			Objects.requireNonNull( server.overworld() ),
			permissionLevel,
			discordIntegration.getModName(),
			Component.literal( discordIntegration.getModName() ),
			server,
			null
		);
		discordCommandSource = _discordCommandSource;
		member = _member;
	}
	
}