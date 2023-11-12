package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.LinkingsManager;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings( "SameReturnValue" )
@Log4j2
class DiscordOpCommand extends AbstractDiscordCommand {
	
	
	@SuppressWarnings( "ParameterHidesMemberVariable" )
	DiscordOpCommand(
		@NotNull ServerConfig serverConfig,
		@NotNull DiscordManager discordManager,
		@NotNull LinkingsManager linkingsManager ) {
		
		super( serverConfig, discordManager, linkingsManager );
	}
	
	@NotNull
	@Override
	public LiteralArgumentBuilder<CommandSourceStack> build() {
		
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
		
		return opDiscord;
	}
	
	@SuppressWarnings( "DuplicatedCode" )
	private int linkMinecraft( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( DiscordCommandHelper.isDiscordSource( source ) ) {
			return -1;
		}
		Member member = discordManager.getMember( LongArgumentType.getLong( context, "discordMemberId" ) );
		if( member == null ) {
			DiscordCommandHelper.sendInvalidMember( serverConfig, source );
			return -1;
		}
		
		return link( source, member, context );
	}
	
	@SuppressWarnings( "DuplicatedCode" )
	private int unlinkMinecraft( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		if( DiscordCommandHelper.isDiscordSource( source ) ) {
			return -1;
		}
		Member member = discordManager.getMember( LongArgumentType.getLong( context, "discordMemberId" ) );
		if( member == null ) {
			DiscordCommandHelper.sendInvalidMember( serverConfig, source );
			return -1;
		}
		
		return unlink( source, member, context );
	}
}
