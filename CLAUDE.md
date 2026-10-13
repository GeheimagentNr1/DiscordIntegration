# CLAUDE.md - Discord Integration

Discord Integration verbindet einen Minecraft-Server über einen Discord-Bot (JDA) mit Discord: Chat in beide Richtungen, Server-Ereignisse (Start/Stopp, Login/Logout, Tode, Advancements), Befehle aus Discord, Verknüpfung von Discord- und Minecraft-Konten mit optionaler Whitelist sowie ein Management-Kanal. Mod-ID `discordintegration`, Package `de.geheimagentnr1.discordintegration`. **Server-only**: NeoForge per `@Mod( value = MODID, dist = Dist.DEDICATED_SERVER )` (seit 4.0.3; 4.0.1/4.0.2 initialisierten auch im Singleplayer), Forge 1.20+ per `DistExecutor.safeRunWhenOn( Dist.DEDICATED_SERVER, .. )`; `displayTest=IGNORE_SERVER_VERSION`.

Diese Datei ist auf allen `develop_*`-Branches identisch; Quelle: `../Docs/mod-claude/DiscordIntegration.md` (Änderungen dort, dann `Docs/tools/sync_mod_claude_md.sh`). Versionsstand: [`../Docs/VERSIONEN.md`](../Docs/VERSIONEN.md).

## Versionen

- Forge: `develop_1.14.4` .. `develop_1.20` mit Mod-Version 2.x (alter, schlanker Aufbau), `develop_1.20.1` .. `develop_1.21` und `develop_1.21.1_forge` mit 3.x (Whitelist, Management-Kanal, konfigurierbare Nachrichten). Alle gepflegten Forge-Branches nutzen JDA 6.
- NeoForge (4.x): sechs Jars - `develop_1.21.1`, `develop_1.21.2` (1.21.2 - 1.21.8), `develop_1.21.9` (1.21.9 - 1.21.10), `develop_1.21.11`, `develop_26.1` (26.1 - 26.2), `develop_26.3`. Details: [`../Docs/migrations/1.21.1-to-1.21.2.md`](../Docs/migrations/1.21.1-to-1.21.2.md) Abschnitt 4j.
- Ranges, Tags und Java-Versionen pro Branch: [`../Docs/VERSIONEN.md`](../Docs/VERSIONEN.md).

## Abhängigkeiten

- Optional: DimensionAccessManager (`dimension_access_manager`, Discord-Befehl `dimensions`) und MoreMobGriefingOptions (`moremobgriefingoptions`, Discord-Befehl `mobgriefing`); für Dev-Runs als `runtimeOnly` aus `ModdingMavenRepositories` (`dam_*`/`mmgo_*` in `gradle.properties`, Secret `MODDING_MAVEN_TOKEN` in der CI).
- Gebündelt (NeoForge per JarInJar, Bereiche in `custom.gradle`): JDA mit nv-websocket-client, okhttp, okio, trove4j, commons-collections4, kotlin-stdlib; json-patch-essentials + zjsonpatch; Jackson `[2.18.2,3)` - **außer 1.21.2 - 1.21.10**: dort Jackson 2.13.4 aus Minecraft (`compileOnly`), nur `jackson-datatype-jsr310` `[2.13.4,2.14)` gebündelt.
- Forge: bis 1.19.4 Shadow-Jar mit Relocation (kotlin, SLF4J 2), ab 1.20 `jarJar`; 1.20 und 1.20.2 - 1.21.1-Forge zusätzlich MinecraftForgeAPI (1.20.1 ohne). Build-Details: [`../Docs/forge/README.md`](../Docs/forge/README.md).

## Architektur

- `DiscordIntegration` - Mod-Klasse (erbt vom eigenen `api/AbstractMod`), baut alle Manager auf und registriert Config, Befehle und `handlers/DiscordMessageHandler` (Minecraft-Ereignisse → Discord).
- `api/` - eigenes kleines Framework (NeoForge): `AbstractMod`, Config-Framework mit Sub-Configs und Listen (`AbstractConfig`, `AbstractSubConfig`, `SubConfigListFactory`, ..), `CommandsRegisterFactory`, `MessageUtil`, `VersionHelper`.
- `config/` - `ServerConfig` mit Sub-Configs `bot` (`BotConfig`, `DiscordPresenceConfig`), `chat`, `management`, `whitelist`, `command_settings` (+ `*MessagesConfig`); `config/command_config/` je ein Discord-Befehl (`difficulty`, `gamerules`, `help`, `link`, `mods`, `online`, `seed`, `time`, `tps`, `unlink`, `mods/dimensions`, `mods/mobgriefing`).
- `elements/discord/` - `DiscordManager` (JDA-Lebenszyklus, Intents inkl. `MESSAGE_CONTENT`, Start-Warnungen), `DiscordMessageBuilder`/`Sender`, `chat/` (Discord → Minecraft), `commands/` (Discord-Befehle über eine eigene `DiscordCommandSourceStack`), `linkings/` (Konten-Verknüpfung, `WhitelistManager`, Datei `linking.json` im Server-Verzeichnis, `PatchUtilClassLoadFixer` gegen `ClassNotFoundException` in json-patch-essentials), `management/`.
- `elements/commands/` - Minecraft-Befehle `DiscordCommand`, `SayToDiscordCommand`, `EmoteToDiscordCommand`.

## Funktionen, Befehle, Konfiguration

- `/discord commands|gamerules|mods` (ohne Rechte), `/discord linkings ...` nur für Admins (Stufe 3, `LEVEL_ADMINS`) oder aus Discord; `link|unlink <player> <discordMemberId>` zusätzlich Stufe 3 am Argument. `/say` (Stufe 2) und `/me` werden vom Mod neu registriert und senden zusätzlich nach Discord.
- Config `world/serverconfig/discordintegration-server.toml`: `bot` (`active`, `bot_token`, `guild_id`, `discord_presence`), `chat` (`channel_id`, Formate inkl. `use_raw_message_format_discord_to_minecraft`, `transmit_bot_messages`, `suppress_server_messages`, `messages`), `management`, `whitelist` (`use_role`, `role_id`, `use_single_linking_management`, `single_linking_management_role_id`, `linking_management_channel_id`), `command_settings` (`command_prefix`, `command_normal_user_permission_level`, `other_bots_command_prefixes`, `commands`). 3.x-Configs sind nicht mit 2.x kompatibel.
- Im Discord Developer Portal müssen die privilegierten Intents (Message Content, Server Members) für den Bot aktiviert sein, sonst kommen Nachrichten leer an.

## Fallstricke

- **Jackson 1.21.2 - 1.21.10**: Minecraft bringt 2.13.4 strikt mit, ein gebündeltes neueres Jackson kollidiert; ab 1.21.11 fehlt es wieder und wird gebündelt.
- **Server-Stopp**: `DiscordManager.stop()` macht `jda.shutdown()` + `awaitShutdown( 10 s )`, sonst `shutdownNow()`. Ab NeoForge 21.9 schließt FML danach den Mod-Classloader - ein JDA-Thread, der noch Klassen lädt, hielt sonst die JVM für immer am Leben.
- **Brigadier-Merge**: zwei Befehle mit Wurzel `/discord` → nur das `requires` des zuerst registrierten Knotens bleibt; Rechteprüfungen deshalb an Knoten, die nur der jeweilige Zweig hat (vor 4.0.2 bzw. 3.2.0 war `/discord linkings link|unlink <player> <id>` ohne OP nutzbar).
- **`transmit_bot_messages`**: leitet Nachrichten anderer Bots weiter, nie die eigenen Befehlsantworten (`isMessageBotFeedback`); von 3.0.0 bis 4.0.1 invertiert (GH#35).
- **Raw-Format**: Discord-Werte werden escaped, sonst konnten Discord-Nutzer eigene Text-Komponenten (z. B. Klick-Events mit Befehlen) einschleusen (4.0.4 / 3.2.0). Raw-JSON wird ab 1.21.5 über `ArgumentType<Component>.parse` gelesen (Cross-fix).
- **26.1**: `/time query daytime` entfernt - Standard `time query day`, alte Werte übersetzt `CommandConfig.getMinecraftCommand()` (nicht `TimeCommandConfig`: die Factory erzeugt beim Laden die Basisklasse). **26.3**: `DisplayInfo`-Record, `CommandSourceStack` ohne Textnamen → eigenes Jar.
- Server-Config nur bei gestopptem Server ändern (der Server schreibt beim Stoppen seine geladenen Werte zurück). JarInJar-Bereiche ohne Pin lösen auf die neueste Version auf (z. B. kotlin-stdlib-Beta). Alle Brüche: [`../Docs/migrations/BREAKING_CHANGES.md`](../Docs/migrations/BREAKING_CHANGES.md).

## Testen

- Braucht einen echten Test-Bot und Test-Guild; Token und IDs nie in Logs, Commits oder Antworten ausgeben. Testfall-Configs liegen lokal in `_Testfälle/` (nie stagen/committen, können Bot-Daten enthalten) und werden als `world/serverconfig/discordintegration-server.toml` eingesetzt.
- Nur in die Server-Testpacks (server-only). [`../Docs/testing/tools/di_starttest.sh`](../Docs/testing/tools/di_starttest.sh) verteilt ein Jar, startet den Server, wartet auf JDA „Finished Loading“ und prüft, dass der Prozess nach dem Stopp endet; `neoforge_smoke_all.sh` enthält den DI-Login.
- Prüfen: Chat beide Richtungen, Discord-Befehle mit Prefix, Linking + Whitelist (Kick-Test braucht `white-list=true` und `enforce-whitelist=true`), Raw-JSON gültig/ungültig, `/discord linkings` ohne OP nicht sichtbar, sauberer Stopp. `transmit_bot_messages` ist ohne zweiten Bot nicht testbar.
- Allgemeine Regeln: [`../Docs/testing/README.md`](../Docs/testing/README.md).

## Links

- Wiki: https://github.com/GeheimagentNr1/DiscordIntegration/wiki (Setup inkl. Intents, Versionsseiten `3.X.X`/`4.X.X`)
- CurseForge: https://www.curseforge.com/minecraft/mc-mods/discord-integration (Projekt-ID 389222)
- Modrinth: https://modrinth.com/mod/discord-integration- (Projekt-ID `wdZidyDu`)
- [`../Docs/migrations/1.21.1-to-1.21.2.md`](../Docs/migrations/1.21.1-to-1.21.2.md) (4j), [`../Docs/forge/README.md`](../Docs/forge/README.md), [`../MOD_KOMPATIBILITAET.md`](../MOD_KOMPATIBILITAET.md), [`../Docs/releasing/release-process.md`](../Docs/releasing/release-process.md)
