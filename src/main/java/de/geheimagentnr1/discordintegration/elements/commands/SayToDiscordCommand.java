package de.geheimagentnr1.discordintegration.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;


public class SayToDiscordCommand {
	
	
	public static void register( CommandDispatcher<CommandSource> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSource> sayCommand = Commands.literal( "say" )
			.requires( source -> source.hasPermissionLevel( 2 ) );
		sayCommand.then( Commands.argument( "message", MessageArgument.message() )
			.executes( context -> {
				CommandSource source = context.getSource();
				ITextComponent message = MessageArgument.getMessage( context, "message" );
				Entity entity = context.getSource().getEntity();
				TranslationTextComponent translationTextComponent =
					new TranslationTextComponent(
					"chat.type.announcement", source.getDisplayName(),
					message
				);
				if( entity != null ) {
					context.getSource().getServer().getPlayerList().func_232641_a_( translationTextComponent,
						ChatType.CHAT, entity.getUniqueID() );
				} else {
					context.getSource().getServer().getPlayerList().func_232641_a_( translationTextComponent,
						ChatType.SYSTEM, Util.field_240973_b_ );
				}
				DiscordNet.sendChatMessage( source, message );
				return Command.SINGLE_SUCCESS;
			} ) );
		dispatcher.register( sayCommand );
	}
}
