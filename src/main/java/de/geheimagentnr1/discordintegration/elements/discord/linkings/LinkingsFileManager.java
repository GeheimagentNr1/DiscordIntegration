package de.geheimagentnr1.discordintegration.elements.discord.linkings;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.models.Linkings;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


@Slf4j
public class LinkingsFileManager {
	
	
	private static final File FILE = new File( "linking.json" );
	
	public static synchronized Linkings load() throws IOException {
		
		try( FileReader fileReader = new FileReader( FILE ) ) {
			Linkings linkings = new GsonBuilder().setPrettyPrinting().create().fromJson( fileReader, Linkings.class );
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
	
	public static synchronized void save( Linkings linkings ) throws IOException {
		
		try( FileWriter fileWriter = new FileWriter( FILE ) ) {
			new GsonBuilder().setPrettyPrinting().create().toJson( linkings, fileWriter );
		}
	}
}
