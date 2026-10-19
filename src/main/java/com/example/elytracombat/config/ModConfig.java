package com.example.elytracombat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clean JSON configuration — mirrors the YAML example from spec.
 * Auto-creates with sensible defaults, hot-reloadable via GUI.
 */
public class ModConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("elytra-combat-config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "elytra-combat.json";

    // === Elytra ===
    public ElytraConfig elytra = new ElytraConfig();
    public static class ElytraConfig {
        public boolean enabled = true;
        public boolean restoreChestplate = true;
        public boolean instantSwap = true;
        public boolean requireOnGroundCheck = false;
    }

    // === Mace ===
    public MaceConfig mace = new MaceConfig();
    public static class MaceConfig {
        public boolean enabled = true;
        public boolean moveToHotbar = true;
        /** Preferred hotbar slot 0-8 (config shows 1-9) */
        public int preferredSlot = 1; // hotbar index 1 => slot 2 visually
        public boolean smartSelection = true;
        public boolean prioritizeHotbar = true;
    }

    // === Rockets ===
    public RocketConfig rockets = new RocketConfig();
    public static class RocketConfig {
        public boolean enabled = true;
        public int preferredSlot = 2;
        public boolean autoMove = false;
        public boolean countDisplay = true;
    }

    // === Combat Mode ===
    public CombatModeConfig combatMode = new CombatModeConfig();
    public static class CombatModeConfig {
        public boolean enabled = false;
        public boolean autoRocketPrepare = true;
        public boolean extraFastSwap = true;
    }

    // === HUD ===
    public HudConfig hud = new HudConfig();
    public static class HudConfig {
        public boolean enabled = true;
        public int x = 10;
        public int y = 10;
        public boolean showElytra = true;
        public boolean showMace = true;
        public boolean showRockets = true;
        public boolean showChestplate = true;
        public boolean showCombatMode = true;
        public float scale = 1.0f;
        public boolean background = true;
        public boolean compact = false;
    }

    // === Visual Effects ===
    public EffectsConfig effects = new EffectsConfig();
    public static class EffectsConfig {
        public boolean sounds = true;
        public boolean particles = true;
        public boolean actionBarMessages = true;
        public boolean titleFlash = false;
        public float soundVolume = 0.9f;
    }

    // === Messages (supports & color codes) ===
    public MessagesConfig messages = new MessagesConfig();
    public static class MessagesConfig {
        public String elytraEquipped = "&b✈ Elytra Equipped";
        public String elytraRestored = "&a🛡 Chestplate Restored";
        public String maceSelected = "&6⚔ Mace Selected";
        public String rocketReady = "&c🚀 Rockets Ready";
        public String noMace = "&cNo Mace Found!";
        public String noElytra = "&cNo Elytra Found!";
        public String inventoryFull = "&cInventory full — cannot swap safely!";
    }

    // === GUI ===
    public GuiConfig gui = new GuiConfig();
    public static class GuiConfig {
        public String title = "⚡ ELYTRA COMBAT ⚡";
        public boolean blurBackground = true;
        public boolean animations = true;
        public String accentColor = "#00D9FF";
        public String backgroundColor = "#0F1419";
    }

    // === Persistence ===
    public static ModConfig load() {
        Path path = getConfigPath();
        if (Files.exists(path)) {
            try {
                String json = Files.readString(path);
                ModConfig cfg = GSON.fromJson(json, ModConfig.class);
                if (cfg != null) {
                    cfg.validate();
                    return cfg;
                }
            } catch (Exception e) {
                LOGGER.warn("[ElytraCombat] Failed to load config, using defaults: {}", e.getMessage());
            }
        }
        return new ModConfig();
    }

    public void save() {
        try {
            Path path = getConfigPath();
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(this));
        } catch (IOException e) {
            LOGGER.error("[ElytraCombat] Failed to save config", e);
        }
    }

    private void validate() {
        mace.preferredSlot = clamp(mace.preferredSlot, 0, 8);
        rockets.preferredSlot = clamp(rockets.preferredSlot, 0, 8);
        hud.x = Math.max(0, hud.x);
        hud.y = Math.max(0, hud.y);
        hud.scale = Math.max(0.5f, Math.min(2.0f, hud.scale));
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }
}
