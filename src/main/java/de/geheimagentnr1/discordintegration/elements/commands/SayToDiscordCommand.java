package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;


@SuppressWarnings( "SameReturnValue" )
public class SayToDiscordCommand {
	
	
	public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSourceStack> sayCommand = Commands.literal( "say" )
			.requires( source -> source.hasPermission( 2 ) );
		sayCommand.then( Commands.argument( "message", MessageArgument.message() )
			.executes( SayToDiscordCommand::sendSayMessage ) );
		dispatcher.register( sayCommand );
	}
	
	private static int sendSayMessage( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		MessageArgument.ChatMessage message = MessageArgument.getChatMessage( context, "message" );
		CommandSourceStack source = context.getSource();
		
		message.resolve(
			source,
			( playerChatMessage ) -> {
				source.getServer().getPlayerList()
					.broadcastChatMessage( playerChatMessage, source, ChatType.bind( ChatType.SAY_COMMAND, source ) );
				ChatManager.sendChatMessage( source, playerChatMessage.serverContent() );
			}
		);
		return Command.SINGLE_SUCCESS;
	}
}
