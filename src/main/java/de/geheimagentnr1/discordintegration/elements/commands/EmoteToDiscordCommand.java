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
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings( "SameReturnValue" )
@RequiredArgsConstructor
public class EmoteToDiscordCommand implements CommandInterface {
	
	
	@NotNull
	private final ChatManager chatManager;
	
	@NotNull
	@Override
	public LiteralArgumentBuilder<CommandSourceStack> build() {
		
		LiteralArgumentBuilder<CommandSourceStack> meCommand = Commands.literal( "me" );
		meCommand.then( Commands.argument( "action", MessageArgument.message() )
			.executes( this::sendMeMessage ) );
		return meCommand;
	}
	
	private int sendMeMessage( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
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
				chatManager.sendEmoteChatMessage( source, playerChatMessage.decoratedContent() );
			}
		);
		return Command.SINGLE_SUCCESS;
	}
}
