package de.geheimagentnr1.discordintegration.config.command_config;


import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public abstract class CommandConfig extends AbstractCommentedConfig {
	
	
	private static final ForgeConfigSpec SPEC;
	
	private static final String DISCORD_COMMAND_NAME = "discord_command";
	
	private static final String DISCORD_COMMAND_COMMENT = "Discord command without prefix";
	
	private static final String MINECRAFT_COMMAND_NAME = "minecraft_command";
	
	private static final String MINECRAFT_COMMAND_COMMENT = "Minecraft command without prefix ('/')";
	
	private static final String USE_PARAMETERS_NAME = "use_parameters";
	
	private static final String USE_PARAMETERS_COMMENT =
		"Should everything attached to the Discord command, be attached to the Minecraft command, too?";
	
	private static final String ENABLED_NAME = "enabled";
	
	private static final String ENABLED_COMMENT = "Should the command be active?";
	
	private static final String MANAGEMENT_COMMAND_NAME = "management_command";
	
	private static final String MANAGEMENT_COMMAND_COMMENT =
		"Should this command only be usable by Discord users with the management role?";
	
	private static final String DESCRIPTION_NAME = "description";
	
	private static final String DESCRIPTION_COMMENT = "Description for the help command" +
		"(Available parameters: %command% = Command," +
		"%command_description_separator% = Command Description Separator)";
	
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment( DISCORD_COMMAND_COMMENT ).define( DISCORD_COMMAND_NAME, "" );
		builder.comment( MINECRAFT_COMMAND_COMMENT ).define( MINECRAFT_COMMAND_NAME, "" );
		builder.comment( USE_PARAMETERS_COMMENT ).define( USE_PARAMETERS_NAME, false );
		builder.comment( ENABLED_COMMENT ).define( ENABLED_NAME, true );
		builder.comment( MANAGEMENT_COMMAND_COMMENT ).define( MANAGEMENT_COMMAND_NAME, false );
		builder.comment( DESCRIPTION_COMMENT ).define( DESCRIPTION_NAME, "" );
		SPEC = builder.build();
	}
	
	protected CommandConfig(
		UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( "OverridableMethodCallDuringObjectConstruction" )
	protected CommandConfig(
		String discordCommand,
		String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand,
		String description ) {
		
		super( () -> {
			HashMap<String, Object> defaultValues = new HashMap<>();
			defaultValues.put( DISCORD_COMMAND_NAME, discordCommand );
			defaultValues.put( MINECRAFT_COMMAND_NAME, minecraftCommand );
			defaultValues.put( USE_PARAMETERS_NAME, useParameters );
			defaultValues.put( ENABLED_NAME, enabled );
			defaultValues.put( MANAGEMENT_COMMAND_NAME, managementCommand );
			defaultValues.put( DESCRIPTION_NAME, description );
			return defaultValues;
		} );
		setComment( DISCORD_COMMAND_NAME, DISCORD_COMMAND_COMMENT );
		setComment( MINECRAFT_COMMAND_NAME, MINECRAFT_COMMAND_COMMENT );
		setComment( USE_PARAMETERS_NAME, USE_PARAMETERS_COMMENT );
		setComment( ENABLED_NAME, ENABLED_COMMENT );
		setComment( MANAGEMENT_COMMAND_NAME, MANAGEMENT_COMMAND_COMMENT );
		setComment( DESCRIPTION_NAME, DESCRIPTION_COMMENT );
	}
	
	public static boolean isCorrect( Object object ) {
		
		if( object instanceof AbstractCommentedConfig ) {
			return SPEC.isCorrect( (AbstractCommentedConfig)object );
		}
		return false;
	}
	
	public boolean shouldBeInCommandList() {
		
		return true;
	}
	
	@Override
	public abstract CommandConfig clone();
	
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
	
	public static boolean useParameters( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( USE_PARAMETERS_NAME );
	}
	
	public static boolean isEnabled( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( ENABLED_NAME );
	}
	
	public static boolean isManagementCommand( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( MANAGEMENT_COMMAND_NAME );
	}
	
	public static String getDescription( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( DESCRIPTION_NAME );
	}
	
	public static void printConfig( Logger logger, String path, AbstractCommentedConfig abstractCommentedConfig ) {
		
		logger.info( "{}.{} = {}", path, DISCORD_COMMAND_NAME, getDiscordCommand( abstractCommentedConfig ) );
		logger.info( "{}.{} = {}", path, MINECRAFT_COMMAND_NAME, getMinecraftCommand( abstractCommentedConfig ) );
		logger.info( "{}.{} = {}", path, USE_PARAMETERS_NAME, useParameters( abstractCommentedConfig ) );
		logger.info( "{}.{} = {}", path, ENABLED_NAME, isEnabled( abstractCommentedConfig ) );
		logger.info( "{}.{} = {}", path, MANAGEMENT_COMMAND_NAME, isManagementCommand( abstractCommentedConfig ) );
		logger.info( "{}.{} = {}", path, DESCRIPTION_NAME, getDescription( abstractCommentedConfig ) );
	}
}
