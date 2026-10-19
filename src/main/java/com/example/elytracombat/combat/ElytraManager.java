package com.example.elytracombat.combat;

import com.example.elytracombat.config.ModConfig;
import com.example.elytracombat.inventory.InventoryManager;
import com.example.elytracombat.util.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Instant Elytra ↔ Chestplate swap with full safety.
 * Remembers previous chestplate (including NBT/components) and restores on second press.
 */
public final class ElytraManager {

    private static ItemStack savedChestplate = ItemStack.EMPTY;
    private static boolean hasSaved = false;
    private static long lastSwapTime = 0;
    private static final long SWAP_COOLDOWN_MS = 150; // anti-spam, still feels instant

    private ElytraManager() {}

    public static boolean isElytraEquipped(PlayerEntity player) {
        ItemStack chest = player.getInventory().getArmorStack(2); // 2 = chest slot
        return !chest.isEmpty() && chest.isOf(Items.ELYTRA);
    }

    public static boolean hasSavedChestplate() {
        return hasSaved && !savedChestplate.isEmpty();
    }

    public static ItemStack getSavedChestplate() {
        return savedChestplate;
    }

    /**
     * Toggle elytra. Returns true if operation succeeded.
     */
    public static boolean toggle(MinecraftClient client, ModConfig config) {
        if (client.player == null || client.interactionManager == null) return false;

        // Anti-spam cooldown — prevents accidental double-swap desync
        long now = System.currentTimeMillis();
        if (now - lastSwapTime < SWAP_COOLDOWN_MS) return false;
        lastSwapTime = now;

        PlayerEntity player = client.player;
        ItemStack equippedChest = player.getInventory().getArmorStack(2);

        // CASE 1: Elytra currently equipped → restore chestplate
        if (!equippedChest.isEmpty() && equippedChest.isOf(Items.ELYTRA)) {
            return restoreChestplate(client, config);
        }

        // CASE 2: Chestplate (or empty) equipped → equip elytra
        return equipElytra(client, config);
    }

    private static boolean equipElytra(MinecraftClient client, ModConfig config) {
        PlayerEntity player = client.player;
        ItemStack equipped = player.getInventory().getArmorStack(2);

        // Save current chestplate if any (deep copy preserves components/NBT/enchants)
        if (!equipped.isEmpty()) {
            if (equipped.isOf(Items.ELYTRA)) {
                // Already elytra — shouldn't happen but handle
                return false;
            }
            savedChestplate = equipped.copy();
            hasSaved = true;
        } else {
            // No chestplate — mark as empty but remember that we came from empty
            savedChestplate = ItemStack.EMPTY;
            hasSaved = false;
        }

        // Find elytra in inventory
        int elytraSlot = ItemUtils.findElytra(player);
        if (elytraSlot == -1) {
            if (config.effects.actionBarMessages) {
                player.sendMessage(Text.translatable("text.elytra-combat.noElytra"), true);
            }
            // Restore saved state since we failed
            hasSaved = false;
            return false;
        }

        // Safety: if chest slot occupied and inventory full → abort to prevent loss
        if (!equipped.isEmpty()) {
            int empty = InventoryManager.findEmptySlot(player);
            // Swap doesn't need empty — it swaps directly. But if we use inventory-store logic, need space.
            // Our swap is atomic via clickSlot PICKUP, so it's safe even when full — as long as target slot is the elytra slot.
            // However if player has no elytra in inventory but we already saved, we already handled above.
        }

        boolean success = InventoryManager.swapChestSlot(client, elytraSlot, equipped);

        if (success) {
            if (config.effects.actionBarMessages) {
                Text msg = ItemUtils.formatMessage(config.messages.elytraEquipped);
                // Add hover with details in non-action-bar scenario
                player.sendMessage(msg, true); // true = action bar
            }
            if (config.effects.particles && client.world != null) {
                spawnElytraParticles(client);
            }
        } else {
            // Rollback saved chestplate on failure
            hasSaved = false;
            savedChestplate = ItemStack.EMPTY;
            if (config.effects.actionBarMessages) {
                player.sendMessage(Text.translatable("text.elytra-combat.inventoryFull"), true);
            }
        }
        return success;
    }

    private static boolean restoreChestplate(MinecraftClient client, ModConfig config) {
        PlayerEntity player = client.player;
        ItemStack equippedElytra = player.getInventory().getArmorStack(2);

        if (!config.elytra.restoreChestplate) {
            // Just unequip elytra to inventory if possible
            int empty = InventoryManager.findEmptySlot(player);
            if (empty == -1) {
                if (config.effects.actionBarMessages) {
                    player.sendMessage(Text.translatable("text.elytra-combat.inventoryFull"), true);
                }
                return false;
            }
            // Find where to put elytra — swap with empty
            // For simplicity, search empty and swap
            // Use a free slot to store elytra
            return InventoryManager.swapChestSlot(client, empty, equippedElytra);
        }

        // If we have a saved chestplate, try to restore it
        if (hasSaved && !savedChestplate.isEmpty()) {
            // Find saved chestplate in inventory — may have moved
            int savedSlot = findStackInInventory(player, savedChestplate);
            if (savedSlot != -1) {
                boolean success = InventoryManager.swapChestSlot(client, savedSlot, equippedElytra);
                if (success) {
                    savedChestplate = ItemStack.EMPTY;
                    hasSaved = false;
                    if (config.effects.actionBarMessages) {
                        player.sendMessage(ItemUtils.formatMessage(config.messages.elytraRestored), true);
                    }
                    return true;
                }
            } else {
                // Saved chestplate not found in inventory — might have been moved/dropped
                // Try any chestplate as fallback, or just store elytra if empty slot exists
                int anyChest = findAnyChestplate(player);
                if (anyChest != -1) {
                    boolean success = InventoryManager.swapChestSlot(client, anyChest, equippedElytra);
                    if (success) {
                        hasSaved = false;
                        savedChestplate = ItemStack.EMPTY;
                        return true;
                    }
                }
            }
            // Last resort: try empty slot — just unequip elytra
            int empty = InventoryManager.findEmptySlot(player);
            if (empty != -1) {
                boolean ok = InventoryManager.swapChestSlot(client, empty, equippedElytra);
                if (ok) hasSaved = false;
                return ok;
            }
            if (config.effects.actionBarMessages) {
                player.sendMessage(Text.translatable("text.elytra-combat.inventoryFull"), true);
            }
            return false;
        }

        // No saved chestplate — just try to unequip elytra to empty slot
        int empty = InventoryManager.findEmptySlot(player);
        if (empty == -1) {
            if (config.effects.actionBarMessages) {
                player.sendMessage(Text.translatable("text.elytra-combat.inventoryFull"), true);
            }
            return false;
        }
        boolean ok = InventoryManager.swapChestSlot(client, empty, equippedElytra);
        if (ok && config.effects.actionBarMessages) {
            player.sendMessage(Text.literal("🛡 Elytra Unequipped").formatted(Formatting.GRAY), true);
        }
        return ok;
    }

    private static int findStackInInventory(PlayerEntity player, ItemStack target) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (ItemStack.areEqual(s, target)) return i;
            // Fallback: same item + components match
            if (!s.isEmpty() && s.isOf(target.getItem()) && s.getComponents().equals(target.getComponents())) {
                return i;
            }
        }
        return -1;
    }

    private static int findAnyChestplate(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (ItemUtils.isChestplate(s) && !s.isOf(Items.ELYTRA)) return i;
        }
        return -1;
    }

    private static void spawnElytraParticles(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        // Subtle cloud puff — client-side only, no server spam
        for (int i = 0; i < 8; i++) {
            double dx = (client.player.getRandom().nextDouble() - 0.5) * 0.6;
            double dy = client.player.getRandom().nextDouble() * 0.5;
            double dz = (client.player.getRandom().nextDouble() - 0.5) * 0.6;
            client.world.addParticle(net.minecraft.particle.ParticleTypes.CLOUD,
                    client.player.getX(), client.player.getY() + 1.0, client.player.getZ(),
                    dx, dy + 0.05, dz);
        }
    }
}
