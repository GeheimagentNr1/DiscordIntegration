package de.geheimagentnr1.discordintegration.elements.discord.linkings.patch;

import com.mantledillusion.essentials.json.patch.PatchUtil;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linking;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class PatchUtilClassLoadFixer {
	
	
	public static void fixPatchUtilClassLoading() {
		
		Linking test = new Linking();
		PatchUtil.Snapshot snapshot = PatchUtil.take( test );
		test.setDiscordNickname( "test" );
		Linking result = PatchUtil.apply( test, snapshot.capture() );
		log.trace(
			"Bugfix for ClassNotFoundExecption with com.mantledillusion.essentials:json-patch-essentials: {}",
			result.toString()
		);
	}
}
