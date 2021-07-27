package de.geheimagentnr1.discordintegration.util;

import de.geheimagentnr1.discordintegration.DiscordIntegration;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Optional;


public class VersionHelper {
	
	
	public static boolean isDependecyWithVersionPresent( String dependencyModId ) {
		
		Optional<? extends ModContainer> modContainer = ModList.get()
			.getModContainerById( DiscordIntegration.MODID );
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
