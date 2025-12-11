package de.geheimagentnr1.discordintegration.api.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public record ListEntryConfigData<T extends AbstractListEntryConfig>(
	@NotNull Class<T> type,
	@NotNull SubConfigListFactory<T> factory,
	@NotNull ConfigValue<List<? extends CommentedConfig>> configValue) {
	
	
}
