package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;


public class MeToDiscordCommand {
	
	
	public static void register( CommandDispatcher<CommandSource> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSource> meCommand = Commands.literal( "me" );
		meCommand.then( Commands.argument( "action", StringArgumentType.greedyString() )
			.executes( MeToDiscordCommand::sendMeMessage ) );
		dispatcher.register( meCommand );
	}
	
	private static int sendMeMessage( CommandContext<CommandSource> context ) {
		
		CommandSource source = context.getSource();
		String action = StringArgumentType.getString( context, "action" );
		source.getServer().getPlayerList().sendMessage( new TranslationTextComponent(
			"chat.type.emote",
			source.getDisplayName(),
			action
		) );
		DiscordNet.sendMeChatMessage( source, action );
		return Command.SINGLE_SUCCESS;
	}
}
