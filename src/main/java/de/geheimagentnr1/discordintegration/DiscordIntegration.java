package de.geheimagentnr1.discordintegration;

import de.geheimagentnr1.discordintegration.config.ServerConfig;
import de.geheimagentnr1.discordintegration.elements.commands.ModCommandsRegisterFactory;
import de.geheimagentnr1.discordintegration.handlers.DiscordMessageHandler;
import de.geheimagentnr1.discordintegration.net.DiscordNet;
import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
		
		DistExecutor.safeRunWhenOn(
			Dist.DEDICATED_SERVER,
			() -> () -> {
				DiscordNet discordNet = registerEventHandler( new DiscordNet( this ) );
				ServerConfig serverConfig = registerConfig(
					abstractMod -> new ServerConfig( abstractMod, discordNet )
				);
				registerEventHandler( new ModCommandsRegisterFactory( discordNet, serverConfig ) );
				registerEventHandler( new DiscordMessageHandler( discordNet, serverConfig ) );
			}
		);
	}
}
