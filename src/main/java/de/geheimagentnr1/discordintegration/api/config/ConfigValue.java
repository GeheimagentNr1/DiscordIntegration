package de.geheimagentnr1.discordintegration.api.config;

import lombok.RequiredArgsConstructor;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


@RequiredArgsConstructor
public class ConfigValue<T> {
	
	
	private final ModConfigSpec.ConfigValue<T> configValue;
	
	public void setValue( @Nullable T value ) {
		
		configValue.set( value );
	}
	
	@NotNull
	public T getValue() {
		
		return configValue.get();
	}
}
