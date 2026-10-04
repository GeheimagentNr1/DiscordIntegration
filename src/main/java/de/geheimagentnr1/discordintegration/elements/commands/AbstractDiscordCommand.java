package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import de.geheimagentnr1.discordintegration.api.elements.commands.CommandInterface;
import de.geheimagentnr1.discordintegration.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.NameAndId;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;


@SuppressWarnings( "SameReturnValue" )
@Log4j2
@RequiredArgsConstructor
public abstract class AbstractDiscordCommand implements CommandInterface {
	
	
	@NotNull
	protected final ServerConfig serverConfig;
	
	@NotNull
	protected final DiscordManager discordManager;
	
	@NotNull
	private final LinkingsManager linkingsManager;
	
	int link(
		@NotNull CommandSourceStack source,
		@NotNull Member member,
		@NotNull CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		NameAndId nameAndId = getNameAndIdFromArgument( context );
		
		try {
			linkingsManager.createLinking(
				member,
				nameAndId,
				successful -> {
					if( successful ) {
						source.sendSuccess(
							() -> Component.literal( MessageUtil.replaceParameters(
								serverConfig.getCommandSettingsConfig()
									.getCommandMessagesConfig()
									.getLinkCreatedResultMessage(),
								Map.of(
									"username", discordManager.getMemberAsTag( member ),
									"nickname", member.getEffectiveName(),
									"player", nameAndId.name()
								)
							) ),
							true
						);
					} else {
						source.sendFailure(
							Component.literal( MessageUtil.replaceParameters(
								serverConfig.getCommandSettingsConfig()
									.getCommandMessagesConfig()
									.getLinkAlreadyExistsResultMessage(),
								Map.of(
									"username", discordManager.getMemberAsTag( member ),
									"nickname", member.getEffectiveName(),
									"player", nameAndId.name()
								)
							) )
						);
					}
					if( source instanceof DiscordCommandSourceStack discordCommandSourceStack ) {
						discordCommandSourceStack.getDiscordCommandSource().sendMessage();
					}
				},
				() -> DiscordCommandHelper.sendLinkCommandsUseIfWhitelistIsDisabledErrorMessage(
					serverConfig,
					source
				),
				throwable -> DiscordCommandHelper.handleError( source, throwable )
			);
		} catch( IOException exception ) {
			DiscordCommandHelper.handleError( source, exception );
			return -1;
		}
		return Command.SINGLE_SUCCESS;
	}
	
	@NotNull
	private NameAndId getNameAndIdFromArgument( @NotNull CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		Collection<NameAndId> nameAndIds = GameProfileArgument.getGameProfiles( context, "player" );
		if( nameAndIds.size() != 1 ) {
			throw GameProfileArgument.ERROR_UNKNOWN_PLAYER.create();
		}
		return nameAndIds.stream()
			.findFirst()
			.orElseThrow( GameProfileArgument.ERROR_UNKNOWN_PLAYER::create );
	}
	
	int unlink(
		@NotNull CommandSourceStack source,
		@NotNull Member member,
		@NotNull CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		NameAndId nameAndId = getNameAndIdFromArgument( context );
		
		try {
			linkingsManager.removeLinking(
				member,
				nameAndId,
				() -> source.sendSuccess(
					() -> Component.literal( MessageUtil.replaceParameters(
						serverConfig.getCommandSettingsConfig()
							.getCommandMessagesConfig()
							.getLinkRemovedResultMessage(),
						Map.of(
							"username", discordManager.getMemberAsTag( member ),
							"nickname", member.getEffectiveName(),
							"player", nameAndId.name()
						)
					) ),
					true
				),
				() -> DiscordCommandHelper.sendLinkCommandsUseIfWhitelistIsDisabledErrorMessage(
					serverConfig,
					source
				),
				throwable -> DiscordCommandHelper.handleError( source, throwable )
			);
		} catch( IOException exception ) {
			DiscordCommandHelper.handleError( source, exception );
			return -1;
		}
		return Command.SINGLE_SUCCESS;
	}
}
