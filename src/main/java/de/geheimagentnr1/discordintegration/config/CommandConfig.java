package de.geheimagentnr1.discordintegration.config;


import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class CommandConfig extends AbstractCommentedConfig {
	
	
	@NotNull
	private static final ForgeConfigSpec SPEC;
	
	@NotNull
	private static final String DISCORD_COMMAND_NAME = "discord_command";
	
	@NotNull
	private static final String DISCORD_COMMAND_COMMENT = "Dicord command without prefix";
	
	@NotNull
	private static final String MINECRAFT_COMMAND_NAME = "minecraft_command";
	
	@NotNull
	private static final String MINECRAFT_COMMAND_COMMENT = "Minecraft command without prefix ('/')";
	
	@NotNull
	private static final String USE_PARAMETERS_NAME = "use_parameters";
	
	@NotNull
	private static final String USE_PARAMETERS_COMMENT =
		"Should everything attached to the Discord command, be attached to the Minecraft command, too?";
	
	@NotNull
	private static final String ENABLED_NAME = "enabled";
	
	@NotNull
	private static final String ENABLED_COMMENT = "Should the command be active?";
	
	@NotNull
	private static final String DESCRIPTION_NAME = "description";
	
	@NotNull
	private static final String DESCRIPTION_COMMENT = "Description for the help command";
	
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment( DISCORD_COMMAND_COMMENT ).define( DISCORD_COMMAND_NAME, "" );
		builder.comment( MINECRAFT_COMMAND_COMMENT ).define( MINECRAFT_COMMAND_NAME, "" );
		builder.comment( USE_PARAMETERS_COMMENT ).define( USE_PARAMETERS_NAME, false );
		builder.comment( ENABLED_COMMENT ).define( ENABLED_NAME, true );
		builder.comment( DESCRIPTION_COMMENT ).define( DESCRIPTION_NAME, "" );
		SPEC = builder.build();
	}
	
	private CommandConfig(
		@NotNull UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) @NotNull Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( "OverridableMethodCallDuringObjectConstruction" )
	CommandConfig(
		@NotNull String discordCommand,
		@NotNull String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		@NotNull String description ) {
		
		super( () -> {
			HashMap<String, Object> defaultValues = new HashMap<>();
			defaultValues.put( DISCORD_COMMAND_NAME, discordCommand );
			defaultValues.put( MINECRAFT_COMMAND_NAME, minecraftCommand );
			defaultValues.put( USE_PARAMETERS_NAME, useParameters );
			defaultValues.put( ENABLED_NAME, enabled );
			defaultValues.put( DESCRIPTION_NAME, description );
			return defaultValues;
		} );
		setComment( DISCORD_COMMAND_NAME, DISCORD_COMMAND_COMMENT );
		setComment( MINECRAFT_COMMAND_NAME, MINECRAFT_COMMAND_COMMENT );
		setComment( USE_PARAMETERS_NAME, USE_PARAMETERS_COMMENT );
		setComment( ENABLED_NAME, ENABLED_COMMENT );
		setComment( DESCRIPTION_NAME, DESCRIPTION_COMMENT );
	}
	
	//package-private
	static boolean isCorrect( @NotNull Object object ) {
		
		if( object instanceof AbstractCommentedConfig ) {
			return SPEC.isCorrect( (AbstractCommentedConfig)object );
		}
		return false;
	}
	
	@SuppressWarnings( { "FinalMethod", "UseOfClone" } )
	@NotNull
	@Override
	public final CommandConfig clone() {
		
		return new CommandConfig( this, mapCreator );
	}
	
	@NotNull
	@Override
	public CommentedConfig createSubConfig() {
		
		return new CommandConfig(
			getDiscordCommand( this ),
			getMinecraftCommand( this ),
			getUseParameter( this ),
			getEnabled( this ),
			getDescription( this )
		);
	}
	
	@NotNull
	@Override
	public ConfigFormat<?> configFormat() {
		
		return SPEC.configFormat();
	}
	
	@NotNull
	public static String getDiscordCommand( @NotNull AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( DISCORD_COMMAND_NAME );
	}
	
	@NotNull
	public static String getMinecraftCommand( @NotNull AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( MINECRAFT_COMMAND_NAME );
	}
	
	public static boolean getUseParameter( @NotNull AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( USE_PARAMETERS_NAME );
	}
	
	public static boolean getEnabled( @NotNull AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( ENABLED_NAME );
	}
	
	@NotNull
	public static String getDescription( @NotNull AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( DESCRIPTION_NAME );
	}
}
