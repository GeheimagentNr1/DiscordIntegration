package de.geheimagentnr1.discordintegration.elements.commands;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.geheimagentnr1.discordintegration.config.CommandConfig;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;


@SuppressWarnings( "SameReturnValue" )
public class DiscordCommand {
	
	
	public static void register( CommandDispatcher<CommandSource> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSource> discordCommand = Commands.literal( "discord" );
		discordCommand.then( Commands.literal( "commands" )
			.executes( DiscordCommand::showCommands ) );
		discordCommand.then( Commands.literal( "gamerules" )
			.executes( DiscordCommand::showGamerules ) );
		discordCommand.then( Commands.literal( "mods" )
			.executes( DiscordCommand::showMods ) );
		dispatcher.register( discordCommand );
	}
	
	private static int showCommands( CommandContext<CommandSource> context ) {
		
		CommandSource source = context.getSource();
		List<? extends AbstractCommentedConfig> commands = ServerConfig.getCommands();
		commands.sort( Comparator.comparing( CommandConfig::getDiscordCommand ) );
		for( AbstractCommentedConfig abstractCommentedConfig : commands ) {
			if( CommandConfig.getEnabled( abstractCommentedConfig ) ) {
				source.sendFeedback(
					new StringTextComponent( String.format(
						"%s%s - %s",
						ServerConfig.getCommandPrefix(),
						CommandConfig.getDiscordCommand( abstractCommentedConfig ),
						CommandConfig.getDescription( abstractCommentedConfig )
					) ),
					false
				);
			}
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static int showGamerules( CommandContext<CommandSource> context ) {
		
		CommandSource source = context.getSource();
		GameRules.func_223590_a( new GameRules.IRuleEntryVisitor() {
			
			@Override
			public <T extends GameRules.RuleValue<T>> void func_223481_a(
				@Nonnull GameRules.RuleKey<T> key,
				@Nonnull GameRules.RuleType<T> type ) {
				
				source.sendFeedback(
					new TranslationTextComponent(
						"commands.gamerule.query",
						key.func_223576_a(),
						source.getServer().getGameRules().get( key )
					),
					false
				);
			}
		} );
		return Command.SINGLE_SUCCESS;
	}
	
	private static int showMods( CommandContext<CommandSource> context ) {
		
		CommandSource source = context.getSource();
		ModList.get().forEachModFile( modFile -> {
			for( IModInfo modInfo : modFile.getModInfos() ) {
				source.sendFeedback(
					new StringTextComponent( String.format(
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
	
}
