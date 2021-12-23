package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;


@SuppressWarnings( "SameReturnValue" )
public class MeToDiscordCommand {
	
	
	public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSourceStack> meCommand = Commands.literal( "me" );
		meCommand.then( Commands.argument( "action", StringArgumentType.greedyString() )
			.executes( MeToDiscordCommand::sendMeMessage ) );
		dispatcher.register( meCommand );
	}
	
	private static int sendMeMessage( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		String action = StringArgumentType.getString( context, "action" );
		TranslatableComponent translationTextComponent = new TranslatableComponent(
			"chat.type.emote",
			source.getDisplayName(),
			action
		);
		if( source.getEntity() != null ) {
			context.getSource().getServer().getPlayerList().broadcastMessage(
				translationTextComponent,
				ChatType.CHAT,
				source.getEntity().getUUID()
			);
		} else {
			context.getSource().getServer().getPlayerList().broadcastMessage(
				translationTextComponent,
				ChatType.SYSTEM,
				Util.NIL_UUID
			);
		}
		ChatManager.sendMeChatMessage( source, action );
		return Command.SINGLE_SUCCESS;
	}
}
