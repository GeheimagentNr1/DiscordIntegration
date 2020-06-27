package de.geheimagentnr1.discordintegration.commands.discord;

import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;


public class GamerulesCommand extends CommandHandler {
	
	
	public GamerulesCommand() {
		
		super( "gamerules" );
	}
	
	@Override
	protected void run( CommandSource source, MinecraftServer server ) {
		
		ArrayList<String> messages = new ArrayList<>();
		GameRules.visitAll( new GameRules.IRuleEntryVisitor() {
			
			@Override
			public <T extends GameRules.RuleValue<T>> void visit( @NotNull GameRules.RuleKey<T> key,
				@Nonnull GameRules.RuleType<T> type ) {
				
				int messagePos = messages.size() - 1;
				String gameruleText = new TranslationTextComponent( "commands.gamerule.query", key.getName(),
					server.getGameRules().get( key ).toString() ).getString();
				if( !messages.isEmpty() && messages.get( messagePos ).length() + gameruleText.length() + 1 < 2000 ) {
					//noinspection HardcodedLineSeparator
					messages.set( messagePos, messages.get( messagePos ) + "\n" + gameruleText );
				} else {
					messages.add( gameruleText );
				}
			}
		} );
		if( messages.size() == 1 ) {
			DiscordNet.sendMessage( "```" + messages.get( 0 ) + "```" );
		} else {
			for( String message : messages ) {
				DiscordNet.sendMessage( message );
			}
		}
	}
	
	@Override
	public String getDescription() {
		
		return "shows the gamerules and their values.";
	}
}
