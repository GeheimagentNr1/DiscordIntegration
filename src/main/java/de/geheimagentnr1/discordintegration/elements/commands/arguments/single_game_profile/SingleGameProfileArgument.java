package de.geheimagentnr1.discordintegration.elements.commands.arguments.single_game_profile;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;


public class SingleGameProfileArgument implements ArgumentType<SingleGameProfileResult> {
	
	
	public static final String registry_name = "single_game_profile";
	
	private static final Collection<String> EXAMPLES = Arrays.asList(
		"Player",
		"0123",
		"dd12be42-52a9-4a91-a8a1-11c01849e498"
	);
	
	public static GameProfile getGameProfile( CommandContext<CommandSourceStack> context, String name )
		throws CommandSyntaxException {
		
		return context.getArgument( name, SingleGameProfileResult.class ).getResult( context.getSource() );
	}
	
	public static SingleGameProfileArgument gameProfile() {
		
		return new SingleGameProfileArgument();
	}
	
	public SingleGameProfileResult parse( StringReader reader ) {
		
		int start = reader.getCursor();
		
		while( reader.canRead() && reader.peek() != ' ' ) {
			reader.skip();
		}
		return new SingleGameProfileResult( reader.getString().substring( start, reader.getCursor() ) );
	}
	
	public <S> CompletableFuture<Suggestions> listSuggestions(
		CommandContext<S> context,
		SuggestionsBuilder builder ) {
		
		if( context.getSource() instanceof SharedSuggestionProvider ) {
			StringReader reader = new StringReader( builder.getInput() );
			reader.setCursor( builder.getStart() );
			EntitySelectorParser entityselectorparser = new EntitySelectorParser( reader, false );
			
			try {
				entityselectorparser.parse();
			} catch( CommandSyntaxException ignored ) {
			}
			
			return entityselectorparser.fillSuggestions(
				builder,
				( suggestionsBuilder ) -> SharedSuggestionProvider.suggest(
					( (SharedSuggestionProvider)context.getSource() ).getOnlinePlayerNames(),
					suggestionsBuilder
				)
			);
		} else {
			return Suggestions.empty();
		}
	}
	
	public Collection<String> getExamples() {
		
		return EXAMPLES;
	}
}