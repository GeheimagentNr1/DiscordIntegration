package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandInterface;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings( "SameReturnValue" )
@RequiredArgsConstructor
public class SayToDiscordCommand implements CommandInterface {
	
	
	@NotNull
	private final ChatManager chatManager;
	
	@NotNull
	@Override
	public LiteralArgumentBuilder<CommandSourceStack> build() {
		
		LiteralArgumentBuilder<CommandSourceStack> sayCommand = Commands.literal( "say" )
			.requires( source -> source.hasPermission( 2 ) );
		sayCommand.then( Commands.argument( "message", MessageArgument.message() )
			.executes( this::sendSayMessage ) );
		return sayCommand;
	}
	
	private int sendSayMessage( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		MessageArgument.resolveChatMessage(
			context,
			"message",
			playerChatMessage -> {
				source.getServer().getPlayerList()
					.broadcastChatMessage( playerChatMessage, source, ChatType.bind( ChatType.SAY_COMMAND, source ) );
				chatManager.sendChatMessage( source, playerChatMessage.decoratedContent() );
			}
		);
		return Command.SINGLE_SUCCESS;
	}
}
