package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;


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
		
		CommandSourceStack source = context.getSource();
		Component message = MessageArgument.getMessage( context, "message" );
		Entity entity = context.getSource().getEntity();
		TranslatableComponent translationTextComponent = new TranslatableComponent(
			"chat.type.announcement",
			source.getDisplayName(),
			message
		);
		if( entity != null ) {
			context.getSource().getServer().getPlayerList().broadcastMessage(
				translationTextComponent,
				ChatType.CHAT,
				entity.getUUID()
			);
		} else {
			context.getSource().getServer().getPlayerList().broadcastMessage(
				translationTextComponent,
				ChatType.SYSTEM,
				Util.NIL_UUID
			);
		}
		DiscordNet.sendChatMessage( source, message );
		return Command.SINGLE_SUCCESS;
	}
}
