package de.geheimagentnr1.discordintegration.api.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;


abstract class AbstractConfigValueInterface {
	
	// Push
	
	protected void push( @NotNull String comment, @NotNull String path ) {
		
		push( List.of( comment ), path );
	}
	
	protected void push( @NotNull List<String> comment, @NotNull String path ) {
		
		push( comment, List.of( path ) );
	}
	
	protected void push( @NotNull String comment, @NotNull List<String> path ) {
		
		push( List.of( comment ), path );
	}
	
	protected abstract void push( @NotNull List<String> comment, @NotNull List<String> path );
	
	// Register with: Comment, Path, DefaultValue
	
	protected <T> void registerConfigValue(
		@NotNull String comment,
		@NotNull String path,
		@NotNull T defaultValue ) {
		
		registerConfigValue( List.of( comment ), path, defaultValue );
	}
	
	protected <T> void registerConfigValue(
		@NotNull List<String> comment,
		@NotNull String path,
		@NotNull T defaultValue ) {
		
		registerConfigValue( comment, List.of( path ), defaultValue );
	}
	
	protected <T> void registerConfigValue(
		@NotNull String comment,
		@NotNull List<String> path,
		@NotNull T defaultValue ) {
		
		registerConfigValue( List.of( comment ), path, defaultValue );
	}
	
	protected <T> void registerConfigValue(
		@NotNull List<String> comment,
		@NotNull List<String> path,
		@NotNull T defaultValue ) {
		
		registerConfigValue( comment, path, defaultValue, false );
	}
	
	// Register with: Comment, Path, DefaultValue, WorldRestart
	
	protected <T> void registerConfigValue(
		@NotNull String comment,
		@NotNull String path,
		@NotNull T defaultValue,
		boolean worldRestart ) {
		
		registerConfigValue( List.of( comment ), path, defaultValue, worldRestart );
	}
	
	protected <T> void registerConfigValue(
		@NotNull List<String> comment,
		@NotNull String path,
		@NotNull T defaultValue,
		boolean worldRestart ) {
		
		registerConfigValue( comment, List.of( path ), defaultValue, worldRestart );
	}
	
	protected <T> void registerConfigValue(
		@NotNull String comment,
		@NotNull List<String> path,
		@NotNull T defaultValue,
		boolean worldRestart ) {
		
		registerConfigValue( List.of( comment ), path, defaultValue, worldRestart );
	}
	
	protected <T> void registerConfigValue(
		@NotNull List<String> comment,
		@NotNull List<String> path,
		@NotNull T defaultValue,
		boolean worldRestart ) {
		
		if( defaultValue instanceof Boolean booleanDefaultValue ) {
			registerConfigValue(
				comment,
				path,
				( builder, elementPath ) -> {
					ModConfigSpec.Builder elementBuilder = builder;
					if( worldRestart ) {
						elementBuilder = elementBuilder.worldRestart();
					}
					return elementBuilder.define(
						elementPath,
						booleanDefaultValue.booleanValue()
					);
				}
			);
		} else {
			registerConfigValue(
				comment,
				path,
				( builder, elementPath ) -> {
					ModConfigSpec.Builder elementBuilder = builder;
					if( worldRestart ) {
						elementBuilder = elementBuilder.worldRestart();
					}
					return elementBuilder.define(
						elementPath,
						defaultValue
					);
				}
			);
		}
	}
	
	// Register with: Comment, Path, BuilderFunction
	
	protected <T> void registerConfigValue(
		@NotNull String comment,
		@NotNull String path,
		@NotNull ConfigValueFactory<T> builderFunction ) {
		
		registerConfigValue( List.of( comment ), path, builderFunction );
	}
	
	protected <T> void registerConfigValue(
		@NotNull List<String> comment,
		@NotNull String path,
		@NotNull ConfigValueFactory<T> builderFunction ) {
		
		registerConfigValue( comment, List.of( path ), builderFunction );
	}
	
	protected <T> void registerConfigValue(
		@NotNull String comment,
		@NotNull List<String> path,
		@NotNull ConfigValueFactory<T> builderFunction ) {
		
		registerConfigValue( List.of( comment ), path, builderFunction );
	}
	
	protected abstract <T> void registerConfigValue(
		@NotNull List<String> comment,
		@NotNull List<String> path,
		@NotNull ConfigValueFactory<T> builderFunction );
	
	// Register SubConfig
	
	protected void registerSubConfig(
		@NotNull String comment,
		@NotNull String path,
		@NotNull AbstractSubConfig subConfig ) {
		
		registerSubConfig( List.of( comment ), path, subConfig );
	}
	
	protected void registerSubConfig(
		@NotNull List<String> comment,
		@NotNull String path,
		@NotNull AbstractSubConfig subConfig ) {
		
		registerSubConfig( comment, List.of( path ), subConfig );
	}
	
	protected void registerSubConfig(
		@NotNull String comment,
		@NotNull List<String> path,
		@NotNull AbstractSubConfig subConfig ) {
		
		registerSubConfig( List.of( comment ), path, subConfig );
	}
	
	protected abstract void registerSubConfig(
		@NotNull List<String> comment,
		@NotNull List<String> path,
		@NotNull AbstractSubConfig subConfig );
	
	// Register SubConfigList
	
	protected <T extends AbstractListEntryConfig> void registerSubConfigList(
		@NotNull String comment,
		@NotNull String path,
		@NotNull Class<T> type,
		@NotNull SubConfigListFactory<T> factory,
		@NotNull List<T> subConfigListDefaultValue ) {
		
		registerSubConfigList( List.of( comment ), path, type, factory, subConfigListDefaultValue );
	}
	
	protected <T extends AbstractListEntryConfig> void registerSubConfigList(
		@NotNull List<String> comment,
		@NotNull String path,
		@NotNull Class<T> type,
		@NotNull SubConfigListFactory<T> factory,
		@NotNull List<T> subConfigListDefaultValue ) {
		
		registerSubConfigList( comment, List.of( path ), type, factory, subConfigListDefaultValue );
	}
	
	protected <T extends AbstractListEntryConfig> void registerSubConfigList(
		@NotNull String comment,
		@NotNull List<String> path,
		@NotNull Class<T> type,
		@NotNull SubConfigListFactory<T> factory,
		@NotNull List<T> subConfigListDefaultValue ) {
		
		registerSubConfigList( List.of( comment ), path, type, factory, subConfigListDefaultValue );
	}
	
	protected abstract <T extends AbstractListEntryConfig> void registerSubConfigList(
		@NotNull List<String> comment,
		@NotNull List<String> path,
		@NotNull Class<T> type,
		@NotNull SubConfigListFactory<T> factory,
		@NotNull List<T> subConfigListDefaultValue );
	
	// Get and Set normal Values
	
	
	@NotNull
	protected <T> T getValue( @NotNull Class<T> clazz, @NotNull List<String> path ) {
		
		return getValue( clazz, pathListToPath( path ) );
	}
	
	@NotNull
	protected abstract <T> T getValue( @NotNull Class<T> clazz, @NotNull String path );
	
	
	protected <T> void setValue( @NotNull Class<T> clazz, @NotNull List<String> path, T value ) {
		
		setValue( clazz, pathListToPath( path ), value );
	}
	
	protected abstract <T> void setValue( @NotNull Class<T> clazz, @NotNull String path, T value );
	
	// Get and Set list Values
	
	@NotNull
	protected <T> List<T> getListValue( @NotNull Class<T> clazz, @NotNull List<String> path ) {
		
		return getListValue( clazz, pathListToPath( path ) );
	}
	
	@NotNull
	protected abstract <T> List<T> getListValue( @NotNull Class<T> clazz, @NotNull String path );
	
	protected <T> void setListValue( @NotNull Class<T> clazz, @NotNull List<String> path, List<T> value ) {
		
		setListValue( clazz, pathListToPath( path ), value );
	}
	
	protected abstract <T> void setListValue( @NotNull Class<T> clazz, @NotNull String path, List<T> value );
	
	// Get SubConfig
	
	@NotNull
	protected <T> T getSubConfig( @NotNull Class<T> clazz, @NotNull List<String> path ) {
		
		return getSubConfig( clazz, pathListToPath( path ) );
	}
	
	
	@NotNull
	protected abstract <T> T getSubConfig( @NotNull Class<T> clazz, @NotNull String path );
	
	// Get and Set SubConfigList
	
	@NotNull
	protected <T extends AbstractListEntryConfig> List<T> getSubConfigListValue(
		@NotNull Class<T> clazz,
		@NotNull List<String> path ) {
		
		return getSubConfigListValue( clazz, pathListToPath( path ) );
	}
	
	@NotNull
	protected abstract <T extends AbstractListEntryConfig> List<T> getSubConfigListValue(
		@NotNull Class<T> clazz,
		@NotNull String path );
	
	protected <T extends AbstractListEntryConfig> void setSubConfigListValue(
		@NotNull Class<T> clazz,
		@NotNull List<String> path,
		@NotNull List<T> value ) {
		
		setSubConfigListValue( clazz, pathListToPath( path ), value );
	}
	
	protected abstract <T extends AbstractListEntryConfig> void setSubConfigListValue(
		@NotNull Class<T> clazz,
		@NotNull String path,
		@NotNull List<T> value );
	
	// Helper
	
	@NotNull
	protected String pathListToPath( @NotNull List<String> path ) {
		
		return path.stream()
			.filter( StringUtils::isNotBlank )
			.collect( Collectors.joining( "." ) );
	}
}
