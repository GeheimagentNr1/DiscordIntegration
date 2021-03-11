package de.geheimagentnr1.discordintegration.config;


import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class CommandConfig extends AbstractCommentedConfig {
	
	
	private static final ForgeConfigSpec SPEC;
	
	private static final String DISCORD_COMMAND_NAME = "discord_command";
	
	private static final String DISCORD_COMMAND_COMMENT = "Dicord command without prefix";
	
	private static final String MINECRAFT_COMMAND_NAME = "minecraft_command";
	
	private static final String MINECRAFT_COMMAND_COMMENT = "Minecraft command without prefix ('/')";
	
	private static final String ENABLED_NAME = "enabled";
	
	private static final String ENABLED_COMMENT = "Should the command be active?";
	
	private static final String DESCRIPTION_NAME = "description";
	
	private static final String DESCRIPTION_COMMENT = "Description for the help command";
	
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment( DISCORD_COMMAND_COMMENT ).define( DISCORD_COMMAND_NAME, "" );
		builder.comment( MINECRAFT_COMMAND_COMMENT ).define( MINECRAFT_COMMAND_NAME, "" );
		builder.comment( ENABLED_COMMENT ).define( ENABLED_NAME, true );
		builder.comment( DESCRIPTION_COMMENT ).define( DESCRIPTION_NAME, "" );
		SPEC = builder.build();
	}
	
	private CommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( "OverridableMethodCallDuringObjectConstruction" )
	CommandConfig( String discordCommand, String minecraftCommand, boolean enabled, String description ) {
		
		super( () -> {
			HashMap<String, Object> defaultValues = new HashMap<>();
			defaultValues.put( DISCORD_COMMAND_NAME, discordCommand );
			defaultValues.put( MINECRAFT_COMMAND_NAME, minecraftCommand );
			defaultValues.put( ENABLED_NAME, enabled );
			defaultValues.put( DESCRIPTION_NAME, description );
			return defaultValues;
		} );
		setComment( DISCORD_COMMAND_NAME, DISCORD_COMMAND_COMMENT );
		setComment( MINECRAFT_COMMAND_NAME, MINECRAFT_COMMAND_COMMENT );
		setComment( ENABLED_NAME, ENABLED_COMMENT );
		setComment( DESCRIPTION_NAME, DESCRIPTION_COMMENT );
	}
	
	//package-private
	static boolean isCorrect( Object object ) {
		
		if( object instanceof AbstractCommentedConfig ) {
			return SPEC.isCorrect( (AbstractCommentedConfig)object );
		}
		return false;
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@Override
	public final CommandConfig clone() {
		
		return new CommandConfig( this, mapCreator );
	}
	
	@Override
	public CommentedConfig createSubConfig() {
		
		return new CommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			getEnabled( this ),
			getDescription( this )
		);
	}
	
	@Override
	public ConfigFormat<?> configFormat() {
		
		return SPEC.configFormat();
	}
	
	public static String getDiscordCommand( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( DISCORD_COMMAND_NAME );
	}
	
	public static String getMinecraftCommand( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( MINECRAFT_COMMAND_NAME );
	}
	
	public static boolean getEnabled( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( ENABLED_NAME );
	}
	
	public static String getDescription( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( DESCRIPTION_NAME );
	}
}
