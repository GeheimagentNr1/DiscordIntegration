package de.geheimagentnr1.discordintegration.api.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import de.geheimagentnr1.discordintegration.api.AbstractMod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;


@Log4j2
public abstract class AbstractSubConfig extends AbstractConfigValueInterface {
	
	
	@NotNull
	protected final AbstractMod abstractMod;
	
	@Nullable
	private final AbstractSubConfig parent;
	
	@Getter( AccessLevel.PACKAGE )
	private ModConfigSpec.Builder builder;
	
	@NotNull
	private final Map<String, ConfigValue<?>> configValues = new HashMap<>();
	
	@NotNull
	private final Map<String, ListEntryConfigData<?>> subConfigListValues = new HashMap<>();
	
	@NotNull
	private final Map<String, AbstractSubConfig> subConfigs = new HashMap<>();
	
	@Setter( AccessLevel.PACKAGE )
	private String configPath = "";
	
	AbstractSubConfig(
		@NotNull AbstractMod _abstractMod,
		@Nullable AbstractSubConfig _parent,
		@NotNull ModConfigSpec.Builder _builder ) {
		
		abstractMod = _abstractMod;
		parent = _parent;
		builder = _builder;
		init();
	}
	
	protected AbstractSubConfig( @NotNull AbstractMod _abstractMod, @NotNull AbstractSubConfig _parent ) {
		
		abstractMod = _abstractMod;
		parent = _parent;
	}
	
	private void init() {
		
		registerConfigValues();
	}
	
	void init( @NotNull ModConfigSpec.Builder _builder ) {
		
		builder = _builder;
		registerConfigValues();
	}
	
	@NotNull
	protected String getFullPath( @NotNull String path ) {
		
		return pathListToPath( List.of( configPath, path ) );
	}
	
	protected abstract void registerConfigValues();
	
	protected boolean isLoaded() {
		
		return parent != null && parent.isLoaded();
	}
	
	@Override
	protected void push( @NotNull List<String> comment, @NotNull List<String> path ) {
		
		builder.comment( comment.toArray( String[]::new ) ).push( path );
	}
	
	protected void pop() {
		
		builder.pop();
	}
	
	@Override
	protected <T> void registerConfigValue(
		@NotNull List<String> comment,
		@NotNull List<String> path,
		@NotNull ConfigValueFactory<T> builderFunction ) {
		
		ModConfigSpec.Builder configValueBuilder = builder.comment( comment.toArray( String[]::new ) );
		if( path.isEmpty() ) {
			throw new IllegalStateException( "path cannot be empty" );
		}
		
		configValues.put(
			pathListToPath( path ),
			new ConfigValue<>( builderFunction.register( configValueBuilder, path.getLast() ) )
		);
	}
	
	@Override
	protected void registerSubConfig(
		@NotNull List<String> comment,
		@NotNull List<String> path,
		@NotNull AbstractSubConfig subConfig ) {
		
		if( path.isEmpty() ) {
			throw new IllegalStateException( "path cannot be empty" );
		}
		String pathString = pathListToPath( path );
		push( comment, pathString );
		subConfig.setConfigPath( pathListToPath( List.of( configPath, pathString ) ) );
		subConfig.init( builder );
		subConfigs.put(
			pathListToPath( path ),
			subConfig
		);
		pop();
	}
	
	@Override
	protected <T extends AbstractListEntryConfig> void registerSubConfigList(
		@NotNull List<String> comment,
		@NotNull List<String> path,
		@NotNull Class<T> type,
		@NotNull SubConfigListFactory<T> factory,
		@NotNull List<T> subConfigListDefaultValue ) {
		
		if( path.isEmpty() ) {
			throw new IllegalStateException( "path cannot be empty" );
		}
		String pathString = pathListToPath( path );
		subConfigListDefaultValue.forEach( element ->
			element.setConfigPath( pathListToPath( List.of( configPath, pathString ) ) ) );
		subConfigListValues.put(
			pathString,
			new ListEntryConfigData<T>(
				type,
				factory,
				new ConfigValue<>(
					builder.comment( comment.toArray( String[]::new ) )
						.defineList(
							path,
							() -> subConfigListDefaultValue.stream()
								.map( AbstractListEntryConfig::convertToNightConfig )
								.filter( Objects::nonNull )
								.toList(),
							object -> {
								if( object instanceof CommentedConfig commentedConfig ) {
									T entry = factory.register( abstractMod, this, commentedConfig );
									entry.setConfigPath( pathListToPath( List.of( configPath, pathString ) ) );
									return entry.getSpec().isCorrect( commentedConfig );
								}
								return false;
							}
						)
				)
			)
		);
	}
	
	@NotNull
	@Override
	protected <T> T getValue( @NotNull Class<T> clazz, @NotNull String path ) {
		
		return Optional.ofNullable( configValues.get( path ) )
			.map( ConfigValue::getValue )
			.filter( clazz::isInstance )
			.map( clazz::cast )
			.orElseThrow( () -> new IllegalStateException( String.format(
				"Unknown config key %s or invalid type %s",
				path,
				clazz.getName()
			) ) );
	}
	
	@Override
	protected <T> void setValue( @NotNull Class<T> clazz, @NotNull String path, T value ) {
		
		if( isLoaded() ) {
			//noinspection unchecked
			Optional.ofNullable( configValues.get( path ) )
				.filter( configValue -> clazz.isInstance( configValue.getValue() ) )
				.map( configValue -> (ConfigValue<T>)configValue )
				.orElseThrow( () -> new IllegalStateException( String.format(
					"Unknown config key %s or invalid type %s",
					path,
					clazz.getName()
				) ) )
				.setValue( value );
		}
	}
	
	protected <T> Predicate<Object> defaultListPredication( Class<T> type ) {
		
		return o -> {
			if( o instanceof List<?> list ) {
				return list.isEmpty() || type.isInstance( list.get( 0 ) );
			}
			return false;
		};
	}
	
	@NotNull
	@Override
	protected <T> List<T> getListValue( @NotNull Class<T> clazz, @NotNull String path ) {
		
		return Optional.ofNullable( configValues.get( path ) )
			.map( ConfigValue::getValue )
			.filter( List.class::isInstance )
			.map( (Function<Object, List<?>>)List.class::cast )
			.orElseThrow( () -> new IllegalStateException( String.format(
				"Unknown config key %s or invalid type %s",
				path,
				clazz.getName()
			) ) )
			.stream()
			.filter( clazz::isInstance )
			.map( clazz::cast )
			.toList();
	}
	
	@Override
	protected <T> void setListValue( @NotNull Class<T> clazz, @NotNull String path, @NotNull List<T> value ) {
		
		if( isLoaded() ) {
			//noinspection unchecked
			Optional.ofNullable( configValues.get( path ) )
				.filter( configValue -> configValue.getValue() instanceof List )
				.map( configValue -> (ConfigValue<List<T>>)configValue )
				.orElseThrow( () -> new IllegalStateException( String.format(
					"Unknown config key %s or invalid type %s",
					path,
					clazz.getName()
				) ) )
				.setValue( value );
		}
	}
	
	@NotNull
	@Override
	protected <T extends AbstractListEntryConfig> List<T> getSubConfigListValue(
		@NotNull Class<T> clazz,
		@NotNull String path ) {
		
		return Optional.ofNullable( subConfigListValues.get( path ) )
			.map(
				listEntryConfigData -> listEntryConfigData.configValue().getValue().stream()
					.map( commentedConfig -> listEntryConfigData.factory()
						.register( abstractMod, this, commentedConfig ) )
					.filter( clazz::isInstance )
					.map( clazz::cast )
					.peek( entry -> entry.setConfigPath( pathListToPath( List.of( configPath, path ) ) ) )
					.toList()
			)
			.orElseThrow( () -> new IllegalStateException( String.format(
				"Unknown config key %s or invalid type %s",
				path,
				clazz.getName()
			) ) );
	}
	
	@Override
	protected <T extends AbstractListEntryConfig> void setSubConfigListValue(
		@NotNull Class<T> clazz,
		@NotNull String path,
		@NotNull List<T> value ) {
		
		if( isLoaded() ) {
			Optional.ofNullable( subConfigListValues.get( path ) )
				.filter( listEntryConfigData -> clazz.isInstance( listEntryConfigData.type() ) )
				.map( ListEntryConfigData::configValue )
				.orElseThrow( () -> new IllegalStateException( String.format(
					"Unknown config key %s or invalid type %s",
					path,
					clazz.getName()
				) ) )
				.setValue(
					value.stream()
						.map( AbstractListEntryConfig::convertToNightConfig )
						.filter( Objects::nonNull )
						.toList()
				);
		}
	}
	
	@NotNull
	@Override
	protected <T> T getSubConfig( @NotNull Class<T> clazz, @NotNull String path ) {
		
		return Optional.ofNullable( subConfigs.get( path ) )
			.filter( clazz::isInstance )
			.map( clazz::cast )
			.orElseThrow( () -> new IllegalStateException( String.format(
				"Unknown config key %s or invalid type %s",
				path,
				clazz.getName()
			) ) );
	}
	
	protected void printValues( @NotNull String prefix ) {
		
		printValues( prefix, Map.of() );
	}
	
	protected void printValues(
		@NotNull String prefix,
		@NotNull Map<String, Function<String, String>> configValueEntryPrintOverride ) {
		
		for( Map.Entry<String, ConfigValue<?>> configValueEntry : configValues.entrySet() ) {
			log.info(
				"{} = {}",
				pathListToPath( List.of( prefix, configValueEntry.getKey() ) ),
				configValueEntryPrintOverride.getOrDefault( configValueEntry.getKey(), Function.identity() )
					.apply( configValueEntry.getValue().getValue().toString() )
			);
		}
		for( Map.Entry<String, AbstractSubConfig> subConfigEntry : subConfigs.entrySet() ) {
			subConfigEntry.getValue().printValues( pathListToPath( List.of( prefix, subConfigEntry.getKey() ) ) );
		}
		for( Map.Entry<String, ListEntryConfigData<?>> subConfigListEntry : subConfigListValues.entrySet() ) {
			ListEntryConfigData<? extends AbstractListEntryConfig> listEntryConfigData = subConfigListEntry.getValue();
			List<? extends AbstractListEntryConfig> value = listEntryConfigData.configValue().getValue().stream()
				.map( commentedConfig -> listEntryConfigData.factory()
					.register( abstractMod, this, commentedConfig ) )
				.peek( entry -> entry.setConfigPath( pathListToPath( List.of(
					configPath,
					subConfigListEntry.getKey()
				) ) ) )
				.toList();
			
			for( int i = 0; i < value.size(); i++ ) {
				AbstractListEntryConfig abstractListEntryConfig = value.get( i );
				abstractListEntryConfig.printValues( pathListToPath( List.of(
					prefix,
					subConfigListEntry.getKey() + "[" + i + "]"
				) ) );
			}
		}
	}
}
