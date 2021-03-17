package de.geheimagentnr1.discordintegration.util;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Optional;


public class VersionHelper {
	
	
	public static boolean isDependecyWithVersionPresent( String dependencyModId ) {
		
		Optional<? extends IModInfo.ModVersion> dependency = ModLoadingContext.get()
			.getActiveContainer()
			.getModInfo()
			.getDependencies()
			.stream()
			.filter( modVersion -> modVersion.getModId().equals( dependencyModId ) )
			.findFirst();
		return ModList.get()
			.getMods()
			.stream()
			.filter( modInfo -> modInfo.getModId().equals( dependencyModId ) )
			.anyMatch(
				modInfo ->
					dependency.isPresent() && dependency.get().getVersionRange().containsVersion( modInfo.getVersion() )
			);
	}
}
