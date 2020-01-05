package de.geheimagentnr1.discordintegration.commands.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;


public class MeCommandToDiscord {
	
	
	public static void register( CommandDispatcher<CommandSource> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSource> meCommand = Commands.literal( "me" );
		meCommand.then( Commands.argument( "action", StringArgumentType.greedyString() ).executes(
			context -> {
				CommandSource source = context.getSource();
				String action = StringArgumentType.getString( context, "action" );
				source.getServer().getPlayerList().sendMessage( new TranslationTextComponent( "chat.type.emote",
					source.getDisplayName(), action ) );
				DiscordNet.sendMeChatMessage( source, action );
				return 1;
			} ) );
		dispatcher.register( meCommand );
	}
}
