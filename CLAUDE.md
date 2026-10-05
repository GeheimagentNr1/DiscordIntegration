# CLAUDE.md - Discord Integration

## Projekt-Übersicht

**Discord Integration** ist ein NeoForge Minecraft Mod (serverseitig).
- **Mod ID**: `discordintegration`
- **Package**: `de.geheimagentnr1.discordintegration`
- **Java Version**: 21 (`develop_26.1`/`develop_26.3`: 25, `jdk-25.0.4.7-hotspot`)
- **NeoForge Version**: je Branch, siehe Tabelle

| Branch | MC | Range | NeoForge (kompiliert gegen) | Jackson | Hinweis |
|---|---|---|---|---|---|
| `develop_1.21.1` | 1.21.1 | `[1.21.1,1.21.2)` | `21.1.216` | gebündelt `[2.18.2,3)` | Fix-Release 4.0.2 (Bot-Nachrichten, Rechte `/discord linkings`) |
| `develop_1.21.2` | 1.21.2 - 1.21.8 | `[1.21.2,1.21.9)` | `21.2.1-beta` | **aus Minecraft (2.13.4)**, nur `jackson-datatype-jsr310` `[2.13.4,2.14)` gebündelt | `visitGameRuleTypes` über die Server-Instanz, `isTame()` statt `getOwnerUUID()`, Raw-JSON über `ArgumentType.parse`, GameTest entfernt |
| `develop_1.21.9` | 1.21.9 - 1.21.10 | `[1.21.9,1.21.11)` | `21.9.16-beta` | aus Minecraft (2.13.4) | Whitelist/Linkings mit `NameAndId` |
| `develop_1.21.11` | 1.21.11 | `[1.21.11,1.21.12)` | `21.11.45` | gebündelt `[2.18.2,3)` | `Commands.hasPermission`, `LevelBasedPermissionSet` für die Discord-Befehlsquelle, typisierte Game-Rules über das Level |
| `develop_26.1` | 26.1 - 26.2 | `[26.1,26.3)` | `26.1.0.19-beta` (Java 25) | gebündelt | `time query daytime` → `time query day` (World Clocks; alter Config-Wert wird in `CommandConfig.getMinecraftCommand()` übersetzt) |
| `develop_26.3` | 26.3 | `[26.3,27)` | `26.3.0.36-beta` (Java 25) | gebündelt | `DisplayInfo`-Record (`title()`, `announceToChat()`), `CommandSourceStack` ohne Textnamen |

Alle 4.0.2, released 2026-10-05 (ingame getestet auf 1.21.1, 1.21.2, 1.21.8, 1.21.9, 1.21.10, 1.21.11, 26.1, 26.2, 26.3 mit den Testfällen aus `_Testfälle/`, siehe `TESTMATRIX.md`). Details: [`../Docs/migrations/1.21.1-to-1.21.2.md`](../Docs/migrations/1.21.1-to-1.21.2.md) 4j.

**Fallstricke:**
- **Jackson:** Minecraft 1.21.2 - 1.21.10 bringt Jackson 2.13.4 strikt mit (`minecraft-dependencies`); ein gebündeltes neueres Jackson kollidiert. Diese Branches kompilieren deshalb `compileOnly` gegen 2.13.4. Ab 1.21.11 und in 26.x fehlt Jackson in Minecraft wieder - dort wird es gebündelt.
- **Server-Stopp:** `DiscordManager.stop()` wartet per `awaitShutdown` auf JDA. Ab NeoForge 21.9 schließt FML nach dem Server-Stopp den Mod-Classloader; ein noch laufender JDA-Thread konnte dann `ShutdownEvent` nicht laden und hielt die JVM am Leben (Server-Prozess endete nie).
- **Bot-Nachrichten:** `transmit_bot_messages` leitet Nachrichten anderer Bots weiter, die eigenen Befehlsantworten (Start- **und** End-Markierung, `isMessageBotFeedback`) nicht. Von 3.0.0 bis 4.0.1 war die Bedingung invertiert (GH#35).
- **Befehle:** `DiscordCommand` (ohne Rechte) und `DiscordOpCommand` registrieren beide `/discord`. Brigadier verwirft beim Zusammenführen das `requires` des später registrierten Knotens - deshalb sitzt die Rechteprüfung zusätzlich am Argument `discordMemberId` (bis 4.0.1 war `/discord linkings link|unlink <player> <id>` ohne OP nutzbar).
- Die JarInJar-Bereiche lösen beim Bauen auf die neueste Version auf (z. B. `kotlin-stdlib` Beta) - bei Bedarf festzurren.

Fügt Chat-Verknüpfung zwischen Discord und Minecraft hinzu sowie Discord-Commands für Server-Daten.

## Abhängigkeiten

- **Dimension Access Manager** (`dimension_access_manager`) - Optional
- **More MobGriefing Options** (`moremobgriefingoptions`) - Optional

## Externe Libraries

- **JDA** (Java Discord API)
- **Jackson** (JSON Processing)

## Projektstruktur

```
src/main/java/de/geheimagentnr1/discordintegration/
├── DiscordIntegration.java              # Haupt-Mod-Klasse
├── api/                                 # Eigenes API-Framework
│   ├── AbstractMod.java
│   ├── config/                          # Config-Framework
│   │   ├── AbstractConfig.java
│   │   ├── ConfigValue.java
│   │   └── ...
│   ├── events/                          # Event-Interfaces
│   └── util/                            # Utilities
├── config/                              # Mod-Konfigurationen
│   ├── BotConfig.java
│   ├── ChatConfig.java
│   ├── ServerConfig.java
│   ├── WhitelistConfig.java
│   └── command_config/                  # Command-spezifische Configs
└── ...
```

## Besonderheiten

- **Umfangreiches Config-System**: Eigenes Framework für hierarchische Konfigurationen
- **Server-Only**: `usableOnClientSide=false`
- **Discord Bot**: Vollständige Discord-Bot Integration mit JDA
- **Eigene AbstractMod**: Hat ein eigenes API-Framework

## Code-Stil

- **Annotations**: `@NotNull` aus `org.jetbrains.annotations`
- **Lombok**: Projekt nutzt Lombok
- **Formatierung**: Leerzeichen nach `(` und vor `)` bei Methodenaufrufen

## Build & Test

```bash
./gradlew build
./gradlew runServer
```

## Deployment

- **CurseForge**: `./gradlew curseforge`
- **Modrinth**: `./gradlew modrinth`

## Wichtige Hinweise

1. **Discord Bot Token**: Benötigt einen Discord Bot Token in der Konfiguration
2. **JDA Library**: Nutzt JDA für Discord-Kommunikation

## Testing

### Java-Versionen

Verschiedene Java-Versionen sind unter `C:\Program Files\Eclipse Adoptium` installiert. Für einen Gradle-Build muss die passende Java-Version gewählt werden:

```powershell
# Java 21 für MC 1.20.5+ (NeoForge)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
./gradlew build
```

### Unit Tests (JUnit 5)

Für reine Logik-Tests ohne Minecraft-Abhängigkeiten:

```bash
./gradlew test
```

Tests liegen unter `src/test/java/`. Ergebnisse: `build/reports/tests/test/index.html`

### NeoForge GameTest Framework

Für Integration Tests in einer echten Minecraft-Umgebung:

```bash
./gradlew runGameTestServer
```

GameTest-Klassen werden mit `@GameTestHolder` annotiert und liegen unter `src/main/java/.../elements/gametests/`.

### CI/CD (GitHub Actions)

Der Workflow `.github/workflows/build-and-test.yml` führt automatisch aus:
1. **Build**: Kompiliert den Mod
2. **Unit Tests**: Führt JUnit Tests aus
3. **GameTests**: Startet GameTestServer (optional)

### Was kann automatisiert getestet werden?

| Aspekt | Automatisiert? | Methode |
|--------|----------------|---------|
| Utility-Klassen | ✅ | JUnit |
| Config-Parsing | ✅ | JUnit |
| Commands | ✅ | GameTest |
| Block/Item-Verhalten | ✅ | GameTest |
| Multi-MC-Version | ⚠️ Pro Branch | CI Matrix |

## Referenzen

- [NeoForge Migration Primer](https://docs.neoforged.net/primer/docs/) — Dokumentiert API-Aenderungen zwischen Minecraft/NeoForge-Versionen; nuetzlich fuer die Pruefung von Breaking Changes beim Upgrade auf neue Versionen
