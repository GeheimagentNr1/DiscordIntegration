package de.geheimagentnr1.discordintegration.elements.linking;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.GameProfile;
import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LinkingManager {
	
	
	private static final Logger LOGGER = LogManager.getLogger( LinkingManager.class );
	
	private static final File FILE = new File( "linking.json" );
	
	private static boolean isActive() {
		
		return DiscordNet.isInitialized() && ServerConfig.isDiscordWhitelistEnabled();
	}
	
	private static synchronized Linkings load() throws IOException {
		
		LOGGER.info( "Load linkings" );
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
	
	private static synchronized void save( Linkings linkings ) throws IOException {
		
		LOGGER.info( "Save linkings" );
		try( FileWriter fileWriter = new FileWriter( FILE ) ) {
			new GsonBuilder().setPrettyPrinting().create().toJson( linkings, fileWriter );
		}
	}
	
	private static synchronized void updateWhitelist( Linkings linkings ) {
		
		Guild guild = DiscordNet.getGuild();
		List<SimpleGameProfile> activateList = new ArrayList<>();
		List<SimpleGameProfile> deactivateList = new ArrayList<>();
		
		LOGGER.info( "Update Whitelist" );
		for( Linking linking : linkings.getLinkings() ) {
			Member member = guild.getMemberById( linking.getDiscordMemberId() );
			if( member == null ) {
				LOGGER.info( "Removed linking of player {}", linking.getMinecraftGameProfile().getName() );
				linkings.remove( linking );
				LinkingMessageSender.deleteMessage( linking );
			} else {
				if( linking.isActive() ) {
					if( hasCorrectRole( member ) ) {
						LOGGER.info( "Member {} has role", member.getEffectiveName() );
						activateList.add( linking.getMinecraftGameProfile() );
					} else {
						LOGGER.info( "Member {} has not role", member.getEffectiveName() );
						deactivateList.add( linking.getMinecraftGameProfile() );
					}
				} else {
					LOGGER.info( "Linking Member {} is inactive", member.getEffectiveName() );
					deactivateList.add( linking.getMinecraftGameProfile() );
				}
				LinkingMessageSender.updateMessage( linking );
			}
		}
		for( SimpleGameProfile simpleGameProfile : deactivateList.stream()
			.filter( simpleGameProfile -> !activateList.contains( simpleGameProfile ) )
			.toList() ) {
			WhitelistManager.removeFromWhitelist( simpleGameProfile );
		}
		for( SimpleGameProfile simpleGameProfile : activateList ) {
			WhitelistManager.addToWhitelist( simpleGameProfile );
		}
	}
	
	public static synchronized void init() {
		
		if( isActive() ) {
			try {
				LOGGER.info( "Init linkings" );
				Linkings linkings = load();
				updateWhitelist( linkings );
				save( linkings );
			} catch( IOException exception ) {
				LOGGER.error( "Failed initialize whitelist on startup", exception );
			}
		}
	}
	
	public static synchronized boolean hasCorrectRole( Member member ) {
		
		return member.getRoles().stream().anyMatch( LinkingManager::isCorrectRole );
	}
	
	private static synchronized boolean isCorrectRole( Role role ) {
		
		return role.getIdLong() == ServerConfig.getWhitelistRole();
	}
	
	public static synchronized void createLinking( Member member, GameProfile gameProfile ) throws IOException {
		
		if( isActive() ) {
			Linkings linkings = load();
			LOGGER.info( "Create linking" );
			SimpleGameProfile minecraftGameProfile = new SimpleGameProfile( gameProfile );
			Linking linking = new Linking(
				member.getIdLong(),
				member.getUser().getName(),
				null,
				false,
				minecraftGameProfile
			);
			Message message = LinkingMessageSender.createOrSendMessage( member, linking );
			linkings.add( linking );//TODO: Boolean
			linking.setMessageId( message.getIdLong() );
			save( linkings );
			updateWhitelist( linkings );
		}
	}
	
	public static synchronized void removeLinking( Member member, GameProfile gameProfile ) throws IOException {
		
		if( isActive() ) {
			Linkings linkings = load();
			LOGGER.info( "Remove linking" );
			SimpleGameProfile minecraftGameProfile = new SimpleGameProfile( gameProfile );
			Linking linking = linkings.find( new Linking(
				member.getIdLong(),
				member.getUser().getName(),
				null,
				false,
				minecraftGameProfile
			) );
			if( linking != null ) {
				LinkingMessageSender.deleteMessage( linking );
				if( linkings.remove( linking ) ) {
					WhitelistManager.removeFromWhitelist( minecraftGameProfile );
				}
				save( linkings );
			}
			updateWhitelist( linkings );
		}
	}
	
	public static synchronized void handleRoleAdded( Member member ) {
		
		if( isActive() ) {
			try {
				updateWhitelist( load() );
			} catch( IOException exception ) {
				LOGGER.error( "Failed to add player {} to whitelist on role add", member.getEffectiveName() );
				LOGGER.error(
					String.format( "Failed to add player %s to whitelist on role add", member.getEffectiveName() ),
					exception
				);
			}
		}
	}
	
	public static synchronized void handleRoleRemoved( Member member ) {
		
		if( isActive() ) {
			try {
				updateWhitelist( load() );
			} catch( IOException exception ) {
				LOGGER.error(
					String.format(
						"Failed to remove player %s from whitelist on role remove",
						member.getEffectiveName()
					),
					exception
				);
			}
		}
	}
	
	public static Linking activateLinking( Long messageId ) {
		
		if( isActive() ) {
			try {
				Linkings linkings = load();
				LOGGER.info( "Activate linking" );
				
				Linking linking = linkings.find( messageId );
				if( linking != null ) {
					linking.setActive( true );
				}
				save( linkings );
				updateWhitelist( linkings );
				return linking;
			} catch( IOException exception ) {
				LOGGER.error( "Failed to activate link", exception );
			}
		}
		return null;
	}
	
	public static Linking deactivateLinking( Long messageId ) {
		
		if( isActive() ) {
			try {
				Linkings linkings = load();
				LOGGER.info( "Deactivate linking" );
				
				Linking linking = linkings.find( messageId );
				if( linking != null ) {
					linking.setActive( false );
				}
				save( linkings );
				updateWhitelist( linkings );
				return linking;
			} catch( IOException exception ) {
				LOGGER.error( "Failed to activate link", exception );
			}
		}
		return null;
	}
	
	public static void removeLinkings( Member member ) {
		
		try {
			if( isActive() ) {
				Linkings linkings = load();
				LOGGER.info( "Remove linkings" );
				for( Linking linking : linkings.removeLinkings( member.getIdLong() ) ) {
					LinkingMessageSender.deleteMessage( linking );
					if( linkings.remove( linking ) ) {
						WhitelistManager.removeFromWhitelist( linking.getMinecraftGameProfile() );
					}
				}
				save( linkings );
				updateWhitelist( linkings );
			}
		} catch( IOException exception ) {
			//TODO
		}
	}
}
