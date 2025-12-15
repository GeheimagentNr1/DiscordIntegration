# AGENTS.md - Discord Integration

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
