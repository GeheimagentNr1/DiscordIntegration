package de.geheimagentnr1.discordintegration;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.discord.linkings.patch.PatchUtilClassLoadFixer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;


@Mod( DiscordIntegration.MODID )
public class DiscordIntegration extends AbstractMod {
	
	
	@NotNull
	static final String MODID = "discordintegration";
	
	@NotNull
	@Override
	public String getModId() {
		
		return MODID;
	}
	
	@Override
	protected void initMod() {
		
		PatchUtilClassLoadFixer.fixPatchUtilClassLoading();
		
		ModLoadingContext.get().registerConfig( ModConfig.Type.SERVER, ServerConfig.CONFIG );
	}
}
