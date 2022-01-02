package de.geheimagentnr1.discordintegration.elements.discord.linkings.patch;

import com.flipkart.zjsonpatch.InvalidJsonPatchException;
import com.mantledillusion.essentials.json.patch.PatchUtil;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linking;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.MinecraftGameProfile;

import java.util.UUID;


public class PatchUtilClassLoadFixer {
	
	public static void fixPatchUtilClassLoading() {
		
		//Fix Class not found exception on Update whitelist
		@SuppressWarnings( "DuplicatedCode" )
		Linking found = new Linking();
		found.setDiscordMemberId( 1L );
		found.setDiscordName( null );
		found.setHasRole( false );
		found.setActive( true );
		found.setMessageId( 1L );
		found.setMinecraftGameProfile( MinecraftGameProfile.builder()
			.uuid( UUID.randomUUID() )
			.name( "Test" )
			.build() );
		@SuppressWarnings( "DuplicatedCode" )
		Linking linking = new Linking();
		linking.setDiscordMemberId( 1L );
		linking.setDiscordName( null );
		linking.setHasRole( false );
		linking.setActive( true );
		linking.setMessageId( 1L );
		linking.setMinecraftGameProfile( MinecraftGameProfile.builder()
			.uuid( UUID.randomUUID() )
			.name( "Test" )
			.build() );
		PatchUtil.Snapshot snapshot = PatchUtil.take( linking );
		linking.setDiscordName( "Test" );
		linking.setHasRole( true );
		linking.setActive( false );
		linking.setMessageId( null );
		linking.getMinecraftGameProfile().setName( "Test 2" );
		PatchUtil.apply( found, snapshot.capture() );
		//noinspection ResultOfObjectAllocationIgnored,ThrowableNotThrown
		new InvalidJsonPatchException( "Dummy" );
	}
}
