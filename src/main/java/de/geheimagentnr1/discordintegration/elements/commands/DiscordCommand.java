package de.geheimagentnr1.discordintegration.elements.commands;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.config.command_config.CommandConfig;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.commands.arguments.single_game_profile.SingleGameProfileArgument;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordCommandSourceStack;
import de.geheimagentnr1.discordintegration.elements.linking.LinkingManager;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;


@SuppressWarnings( "SameReturnValue" )
public class DiscordCommand {
	
	
	private static final Logger LOGGER = LogManager.getLogger( DiscordCommand.class );
	
	
	public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSourceStack> discordCommand = Commands.literal( "discord" );
		discordCommand.then( Commands.literal( "commands" )
			.executes( DiscordCommand::showCommands ) );
		discordCommand.then( Commands.literal( "gamerules" )
			.executes( DiscordCommand::showGamerules ) );
		discordCommand.then( Commands.literal( "mods" )
			.executes( DiscordCommand::showMods ) );
		discordCommand.then( Commands.literal( "link" )
			.then( Commands.argument( "player", SingleGameProfileArgument.gameProfile() )
				.then( Commands.argument( "discordMemberId", LongArgumentType.longArg() )
					.executes( DiscordCommand::linkMinecraft ) )
				.executes( DiscordCommand::linkDiscord ) ) );
		discordCommand.then( Commands.literal( "unlink" )
			.then( Commands.argument( "player", SingleGameProfileArgument.gameProfile() )
				.then( Commands.argument( "discordMemberId", LongArgumentType.longArg() )
					.executes( DiscordCommand::unlinkMinecraft ) )
				.executes( DiscordCommand::unlinkDiscord ) ) );
		dispatcher.register( discordCommand );
	}
	
	private static int showCommands( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		List<? extends AbstractCommentedConfig> commands = ServerConfig.COMMAND_SETTINGS_CONFIG.getCommands();
		commands.sort( Comparator.comparing( CommandConfig::getDiscordCommand ) );
		for( AbstractCommentedConfig abstractCommentedConfig : commands ) {
			if( CommandConfig.isEnabled( abstractCommentedConfig ) ) {
				source.sendSuccess(
					new TextComponent( String.format(
						"%s%s - %s",
						ServerConfig.COMMAND_SETTINGS_CONFIG.getCommandPrefix(),
						CommandConfig.getDiscordCommand( abstractCommentedConfig ),
						CommandConfig.getDescription( abstractCommentedConfig )
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
	
	private static int linkMinecraft( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		Member member = DiscordNet.getGuild().getMemberById( LongArgumentType.getLong( context, "discordMemberId" ) );
		if( member == null ) {
			source.sendFailure( new TextComponent( "Discord Member does not exists or Discord context unloadable" ) );
			return -1;
		}
		
		return link( source, member, context );
	}
	
	private static int link( CommandSourceStack source, Member member, CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		GameProfile gameProfile = SingleGameProfileArgument.getGameProfile( context, "player" );
		
		try {
			LinkingManager.createLinking( member, gameProfile );
			source.sendSuccess(
				new TextComponent( String.format(
					"Created Linking between Discord account \"%s\" and Minecraft account \"%s\"",
					member.getEffectiveName(),
					gameProfile.getName()
				) ),
				true
			);
		} catch( IOException exception ) {
			LOGGER.error( "Linking failed", exception );
			source.sendFailure( new TextComponent( exception.getMessage() ) );
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
	
	private static int unlinkMinecraft( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		Member member = DiscordNet.getGuild().getMemberById( LongArgumentType.getLong( context, "discordMemberId" ) );
		if( member == null ) {
			source.sendFailure( new TextComponent( "Discord Member does not exists or Discord context unloadable" ) );
			return -1;
		}
		
		return unlink( source, member, context );
	}
	
	private static int unlink( CommandSourceStack source, Member member, CommandContext<CommandSourceStack> context )
		throws CommandSyntaxException {
		
		GameProfile gameProfile = SingleGameProfileArgument.getGameProfile( context, "player" );
		
		try {
			LinkingManager.removeLinking( member, gameProfile );
			source.sendSuccess(
				new TextComponent( String.format(
					"Removed Linking between Discord account \"%s\" and Minecraft account \"%s\"",
					member.getEffectiveName(),
					gameProfile.getName()
				) ),
				true
			);
		} catch( IOException exception ) {
			LOGGER.error( "Unlinking failed", exception );
			source.sendFailure( new TextComponent( exception.getMessage() ) );
			return -1;
		}
		return Command.SINGLE_SUCCESS;
	}
}
