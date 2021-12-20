package de.geheimagentnr1.discordintegration.elements.commands.arguments.single_game_profile;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;


//package-private
class SingleGameProfileResult {
	
	
	private static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER =
		new SimpleCommandExceptionType( new TranslatableComponent( "argument.player.unknown" ) );
	
	private final String player;
	
	//package-private
	SingleGameProfileResult( String _player ) {
		
		player = _player;
	}
	
	//package-private
	GameProfile getResult( CommandSourceStack source ) throws CommandSyntaxException {
		
		return source.getServer().getProfileCache().get( player ).orElseThrow( ERROR_UNKNOWN_PLAYER::create );
	}
}
