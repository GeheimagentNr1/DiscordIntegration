package de.geheimagentnr1.discordintegration.elements.commands;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.geheimagentnr1.discordintegration.config.CommandConfig;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandInterface;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@SuppressWarnings( "SameReturnValue" )
@RequiredArgsConstructor
public class DiscordCommand implements CommandInterface {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	@Override
	public LiteralArgumentBuilder<CommandSourceStack> build() {
		
		LiteralArgumentBuilder<CommandSourceStack> discordCommand = Commands.literal( "discord" );
		discordCommand.then( Commands.literal( "commands" )
			.executes( this::showCommands ) );
		discordCommand.then( Commands.literal( "gamerules" )
			.executes( this::showGamerules ) );
		discordCommand.then( Commands.literal( "mods" )
			.executes( this::showMods ) );
		return discordCommand;
	}
	
	private int showCommands( @NotNull CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		List<? extends AbstractCommentedConfig> commands = new ArrayList<>(serverConfig.getCommands());
		commands.sort( Comparator.comparing( CommandConfig::getDiscordCommand ) );
		for( AbstractCommentedConfig abstractCommentedConfig : commands ) {
			if( CommandConfig.getEnabled( abstractCommentedConfig ) ) {
				source.sendSuccess(
					() -> Component.literal( String.format(
						"%s%s - %s",
						serverConfig.getCommandPrefix(),
						CommandConfig.getDiscordCommand( abstractCommentedConfig ),
						CommandConfig.getDescription( abstractCommentedConfig )
					) ),
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
}
