package com.example.elytracombat.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

/**
 * Premium sound feedback — configurable, lightweight.
 */
public final class SoundUtil {

    private SoundUtil() {}

    public static void playElytraEquip(MinecraftClient client) {
        if (client.player == null) return;
        client.player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, 1.0f, 1.1f);
        // Extra subtle woosh
        client.player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.7f, 1.4f);
    }

    public static void playChestplateRestore(MinecraftClient client) {
        if (client.player == null) return;
        client.player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 1.0f, 0.95f);
    }

    public static void playMaceSelect(MinecraftClient client) {
        if (client.player == null) return;
        client.player.playSound(SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY, 0.6f, 1.2f);
        // Fallback to generic if heavy not available
        client.player.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 0.5f, 1.8f);
    }

    public static void playRocketReady(MinecraftClient client) {
        if (client.player == null) return;
        client.player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.8f, 1.3f);
    }

    public static void playToggle(MinecraftClient client, boolean enabled) {
        if (client.player == null) return;
        if (enabled) {
            client.player.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 0.8f, 1.2f);
        } else {
            client.player.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 0.8f, 0.9f);
        }
    }

    public static void playError(MinecraftClient client) {
        if (client.player == null) return;
        client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 0.7f, 0.6f);
    }

    public static void playClick(MinecraftClient client) {
        if (client.player == null) return;
        client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.6f, 1.0f);
    }

    public static void playHover(MinecraftClient client) {
        // Very subtle — not needed every hover, used sparingly
    }
}
