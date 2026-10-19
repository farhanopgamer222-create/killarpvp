package com.example.elytracombat.combat;

import com.example.elytracombat.config.ModConfig;
import com.example.elytracombat.inventory.InventoryManager;
import com.example.elytracombat.util.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Rocket management — ensures rockets are selectable without spamming inventory.
 */
public final class RocketManager {

    private RocketManager() {}

    public static boolean selectRockets(MinecraftClient client, ModConfig config) {
        if (client.player == null) return false;
        PlayerEntity player = client.player;

        int rocketSlot = ItemUtils.findRockets(player);
        if (rocketSlot == -1) return false;

        int rocketCount = ItemUtils.countRockets(player);

        // If already in hotbar, just select
        if (rocketSlot < 9) {
            // Optionally move to preferred slot if autoMove enabled and not already there
            if (config.rockets.autoMove) {
                int pref = clamp(config.rockets.preferredSlot);
                if (rocketSlot != pref) {
                    // Only move if preferred slot is empty or also rockets (to avoid overwriting)
                    ItemStack prefStack = player.getInventory().getStack(pref);
                    if (prefStack.isEmpty() || prefStack.isOf(net.minecraft.item.Items.FIREWORK_ROCKET)) {
                        boolean moved = InventoryManager.moveToHotbar(client, rocketSlot, pref);
                        if (moved) {
                            player.getInventory().selectedSlot = pref;
                            sendFeedback(client, config, rocketCount);
                            return true;
                        }
                    }
                }
            }
            player.getInventory().selectedSlot = rocketSlot;
            sendFeedback(client, config, rocketCount);
            return true;
        }

        // In main inventory
        if (!config.rockets.autoMove) {
            // Without auto-move, we still need to bring to hotbar to use — but respect config
            // So we don't move, we just fail? Instead we do minimal move to preferred if empty
            // Spec: "Optionally move them to the configured Rocket Slot"
            return false;
        }

        int pref = clamp(config.rockets.preferredSlot);
        // Check preferred slot safety
        ItemStack prefStack = player.getInventory().getStack(pref);
        if (!prefStack.isEmpty() && !prefStack.isOf(net.minecraft.item.Items.FIREWORK_ROCKET)) {
            // Preferred occupied by non-rocket — try to find empty hotbar slot instead
            for (int i = 0; i < 9; i++) {
                if (player.getInventory().getStack(i).isEmpty()) {
                    pref = i;
                    break;
                }
            }
        }

        boolean moved = InventoryManager.moveToHotbar(client, rocketSlot, pref);
        if (moved) {
            player.getInventory().selectedSlot = pref;
            sendFeedback(client, config, rocketCount);
            return true;
        }
        return false;
    }

    public static int getRocketCount(PlayerEntity player) {
        return ItemUtils.countRockets(player);
    }

    private static void sendFeedback(MinecraftClient client, ModConfig config, int count) {
        if (!config.effects.actionBarMessages) return;
        Text base = Text.translatable("text.elytra-combat.rocketsReady", String.valueOf(count));
        if (config.messages.rocketReady != null && !config.messages.rocketReady.isEmpty()) {
            Text custom = ItemUtils.formatMessage(config.messages.rocketReady + " — " + count);
            client.player.sendMessage(custom, true);
        } else {
            client.player.sendMessage(base, true);
        }
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(8, v));
    }
}
