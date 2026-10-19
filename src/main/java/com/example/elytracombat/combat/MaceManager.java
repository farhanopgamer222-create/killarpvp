package com.example.elytracombat.combat;

import com.example.elytracombat.config.ModConfig;
import com.example.elytracombat.inventory.InventoryManager;
import com.example.elytracombat.util.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Smart Mace selection — finds best mace and selects its hotbar slot.
 * If not in hotbar and config allows, moves to preferred slot via safe SWAP.
 */
public final class MaceManager {

    private MaceManager() {}

    public static boolean selectBestMace(MinecraftClient client, ModConfig config) {
        if (client.player == null) return false;
        PlayerEntity player = client.player;

        int bestSlot = ItemUtils.findBestMaceSlot(player);
        if (bestSlot == -1) return false;

        // Already in hotbar — just select it
        if (bestSlot < 9) {
            player.getInventory().selectedSlot = bestSlot;
            sendFeedback(client, config, bestSlot);
            return true;
        }

        // In inventory — need to move to hotbar if allowed
        if (!config.mace.moveToHotbar) {
            // Can't move, but we can still try to SWAP via picking? Instead just select if config forbids, we fail gracefully
            // Alternative: use pickBlock style inventory swap to hotbar via SWAP key? But config says don't move.
            // So we attempt direct slot pick via inventory tick — no, must move. So inform user.
            return false;
        }

        int preferred = config.mace.preferredSlot;
        // Clamp 0-8
        preferred = Math.max(0, Math.min(8, preferred));

        // Check if preferred slot occupied — SWAP is safe (atomic), so it's okay to overwrite
        // But we warn if overwriting valuable? SWAP just exchanges, doesn't delete, so safe.
        boolean moved = InventoryManager.moveToHotbar(client, bestSlot, preferred);
        if (moved) {
            player.getInventory().selectedSlot = preferred;
            sendFeedback(client, config, preferred);
            return true;
        }

        // Fallback: try any empty hotbar slot
        for (int i = 0; i < 9; i++) {
            ItemStack hot = player.getInventory().getStack(i);
            if (hot.isEmpty()) {
                if (InventoryManager.moveToHotbar(client, bestSlot, i)) {
                    player.getInventory().selectedSlot = i;
                    sendFeedback(client, config, i);
                    return true;
                }
            }
        }
        return false;
    }

    private static void sendFeedback(MinecraftClient client, ModConfig config, int slot) {
        if (!config.effects.actionBarMessages) return;
        String slotNum = String.valueOf(slot + 1);
        Text msg = Text.translatable("text.elytra-combat.maceSelected", slotNum);
        // Use configured message if custom
        if (config.messages.maceSelected != null && !config.messages.maceSelected.isEmpty()) {
            Text custom = ItemUtils.formatMessage(config.messages.maceSelected + " — Slot " + slotNum);
            client.player.sendMessage(custom, true);
        } else {
            client.player.sendMessage(msg, true);
        }
    }
}
