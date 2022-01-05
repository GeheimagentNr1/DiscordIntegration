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
import de.geheimagentnr1.discordintegration.elements.commands.arguments.single_game_profile.SingleGameProfileArgument;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;


@SuppressWarnings( "SameReturnValue" )
@Log4j2
public class DiscordCommand {
	
	
	public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSourceStack> discordCommand = Commands.literal( "discord" );
		discordCommand.then( Commands.literal( "commands" )
			.executes( DiscordCommand::showCommands ) );
		discordCommand.then( Commands.literal( "gamerules" )
			.executes( DiscordCommand::showGamerules ) );
		discordCommand.then( Commands.literal( "mods" )
			.executes( DiscordCommand::showMods ) );
		discordCommand.then( Commands.literal( "linkings" )
			.then( Commands.literal( "link" )
				.then( Commands.argument( "player", SingleGameProfileArgument.gameProfile() )
					.executes( DiscordCommand::linkDiscord ) ) )
			.then( Commands.literal( "unlink" )
				.then( Commands.argument( "player", SingleGameProfileArgument.gameProfile() )
					.executes( DiscordCommand::unlinkDiscord ) ) ) );
		
		LiteralArgumentBuilder<CommandSourceStack> opDiscordCommand = Commands.literal( "discord" )
			.requires( commandSourceStack -> commandSourceStack.hasPermission( 3 ) );
		opDiscordCommand
			.then( Commands.literal( "linkings" )
				.then( Commands.literal( "link" )
					.then( Commands.argument( "player", SingleGameProfileArgument.gameProfile() )
						.then( Commands.argument( "discordMemberId", LongArgumentType.longArg() )
							.executes( DiscordCommand::linkMinecraft ) ) ) )
				.then( Commands.literal( "unlink" )
					.then( Commands.argument( "player", SingleGameProfileArgument.gameProfile() )
						.then( Commands.argument( "discordMemberId", LongArgumentType.longArg() )
							.executes( DiscordCommand::unlinkMinecraft ) ) ) ) );
		
		dispatcher.register( discordCommand );
		dispatcher.register( opDiscordCommand );
	}
	
	private static int showCommands( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		List<? extends AbstractCommentedConfig> commands = ServerConfig.COMMAND_SETTINGS_CONFIG.getCommands();
		commands.sort( Comparator.comparing( CommandConfig::getDiscordCommand ) );
		for( AbstractCommentedConfig commandConfig : commands ) {
			if( CommandConfig.isEnabled( commandConfig ) ) {
				source.sendSuccess(
					new TextComponent( String.format(
						"%s%s - %s%s",
						ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandPrefix(),
						CommandConfig.getDiscordCommand( commandConfig ),
						CommandConfig.getDescription( commandConfig ),
						CommandConfig.isManagementCommand( commandConfig ) ?
							" (Only usable by users with Management role)" :
							""
					) ),
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
					new TranslatableComponent(
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
					new TextComponent( String.format(
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
			source.sendFailure( new TextComponent( "Invalid Command Configuration" ) );
			return -1;
		}
		Member member = discordSource.getMember();
		
		return link( source, member, context );
	}
	
	@SuppressWarnings( "DuplicatedCode" )
	private static int linkMinecraft( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( DiscordCommandHelper.isNotDiscordSource( source ) ) {
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
		
		GameProfile gameProfile = SingleGameProfileArgument.getGameProfile( context, "player" );
		
		try {
			LinkingsManager.createLinking(
				member,
				gameProfile,
				successful -> {
					if( successful ) {
						source.sendSuccess(
							new TextComponent( String.format(
								"Created Linking between Discord account \"%s\" and Minecraft account \"%s\"",
								DiscordManager.getNameFromMember( member ),
								gameProfile.getName()
							) ),
							true
						);
					} else {
						source.sendFailure(
							new TextComponent( String.format(
								"Linking between Discord account \"%s\" and Minecraft account \"%s\" already exists",
								DiscordManager.getNameFromMember( member ),
								gameProfile.getName()
							) )
						);
					}
					if( source instanceof DiscordCommandSourceStack discordCommandSourceStack ) {
						discordCommandSourceStack.getSource().sendMessage();
					}
				},
				throwable -> DiscordCommandHelper.handleError( source, throwable )
			);
		} catch( IOException exception ) {
			DiscordCommandHelper.handleError( source, exception );
			return -1;
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static int unlinkDiscord( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( !( source instanceof DiscordCommandSourceStack discordSource ) ) {
			source.sendFailure( new TextComponent( "Invalid Command Configuration" ) );
			return -1;
		}
		Member member = discordSource.getMember();
		
		return unlink( source, member, context );
	}
	
	@SuppressWarnings( "DuplicatedCode" )
	private static int unlinkMinecraft( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( DiscordCommandHelper.isNotDiscordSource( source ) ) {
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
		
		GameProfile gameProfile = SingleGameProfileArgument.getGameProfile( context, "player" );
		
		try {
			LinkingsManager.removeLinking(
				member,
				gameProfile,
				throwable -> DiscordCommandHelper.handleError( source, throwable )
			);
			source.sendSuccess(
				new TextComponent( String.format(
					"Removed Linking between Discord account \"%s\" and Minecraft account \"%s\"",
					DiscordManager.getNameFromMember( member ),
					gameProfile.getName()
				) ),
				true
			);
		} catch( IOException exception ) {
			DiscordCommandHelper.handleError( source, exception );
			return -1;
		}
		return Command.SINGLE_SUCCESS;
	}
}
