package de.geheimagentnr1.discordintegration.api.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import de.geheimagentnr1.discordintegration.api.AbstractMod;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


@Log4j2
public abstract class AbstractListEntryConfig extends AbstractSubConfig {
	
	
	@Getter
	private final ModConfigSpec spec;
	
	private CommentedConfig commentedConfig;
	
	protected AbstractListEntryConfig(
		@NotNull AbstractMod _abstractMod,
		@NotNull AbstractSubConfig _parent,
		@NotNull CommentedConfig _commentedConfig ) {
		
		this( _abstractMod, _parent );
		this.commentedConfig = _commentedConfig;
	}
	
	protected AbstractListEntryConfig( @NotNull AbstractMod _abstractMod, @NotNull AbstractSubConfig _parent ) {
		
		super( _abstractMod, _parent, new ModConfigSpec.Builder() );
		spec = getBuilder().build();
	}
	
	@Override
	protected boolean isLoaded() {
		
		return commentedConfig != null;
	}
	
	@Override
	protected <T> T getValue( @NotNull Class<T> clazz, @NotNull String path ) {
		
		if( commentedConfig == null ) {
			throw new IllegalStateException( "CommentedConfig is not set" );
		}
		Object value = commentedConfig.get( path );
		if( value == null || !clazz.isInstance( value ) ) {
			throw new IllegalStateException( String.format(
				"Unknown config key %s or invalid type %s",
				path,
				clazz.getName()
			) );
		}
		return clazz.cast( value );
	}
	
	@NotNull
	CommentedConfig convertToNightConfig() {
		
		Map<String, Object> valueMap = new HashMap<>();
		spec.getSpec().valueMap().forEach( ( key, value ) -> {
			if( value instanceof ModConfigSpec.ValueSpec valueSpec ) {
				valueMap.put( key, valueSpec.getDefault() );
			}
		} );
		CommentedConfig nightConfig = CommentedConfig.inMemory();
		valueMap.forEach( nightConfig::set );
		spec.getSpec().valueMap().forEach( ( key, value ) -> {
			if( value instanceof ModConfigSpec.ValueSpec valueSpec ) {
				nightConfig.setComment( key, valueSpec.getComment() );
			}
		} );
		return nightConfig;
	}
}
