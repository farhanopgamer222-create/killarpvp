package com.example.elytracombat.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Item lookup + smart selection utilities.
 * Never scans every tick — only on keypress.
 */
public final class ItemUtils {

    private ItemUtils() {}

    // ---- Search helpers ----

    public static int findElytra(PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        // Search entire inventory (main + hotbar)
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && s.isOf(Items.ELYTRA)) return i;
        }
        // Also check if already equipped? caller handles
        return -1;
    }

    public static List<Integer> findAllMaces(PlayerEntity player) {
        List<Integer> out = new ArrayList<>();
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && s.isOf(Items.MACE)) out.add(i);
        }
        return out;
    }

    public static int findBestMaceSlot(PlayerEntity player) {
        List<Integer> all = findAllMaces(player);
        if (all.isEmpty()) return -1;
        if (all.size() == 1) return all.get(0);

        PlayerInventory inv = player.getInventory();

        // Priority 1: Already in hotbar
        for (int slot : all) {
            if (slot < 9) return slot; // hotbar is 0-8
        }

        // Priority 2+: Score by durability + enchantments
        int bestSlot = all.get(0);
        int bestScore = scoreMace(inv.getStack(bestSlot));

        for (int i = 1; i < all.size(); i++) {
            int slot = all.get(i);
            int score = scoreMace(inv.getStack(slot));
            if (score > bestScore) {
                bestScore = score;
                bestSlot = slot;
            }
        }
        return bestSlot;
    }

    private static int scoreMace(ItemStack stack) {
        int score = 1000;

        // Durability: more remaining = higher score
        int maxDmg = stack.getMaxDamage();
        int dmg = stack.getOrDefault(DataComponentTypes.DAMAGE, 0);
        int remaining = maxDmg - dmg;
        score += remaining * 2; // weight durability

        // Enchantments bonus — Wind Burst, Density, Breach are valuable for mace PvP
        ItemEnchantmentsComponent enchants = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        score += enchants.getSize() * 150;

        // Unbreakable bonus
        if (stack.contains(DataComponentTypes.UNBREAKABLE)) score += 500;

        // Custom name bonus (player-marked best mace)
        if (stack.contains(DataComponentTypes.CUSTOM_NAME)) score += 50;

        return score;
    }

    public static int findRockets(PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        // Prefer hotbar first
        for (int i = 0; i < 9; i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && s.isOf(Items.FIREWORK_ROCKET)) return i;
        }
        for (int i = 9; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && s.isOf(Items.FIREWORK_ROCKET)) return i;
        }
        return -1;
    }

    public static int countRockets(PlayerEntity player) {
        int total = 0;
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && s.isOf(Items.FIREWORK_ROCKET)) total += s.getCount();
        }
        return total;
    }

    public static boolean isChestplate(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.isOf(Items.NETHERITE_CHESTPLATE)
                || stack.isOf(Items.DIAMOND_CHESTPLATE)
                || stack.isOf(Items.IRON_CHESTPLATE)
                || stack.isOf(Items.CHAINMAIL_CHESTPLATE)
                || stack.isOf(Items.GOLDEN_CHESTPLATE)
                || stack.isOf(Items.LEATHER_CHESTPLATE)
                || stack.isOf(Items.ELYTRA); // consider elytra as chest slot occupant
    }

    public static boolean isElytra(ItemStack stack) {
        return !stack.isEmpty() && stack.isOf(Items.ELYTRA);
    }

    public static Text formatMessage(String raw) {
        // Convert & color codes to § for vanilla formatting
        String formatted = raw.replace('&', '§');
        return Text.literal(formatted);
    }

    public static Text elytraCountText(int count) {
        if (count == 0) return Text.literal("✘ None").formatted(Formatting.RED);
        return Text.literal("✔ " + count).formatted(Formatting.GREEN);
    }
}
