package de.geheimagentnr1.discordintegration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscordIntegrationTest {

    @Test
    void modIdIsValid() {

        String modId = "discordintegration";
        assertTrue( modId.matches( "[a-z][a-z0-9_]{1,63}" ) );
    }
}
