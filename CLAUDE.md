# CLAUDE.md - Discord Integration

## Projekt-Übersicht

**Discord Integration** ist ein NeoForge Minecraft Mod für Minecraft 1.21.1.
- **Mod ID**: `discordintegration`
- **Package**: `de.geheimagentnr1.discordintegration`
- **Java Version**: 21
- **NeoForge Version**: 21.1.x

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
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
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
