package de.geheimagentnr1.discordintegration.config.command_config;


import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public abstract class CommandConfig extends AbstractCommentedConfig {
	
	
	@NotNull
	private static final ForgeConfigSpec SPEC;
	
	@NotNull
	private static final String DISCORD_COMMAND_NAME = "discord_command";
	
	@NotNull
	private static final String DISCORD_COMMAND_COMMENT = "Discord command without prefix";
	
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
	private static final String MANAGEMENT_COMMAND_NAME = "management_command";
	
	@NotNull
	private static final String MANAGEMENT_COMMAND_COMMENT =
		"Should this command only be usable by Discord users with the management role?";
	
	@NotNull
	private static final String DESCRIPTION_NAME = "description";
	
	@NotNull
	private static final String DESCRIPTION_COMMENT = "Description for the help command " +
		"(Available parameters: %command% = Command, " +
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
		@NotNull UnmodifiableCommentedConfig toCopy,
		@SuppressWarnings( "ParameterHidesMemberVariable" ) @NotNull Supplier<Map<String, Object>> mapCreator ) {
		
		super( toCopy, mapCreator );
	}
	
	@SuppressWarnings( "OverridableMethodCallDuringObjectConstruction" )
	protected CommandConfig(
		@NotNull String discordCommand,
		@NotNull String minecraftCommand,
		boolean useParameters,
		boolean enabled,
		boolean managementCommand,
		String description ) {
		@NotNull String description ) {
		
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
	
	public static boolean isCorrect( @NotNull Object object ) {
		
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
	
	public static boolean useParameters( @NotNull AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( USE_PARAMETERS_NAME );
	}
	
	public static boolean isEnabled( @NotNull AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( ENABLED_NAME );
	}
	
	public static boolean isManagementCommand( AbstractCommentedConfig abstractCommentedConfig ) {
		
		return abstractCommentedConfig.get( MANAGEMENT_COMMAND_NAME );
	}
	
	@NotNull
	public static String getDescription( @NotNull AbstractCommentedConfig abstractCommentedConfig ) {
		
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
