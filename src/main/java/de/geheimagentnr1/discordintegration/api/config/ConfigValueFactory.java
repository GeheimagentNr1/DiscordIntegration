package de.geheimagentnr1.discordintegration.api.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;


@FunctionalInterface
public interface ConfigValueFactory<T> {
	
	
	@NotNull
	ModConfigSpec.ConfigValue<T> register( @NotNull ModConfigSpec.Builder _builder, @NotNull String _path );
}
