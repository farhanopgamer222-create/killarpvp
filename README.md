# ⚡ Elytra Combat — Premium Elytra + Mace PvP Mod

> **Fast + Safe + Smooth + Configurable + Lightweight + PvP-focused**
> A high-quality client-side Fabric mod for **Minecraft 1.21.x** that makes Elytra + Mace combat instant.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.x-00D9FF?style=for-the-badge)
![Fabric](https://img.shields.io/badge/Fabric-API-0d1117?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

---

## ✨ Features

### 🪽 Elytra Quick-Swap (`G`)
- Instantly equip Elytra into chest slot, saving your Chestplate
- Press again to **restore** previous Chestplate perfectly (NBT/components preserved)
- Handles full inventory safely — **never** deletes/dupes items
- Particle + sound + action-bar feedback (`✈ Elytra Equipped`)

### ⚔ Mace Quick-Swap (`R`)
- Searches hotbar → inventory intelligently
- **Smart selection**: prioritizes hotbar → durability → enchantments → overall condition
- Optionally moves best Mace to your configured hotbar slot (default Slot 2)
- Auto-selects slot so you can attack immediately

### 🚀 Rocket Management (`V`)
- Detects Firework Rockets anywhere (hotbar/inventory)
- Optional auto-move to configured Rocket Slot (default Slot 3)
- Never shuffles inventory unnecessarily
- Shows total count in HUD (`🚀 Rockets: 32`)

### ⚡ Combat Mode (`H`)
- One-toggle ultra-fast switching for Elytra ↔ Mace flow:
```
CHESTPLATE → [G] → ELYTRA → [V] Rockets Ready → Fly → [R] MACE → Attack → [G] Restore
```

### 🎨 Beautiful GUI (`K`)
- Modern dark panel with aqua accent, animations, hover sounds
- Quick actions for all 3 systems + Settings + HUD/Combat toggles
- Fully configurable via in-game screen

### 📊 Premium HUD
- Small, non-intrusive overlay:
```
⚡ ELYTRA COMBAT
🪽 Elytra: READY/EQUIPPED
⚔ Mace: READY
🚀 Rockets: 32
🛡 Chestplate: SAVED
⚡ Combat: ON
```
- Position, scale, background toggle, per-line visibility
```yaml
hud:
  enabled: true
  x: 10
  y: 10
  show-elytra: true
  show-mace: true
  show-rockets: true
  show-chestplate: true
```

### 🔒 Inventory Safety (Critical)
The mod **NEVER**:
- Deletes, duplicates, or overwrites items
- Loses enchantments / NBT / DataComponents
- Drops valuables accidentally

Every move:
1. Validates destination slot
2. Verifies inventory space
3. Preserves all components via `copy()`
4. Uses atomic `SlotActionType.SWAP` / `PICKUP` (server-validated)
5. Cancels if unsafe

---

## 🎮 Keybinds (Configurable in Controls)

| Action | Default | Category |
|---|---|---|
| **Elytra Toggle** | `G` | Elytra Combat |
| **Mace Select** | `R` | Elytra Combat |
| **Rocket Select** | `V` | Elytra Combat |
| **Combat Mode** | `H` | Elytra Combat |
| **Open Mod Menu** | `K` | Elytra Combat |

All rebindable in **Options → Controls**.

---

## ⚙ Configuration

File: `config/elytra-combat.json` (auto-generated)

```json
{
  "elytra": {
    "enabled": true,
    "restoreChestplate": true,
    "instantSwap": true
  },
  "mace": {
    "enabled": true,
    "moveToHotbar": true,
    "preferredSlot": 1,
    "smartSelection": true
  },
  "rockets": {
    "enabled": true,
    "preferredSlot": 2,
    "autoMove": false
  },
  "combatMode": {
    "enabled": false,
    "autoRocketPrepare": true
  },
  "hud": {
    "enabled": true,
    "x": 10,
    "y": 10,
    "showElytra": true,
    "showMace": true,
    "showRockets": true,
    "showChestplate": true
  },
  "effects": {
    "sounds": true,
    "particles": true,
    "actionBarMessages": true
  },
  "messages": {
    "elytraEquipped": "&b✈ Elytra Equipped",
    "elytraRestored": "&a🛡 Chestplate Restored",
    "maceSelected": "&6⚔ Mace Selected",
    "rocketReady": "&c🚀 Rockets Ready"
  }
}
```

Also editable in-game via **K → Settings**.

---

## 🚀 Installation

### Requirements
- Minecraft **1.21.1** (also compatible with 1.21.x)
- Fabric Loader **≥0.15.11**
- Fabric API **0.107.0+1.21.1**
- Java **21**

### Steps
1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Download `fabric-api-0.107.0+1.21.1.jar` to `mods/`
3. Download `elytra-combat-1.0.0.jar` (this mod) to `mods/`
4. Launch game → check **Options → Controls → Elytra Combat**

### Build from Source
```bash
git clone https://github.com/example/elytra-combat
cd elytra-combat
./gradlew build
# jar at build/libs/elytra-combat-1.0.0.jar
```

---

## 🏗 Project Structure

```
elytra-combat-mod/
├── src/main/java/com/example/elytracombat/
│   ├── ElytraCombatClient.java      # Entrypoint + tick loop
│   ├── keybind/ModKeybinds.java     # 5 configurable keybinds
│   ├── combat/
│   │   ├── ElytraManager.java       # Instant swap + restore + safety
│   │   ├── MaceManager.java         # Smart selection + hotbar move
│   │   └── RocketManager.java       # Rocket detection + auto-move
│   ├── inventory/InventoryManager.java # Safety layer (no dupe/loss)
│   ├── gui/
│   │   ├── CombatScreen.java        # Modern premium GUI
│   │   └── CombatSettingsScreen.java
│   ├── hud/CombatHud.java           # Configurable overlay
│   ├── config/ModConfig.java        # JSON persistence
│   └── util/
│       ├── ItemUtils.java           # Search + scoring
│       └── SoundUtil.java           # Premium audio feedback
├── src/main/resources/
│   ├── fabric.mod.json
│   ├── assets/elytra_combat/lang/en_us.json
│   └── elytra-combat.mixins.json
└── build.gradle
```

---

## 🎯 Performance

- **Zero** tick-loop inventory scans — only on keypress/GUI open
- No background threads, no constant polling
- Minimal FPS impact — client-side only, server-validated clicks

---

## 📝 License

MIT — see [LICENSE](LICENSE)

---

## 🙏 Credits

Built with [Fabric](https://fabricmc.net/), [Yarn Mappings](https://linkie.shedaniel.me/mappings), Fabric API.
Inspired by premium PvP utility mods — designed to feel **professional**, not like a basic swap mod.

**Enjoy clean Elytra + Mace combos! 🪽⚔🚀**
