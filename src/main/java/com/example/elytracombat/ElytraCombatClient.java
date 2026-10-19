package com.example.elytracombat;

import com.example.elytracombat.combat.ElytraManager;
import com.example.elytracombat.combat.MaceManager;
import com.example.elytracombat.combat.RocketManager;
import com.example.elytracombat.config.ModConfig;
import com.example.elytracombat.gui.CombatScreen;
import com.example.elytracombat.hud.CombatHud;
import com.example.elytracombat.keybind.ModKeybinds;
import com.example.elytracombat.util.SoundUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Elytra Combat — Premium Elytra + Mace PvP Utility
 * Main client entrypoint. Handles keybind polling and initializes all subsystems.
 */
public class ElytraCombatClient implements ClientModInitializer {

    public static final String MOD_ID = "elytra-combat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ModConfig config;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[ElytraCombat] Initializing premium Elytra + Mace combat module...");

        // Load config first — everything else reads from it
        config = ModConfig.load();
        config.save(); // ensure defaults written

        // Register keybinds (visible in Controls screen)
        ModKeybinds.register();

        // Register HUD
        HudRenderCallback.EVENT.register(new CombatHud());

        // Main tick loop — ONLY runs on keypress, no background scanning
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // Elytra Toggle — instant swap with safety checks
            while (ModKeybinds.ELYTRA_TOGGLE.wasPressed()) {
                handleElytraToggle(client);
            }

            // Mace Select — smart selection + hotbar move if needed
            while (ModKeybinds.MACE_SELECT.wasPressed()) {
                handleMaceSelect(client);
            }

            // Rocket Select
            while (ModKeybinds.ROCKET_SELECT.wasPressed()) {
                handleRocketSelect(client);
            }

            // Combat Mode Toggle — enables ultra-fast switching + auto HUD
            while (ModKeybinds.COMBAT_MODE.wasPressed()) {
                toggleCombatMode(client);
            }

            // Open Modern GUI
            while (ModKeybinds.OPEN_MENU.wasPressed()) {
                client.setScreen(new CombatScreen(null));
            }
        });

        LOGGER.info("[ElytraCombat] Ready — Press {} for Elytra Toggle, {} for Mace Select",
                ModKeybinds.ELYTRA_TOGGLE.getBoundKeyLocalizedText().getString(),
                ModKeybinds.MACE_SELECT.getBoundKeyLocalizedText().getString());
    }

    private void handleElytraToggle(MinecraftClient client) {
        if (!config.elytra.enabled) return;
        boolean success = ElytraManager.toggle(client, config);
        if (success) {
            if (ElytraManager.isElytraEquipped(client.player)) {
                SoundUtil.playElytraEquip(client);
            } else {
                SoundUtil.playChestplateRestore(client);
            }
        }
    }

    private void handleMaceSelect(MinecraftClient client) {
        if (!config.mace.enabled) return;
        boolean found = MaceManager.selectBestMace(client, config);
        if (found) {
            SoundUtil.playMaceSelect(client);
        } else {
            if (config.effects.actionBarMessages) {
                client.player.sendMessage(Text.translatable("text.elytra-combat.noMace"), true);
            }
            SoundUtil.playError(client);
        }
    }

    private void handleRocketSelect(MinecraftClient client) {
        if (!config.rockets.enabled) return;
        boolean found = RocketManager.selectRockets(client, config);
        if (found) {
            SoundUtil.playRocketReady(client);
        } else {
            if (config.effects.actionBarMessages) {
                client.player.sendMessage(Text.translatable("text.elytra-combat.noRockets"), true);
            }
            SoundUtil.playError(client);
        }
    }

    private void toggleCombatMode(MinecraftClient client) {
        config.combatMode.enabled = !config.combatMode.enabled;
        config.save();
        if (config.effects.actionBarMessages) {
            if (config.combatMode.enabled) {
                client.player.sendMessage(Text.translatable("text.elytra-combat.combatEnabled"), true);
            } else {
                client.player.sendMessage(Text.translatable("text.elytra-combat.combatDisabled"), true);
            }
        }
        SoundUtil.playToggle(client, config.combatMode.enabled);
    }

    public static ModConfig getConfig() {
        return config;
    }
}
