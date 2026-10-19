# Installation & Build Guide

## Quick Start (Player)

1. **Install Fabric Loader 0.15.11** for Minecraft 1.21.1:
   - https://fabricmc.net/use/installer/

2. **Download Fabric API**:
   - https://modrinth.com/mod/fabric-api
   - Version: `0.107.0+1.21.1` — place in `mods/`

3. **Download Elytra Combat**:
   - Grab `elytra-combat-1.0.0.jar` from Releases → place in `mods/`

4. **Launch** → verify in **Options → Controls → Elytra Combat**

## Build from Source (Developer)

```bash
# Java 21 required
java -version

# Clone
git clone <repo>
cd elytra-combat-mod

# Build
./gradlew build --stacktrace

# Output jar
ls build/libs/
# elytra-combat-1.0.0.jar
```

### Run client for testing
```bash
./gradlew runClient
```

## Dev Environment

- **Minecraft**: 1.21.1
- **Yarn**: 1.21.1+build.3
- **Loader**: 0.15.11
- **Fabric API**: 0.107.0+1.21.1
- **Java**: 21 (required by 1.21)
- **Loom**: 1.7-SNAPSHOT

## Config File

After first launch, edit:
```
config/elytra-combat.json
```
Or in-game: press `K` → `Settings` (live save).

## Troubleshooting

- **Elytra not swapping**: Check `config.elytra.enabled`, verify elytra exists in inventory, check inventory full message.
- **Mace not found**: Ensure `MACE` item exists (1.21+), check `mace.moveToHotbar` toggle.
- **Rockets not selecting**: Enable `rockets.autoMove` if rockets are in main inventory but need hotbar.
- **HUD not showing**: Toggle with `K` → `HUD: ON`, check `hud.enabled` and `hud.x/y`.

