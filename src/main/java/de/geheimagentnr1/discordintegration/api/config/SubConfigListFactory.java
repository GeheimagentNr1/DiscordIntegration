package de.geheimagentnr1.discordintegration.api.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import de.geheimagentnr1.discordintegration.api.AbstractMod;
import org.jetbrains.annotations.NotNull;


@FunctionalInterface
public interface SubConfigListFactory<T> {
	
	
	@NotNull
	T register(
		@NotNull AbstractMod _abstractMod,
		@NotNull AbstractSubConfig _parent,
		@NotNull CommentedConfig _commentedConfig );
}
