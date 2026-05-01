package de.geheimagentnr1.discordintegration.elements.gametests;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;


@GameTestHolder( "discordintegration" )
public class DiscordIntegrationGameTests {

    @GameTest( templateNamespace = "neoforge", template = "floor_3x3x3" )
    public static void modLoadsSuccessfully( GameTestHelper helper ) {

        helper.succeed();
    }
}
