package de.geheimagentnr1.discordintegration.elements.commands;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.config.command_config.CommandConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import de.geheimagentnr1.discordintegration.util.MessageUtil;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@SuppressWarnings( "SameReturnValue" )
@Log4j2
public class DiscordCommand {
	
	
	public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSourceStack> discord = Commands.literal( "discord" );
		discord.then( Commands.literal( "commands" )
			.executes( DiscordCommand::showCommands ) );
		discord.then( Commands.literal( "gamerules" )
			.executes( DiscordCommand::showGamerules ) );
		discord.then( Commands.literal( "mods" )
			.executes( DiscordCommand::showMods ) );
		discord.then( Commands.literal( "linkings" )
			.then( Commands.literal( "link" )
				.then( Commands.argument( "player", GameProfileArgument.gameProfile() )
					.executes( DiscordCommand::linkDiscord ) ) )
			.then( Commands.literal( "unlink" )
				.then( Commands.argument( "player", GameProfileArgument.gameProfile() )
					.executes( DiscordCommand::unlinkDiscord ) ) ) );
		
		LiteralArgumentBuilder<CommandSourceStack> opDiscord = Commands.literal( "discord" )
			.requires( commandSourceStack -> commandSourceStack.hasPermission( 3 ) );
		opDiscord
			.then( Commands.literal( "linkings" )
				.then( Commands.literal( "link" )
					.then( Commands.argument( "player", GameProfileArgument.gameProfile() )
						.then( Commands.argument( "discordMemberId", LongArgumentType.longArg() )
							.executes( DiscordCommand::linkMinecraft ) ) ) )
				.then( Commands.literal( "unlink" )
					.then( Commands.argument( "player", GameProfileArgument.gameProfile() )
						.then( Commands.argument( "discordMemberId", LongArgumentType.longArg() )
							.executes( DiscordCommand::unlinkMinecraft ) ) ) ) );
		
		dispatcher.register( discord );
		dispatcher.register( opDiscord );
	}
	
	private static int showCommands( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		List<? extends AbstractCommentedConfig> commands = ServerConfig.COMMAND_SETTINGS_CONFIG.getCommands();
		commands.sort( Comparator.comparing( CommandConfig::getDiscordCommand ) );
		for( AbstractCommentedConfig commandConfig : commands ) {
			if( CommandConfig.isEnabled( commandConfig ) ) {
				source.sendSuccess(
					Component.literal(
						MessageUtil.replaceParameters(
							CommandConfig.getDescription( commandConfig ),
							Map.of(
								"command",
								ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandPrefix() +
									CommandConfig.getDiscordCommand( commandConfig ),
								"command_description_separator",
								" - "
							)
						)
					).append(
						CommandConfig.isManagementCommand( commandConfig ) ?
							" (" + ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandMessagesConfig()
								.getOnlyManagementCommandHintMessage() + ")" :
							""
					),
					false
				);
			}
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static int showGamerules( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		GameRules.visitGameRuleTypes( new GameRules.GameRuleTypeVisitor() {
			
			@Override
			public <T extends GameRules.Value<T>> void visit(
				@Nonnull GameRules.Key<T> key,
				@Nonnull GameRules.Type<T> type ) {
				
				source.sendSuccess(
					Component.translatable(
						"commands.gamerule.query",
						key.getId(),
						source.getServer().getGameRules().getRule( key )
					),
					false
				);
			}
		} );
		return Command.SINGLE_SUCCESS;
	}
	
	private static int showMods( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		ModList.get().forEachModFile( modFile -> {
			for( IModInfo modInfo : modFile.getModInfos() ) {
				source.sendSuccess(
					Component.literal( String.format(
						"%s (%s) - %s",
						modInfo.getModId(),
						modInfo.getVersion(),
						modFile.getFileName()
					) ),
					false
				);
			}
		} );
		return Command.SINGLE_SUCCESS;
	}
	
	private static int linkDiscord( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( !( source instanceof DiscordCommandSourceStack discordSource ) ) {
			DiscordCommandHelper.sendInvalidCommandConfigurationErrorMessage( source );
			return -1;
		}
		Member member = discordSource.getMember();
		
		return link( source, member, context );
	}
	
	@SuppressWarnings( "DuplicatedCode" )
	private static int linkMinecraft( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( DiscordCommandHelper.isDiscordSource( source ) ) {
			return -1;
		}
		Member member = DiscordManager.getMember( LongArgumentType.getLong( context, "discordMemberId" ) );
		if( member == null ) {
			DiscordCommandHelper.sendInvalidMember( source );
			return -1;
		}
		
		return link( source, member, context );
	}
	
	private static int link( CommandSourceStack source, Member member, CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		GameProfile gameProfile = getGameProfileFromArgument( context );
		
		try {
			LinkingsManager.createLinking(
				member,
				gameProfile,
				successful -> {
					if( successful ) {
						source.sendSuccess(
							new TextComponent( MessageUtil.replaceParameters(
								ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandMessagesConfig()
									.getLinkCreatedResultMessage(),
								Map.of(
									"username", DiscordManager.getMemberAsTag( member ),
									"nickname", member.getEffectiveName(),
									"player", gameProfile.getName()
								)
							) ),
							true
						);
					} else {
						source.sendFailure(
							new TextComponent( MessageUtil.replaceParameters(
								ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandMessagesConfig()
									.getLinkAlreadyExistsResultMessage(),
								Map.of(
									"username", DiscordManager.getMemberAsTag( member ),
									"nickname", member.getEffectiveName(),
									"player", gameProfile.getName()
								)
							) )
						);
					}
				},
				() -> DiscordCommandHelper.sendLinkCommandsUseIfWhitelistIsDisabledErrorMessage( source ),
				throwable -> DiscordCommandHelper.handleError( source, throwable )
			);
		} catch( IOException exception ) {
			DiscordCommandHelper.handleError( source, exception );
			return -1;
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static GameProfile getGameProfileFromArgument( CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		Collection<GameProfile> gameProfiles = GameProfileArgument.getGameProfiles( context, "player" );
		if( gameProfiles.size() != 1 ) {
			throw GameProfileArgument.ERROR_UNKNOWN_PLAYER.create();
		}
		return gameProfiles.stream()
			.findFirst()
			.orElseThrow( GameProfileArgument.ERROR_UNKNOWN_PLAYER::create );
	}
	
	private static int unlinkDiscord( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( !( source instanceof DiscordCommandSourceStack discordSource ) ) {
			DiscordCommandHelper.sendInvalidCommandConfigurationErrorMessage( source );
			return -1;
		}
		Member member = discordSource.getMember();
		
		return unlink( source, member, context );
	}
	
	@SuppressWarnings( "DuplicatedCode" )
	private static int unlinkMinecraft( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( DiscordCommandHelper.isDiscordSource( source ) ) {
			return -1;
		}
		Member member = DiscordManager.getMember( LongArgumentType.getLong( context, "discordMemberId" ) );
		if( member == null ) {
			DiscordCommandHelper.sendInvalidMember( source );
			return -1;
		}
		
		return unlink( source, member, context );
	}
	
	private static int unlink( CommandSourceStack source, Member member, CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		GameProfile gameProfile = getGameProfileFromArgument( context );
		
		try {
			LinkingsManager.removeLinking(
				member,
				gameProfile,
				() -> {
					source.sendSuccess(
						new TextComponent( MessageUtil.replaceParameters(
							ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandMessagesConfig()
								.getLinkRemovedResultMessage(),
							Map.of(
								"username", DiscordManager.getMemberAsTag( member ),
								"nickname", member.getEffectiveName(),
								"player", gameProfile.getName()
							)
						) ),
						true
					);
				},
				() -> DiscordCommandHelper.sendLinkCommandsUseIfWhitelistIsDisabledErrorMessage( source ),
				throwable -> DiscordCommandHelper.handleError( source, throwable )
			);
		} catch( IOException exception ) {
			DiscordCommandHelper.handleError( source, exception );
			return -1;
		}
		return Command.SINGLE_SUCCESS;
	}
}
