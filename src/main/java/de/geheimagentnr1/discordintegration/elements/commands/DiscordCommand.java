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
import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandInterface;
import lombok.RequiredArgsConstructor;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@SuppressWarnings( "SameReturnValue" )
@Log4j2
@RequiredArgsConstructor
public class DiscordCommand implements CommandInterface {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	@Override
	public LiteralArgumentBuilder<CommandSourceStack> build() {
		
		LiteralArgumentBuilder<CommandSourceStack> discord = Commands.literal( "discord" );
		discord.then( Commands.literal( "commands" )
			.executes( this::showCommands ) );
		discord.then( Commands.literal( "gamerules" )
			.executes( this::showGamerules ) );
		discord.then( Commands.literal( "mods" )
			.executes( this::showMods ) );
		discord.then( Commands.literal( "linkings" )
			.then( Commands.literal( "link" )
				.then( Commands.argument( "player", GameProfileArgument.gameProfile() )
					.executes( this::linkDiscord ) ) )
			.then( Commands.literal( "unlink" )
				.then( Commands.argument( "player", GameProfileArgument.gameProfile() )
					.executes( this::unlinkDiscord ) ) ) );
		
		LiteralArgumentBuilder<CommandSourceStack> opDiscord = Commands.literal( "discord" )
			.requires( commandSourceStack -> commandSourceStack.hasPermission( 3 ) );
		opDiscord
			.then( Commands.literal( "linkings" )
				.then( Commands.literal( "link" )
					.then( Commands.argument( "player", GameProfileArgument.gameProfile() )
						.then( Commands.argument( "discordMemberId", LongArgumentType.longArg() )
							.executes( this::linkMinecraft ) ) ) )
				.then( Commands.literal( "unlink" )
					.then( Commands.argument( "player", GameProfileArgument.gameProfile() )
						.then( Commands.argument( "discordMemberId", LongArgumentType.longArg() )
							.executes( this::unlinkMinecraft ) ) ) ) );
		
		dispatcher.register( discord );
		dispatcher.register( opDiscord );
	}
	
	private int showCommands( @NotNull CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		List<? extends AbstractCommentedConfig> commands = new ArrayList<>( serverConfig.COMMAND_SETTINGS_CONFIG.getCommands() );
		commands.sort( Comparator.comparing( CommandConfig::getDiscordCommand ) );
		for( AbstractCommentedConfig commandConfig : commands ) {
			if( CommandConfig.isEnabled( commandConfig ) ) {
				source.sendSuccess(
					() -> Component.literal( MessageUtil.replaceParameters(
							CommandConfig.getDescription( commandConfig ),
							Map.of(
								"command",
								serverConfig.COMMAND_SETTINGS_CONFIG.getCommandPrefix() +
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
	
	private int showGamerules( @NotNull CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		GameRules.visitGameRuleTypes( new GameRules.GameRuleTypeVisitor() {
			
			@Override
			public <T extends GameRules.Value<T>> void visit(
				@NotNull GameRules.Key<T> key,
				@NotNull GameRules.Type<T> type ) {
				
				source.sendSuccess(
					() -> Component.translatable(
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
	
	private int showMods( @NotNull CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		ModList.get().forEachModFile( modFile -> {
			for( IModInfo modInfo : modFile.getModInfos() ) {
				source.sendSuccess(
					() -> Component.literal( String.format(
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
	
	private int linkDiscord( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( !( source instanceof DiscordCommandSourceStack discordSource ) ) {
			DiscordCommandHelper.sendInvalidCommandConfigurationErrorMessage( source );
			return -1;
		}
		Member member = discordSource.getMember();
		
		return link( source, member, context );
	}
	
	@SuppressWarnings( "DuplicatedCode" )
	private int linkMinecraft( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
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
	
	private int link( CommandSourceStack source, Member member, CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		GameProfile gameProfile = getGameProfileFromArgument( context );
		
		try {
			LinkingsManager.createLinking(
				member,
				gameProfile,
				successful -> {
					if( successful ) {
						source.sendSuccess(
							Component.literal( MessageUtil.replaceParameters(
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
							Component.literal( MessageUtil.replaceParameters(
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
					if( source instanceof DiscordCommandSourceStack discordCommandSourceStack ) {
						discordCommandSourceStack.getSource().sendMessage();
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
	
	private GameProfile getGameProfileFromArgument( CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		Collection<GameProfile> gameProfiles = GameProfileArgument.getGameProfiles( context, "player" );
		if( gameProfiles.size() != 1 ) {
			throw GameProfileArgument.ERROR_UNKNOWN_PLAYER.create();
		}
		return gameProfiles.stream()
			.findFirst()
			.orElseThrow( GameProfileArgument.ERROR_UNKNOWN_PLAYER::create );
	}
	
	private int unlinkDiscord( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( !( source instanceof DiscordCommandSourceStack discordSource ) ) {
			DiscordCommandHelper.sendInvalidCommandConfigurationErrorMessage( source );
			return -1;
		}
		Member member = discordSource.getMember();
		
		return unlink( source, member, context );
	}
	
	@SuppressWarnings( "DuplicatedCode" )
	private int unlinkMinecraft( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
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
	
	private int unlink( CommandSourceStack source, Member member, CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		GameProfile gameProfile = getGameProfileFromArgument( context );
		
		try {
			LinkingsManager.removeLinking(
				member,
				gameProfile,
				() -> {
					source.sendSuccess(
						Component.literal( MessageUtil.replaceParameters(
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
