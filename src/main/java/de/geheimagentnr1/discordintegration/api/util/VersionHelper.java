package de.geheimagentnr1.discordintegration.api.util;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class VersionHelper {
	
	
	public static boolean isDependecyWithVersionPresent( @NotNull String modId, @NotNull String dependencyModId ) {
		
		Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById( modId );
		if( modContainer.isEmpty() ) {
			return false;
		}
		Optional<? extends IModInfo.ModVersion> dependency = modContainer.get()
			.getModInfo()
			.getDependencies()
			.stream()
			.filter( modVersion -> modVersion.getModId().equals( dependencyModId ) )
			.findFirst();
		return ModList.get()
			.getMods()
			.stream()
			.filter( modInfo -> modInfo.getModId().equals( dependencyModId ) )
			.anyMatch( modInfo ->
				dependency.isPresent() && dependency.get().getVersionRange().containsVersion( modInfo.getVersion() )
			);
	}
}
