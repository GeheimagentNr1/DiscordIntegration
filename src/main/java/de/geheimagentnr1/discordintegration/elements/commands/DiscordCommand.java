package de.geheimagentnr1.discordintegration.elements.commands;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.geheimagentnr1.discordintegration.config.CommandConfig;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;


@SuppressWarnings( "SameReturnValue" )
public class DiscordCommand {
	
	
	public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSourceStack> discordCommand = Commands.literal( "discord" );
		discordCommand.then( Commands.literal( "commands" )
			.executes( DiscordCommand::showCommands ) );
		discordCommand.then( Commands.literal( "gamerules" )
			.executes( DiscordCommand::showGamerules ) );
		discordCommand.then( Commands.literal( "mods" )
			.executes( DiscordCommand::showMods ) );
		dispatcher.register( discordCommand );
	}
	
	private static int showCommands( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		List<? extends AbstractCommentedConfig> commands = ServerConfig.getCommands();
		commands.sort( Comparator.comparing( CommandConfig::getDiscordCommand ) );
		for( AbstractCommentedConfig abstractCommentedConfig : commands ) {
			if( CommandConfig.getEnabled( abstractCommentedConfig ) ) {
				source.sendSuccess(
					new TextComponent( String.format(
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
	
	private static int showGamerules( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		GameRules.visitGameRuleTypes( new GameRules.GameRuleTypeVisitor() {
			
			@Override
			public <T extends GameRules.Value<T>> void visit(
				@Nonnull GameRules.Key<T> key, @Nonnull GameRules.Type<T> type ) {
				
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
	
}
