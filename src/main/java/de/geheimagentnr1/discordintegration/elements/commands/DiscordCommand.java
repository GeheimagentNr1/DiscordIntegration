package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.config.command_config.CommandConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.commands.models.DiscordCommandSourceStack;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import de.geheimagentnr1.minecraft_forge_api.util.MessageUtil;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@SuppressWarnings( "SameReturnValue" )
@Log4j2
class DiscordCommand extends AbstractDiscordCommand {
	
	
	@SuppressWarnings( "ParameterHidesMemberVariable" )
	DiscordCommand(
		@NotNull ServerConfig serverConfig,
		@NotNull DiscordManager discordManager,
		@NotNull LinkingsManager linkingsManager ) {
		
		super( serverConfig, discordManager, linkingsManager );
	}
	
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
		
		return discord;
	}
	
	private int showCommands( @NotNull CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		List<CommandConfig> commands = new ArrayList<>(
			serverConfig.getCommandSettingsConfig().getCommands()
		);
		commands.sort( Comparator.comparing( CommandConfig::getDiscordCommand ) );
		for( CommandConfig commandConfig : commands ) {
			if( commandConfig.isEnabled() ) {
				source.sendSuccess(
					() -> Component.literal( MessageUtil.replaceParameters(
							commandConfig.getDescription(),
							Map.of(
								"command",
								serverConfig.getCommandSettingsConfig().getCommandPrefix() +
									commandConfig.getDiscordCommand(),
								"command_description_separator",
								" - "
							)
						)
					).append(
						commandConfig.isManagementCommand() ?
							" (" + serverConfig.getCommandSettingsConfig()
								.getCommandMessagesConfig()
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
	
	private int linkDiscord( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( !( source instanceof DiscordCommandSourceStack discordSource ) ) {
			DiscordCommandHelper.sendInvalidCommandConfigurationErrorMessage( source );
			return -1;
		}
		Member member = discordSource.getMember();
		
		return link( source, member, context );
	}
	
	private int unlinkDiscord( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( !( source instanceof DiscordCommandSourceStack discordSource ) ) {
			DiscordCommandHelper.sendInvalidCommandConfigurationErrorMessage( source );
			return -1;
		}
		Member member = discordSource.getMember();
		
		return unlink( source, member, context );
	}
}
