package de.geheimagentnr1.discordintegration.api.elements.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;


@FunctionalInterface
public interface CommandInterface {
	
	
	default void register( @NotNull CommandDispatcher<CommandSourceStack> dispatcher ) {
		
		dispatcher.register( build() );
	}
	
	@NotNull
	LiteralArgumentBuilder<CommandSourceStack> build();
}
