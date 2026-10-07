package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import de.geheimagentnr1.discordintegration.elements.discord.DiscordManager;
import de.geheimagentnr1.discordintegration.elements.discord.chat.ChatManager;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linkings;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@SuppressWarnings( { "SynchronizeOnThis", "NestedSynchronizedStatement" } )
@Log4j2
public class LinkingsFileManager {
	
	
	@NotNull
	private static final File FILE = new File( "linking.json" );
	
	@NotNull
	Linkings load() throws IOException {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsFileManager.class ) {
				try( FileReader fileReader = new FileReader( FILE, StandardCharsets.UTF_8 ) ) {
					Linkings linkings = new GsonBuilder().setPrettyPrinting().create().fromJson(
						fileReader,
						Linkings.class
					);
					if( linkings == null ) {
						linkings = new Linkings();
						save( linkings );
					}
					return linkings;
				} catch( IOException | JsonSyntaxException | JsonIOException exception ) {
					Linkings linkings = new Linkings();
					save( linkings );
					return linkings;
				}
			}
		}
	}
	
	void save( @NotNull Linkings linkings ) throws IOException {
		
		synchronized( DiscordManager.class ) {
			synchronized( LinkingsFileManager.class ) {
				try( FileWriter fileWriter = new FileWriter( FILE, StandardCharsets.UTF_8 ) ) {
					new GsonBuilder().setPrettyPrinting().create().toJson( linkings, fileWriter );
				}
			}
		}
	}
}
