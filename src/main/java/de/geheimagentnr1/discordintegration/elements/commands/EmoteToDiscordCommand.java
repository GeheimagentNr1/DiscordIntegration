package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.players.PlayerList;


@SuppressWarnings( "SameReturnValue" )
public class EmoteToDiscordCommand {
	
	
	public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSourceStack> meCommand = Commands.literal( "me" );
		meCommand.then( Commands.argument( "action", MessageArgument.message() )
			.executes( EmoteToDiscordCommand::sendMeMessage ) );
		dispatcher.register( meCommand );
	}
	
	private static int sendMeMessage( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		MessageArgument.resolveChatMessage(
			context,
			"action",
			playerChatMessage -> {
				
				CommandSourceStack source = context.getSource();
				PlayerList playerlist = source.getServer().getPlayerList();
				playerlist.broadcastChatMessage(
					playerChatMessage,
					source,
					ChatType.bind( ChatType.EMOTE_COMMAND, source )
				);
				DiscordNet.sendEmoteChatMessage( source, playerChatMessage.decoratedContent() );
			}
		);
		return Command.SINGLE_SUCCESS;
	}
}
