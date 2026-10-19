package com.example.elytracombat.hud;

import com.example.elytracombat.ElytraCombatClient;
import com.example.elytracombat.combat.ElytraManager;
import com.example.elytracombat.combat.RocketManager;
import com.example.elytracombat.config.ModConfig;
import com.example.elytracombat.util.ItemUtils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Minimal, premium HUD — fully configurable position/scale/visibility.
 * Renders only when enabled and not in debug (F3) clutter.
 */
public class CombatHud implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        ModConfig config = ElytraCombatClient.getConfig();
        if (config == null || !config.hud.enabled) return;

        // Hide when GUI open to avoid overlap
        if (client.currentScreen != null) return;

        PlayerEntity player = client.player;

        int x = config.hud.x;
        int y = config.hud.y;
        float scale = config.hud.scale;

        ctx.getMatrices().push();
        ctx.getMatrices().scale(scale, scale, 1.0f);
        // Adjust for scale so x/y remain logical pixels
        int sx = (int)(x / scale);
        int sy = (int)(y / scale);

        int lineHeight = config.hud.compact ? 10 : 12;
        int padding = 6;
        int width = 148;
        int height = computeHeight(config);

        // Background
        if (config.hud.background) {
            // Premium dark with subtle accent border
            ctx.fill(sx - 2, sy - 2, sx + width + 2, sy + height + 2, 0xFF0F1419);
            ctx.fill(sx - 2, sy - 2, sx + width + 2, sy + 1, 0xFF00D9FF); // top accent line
            ctx.fill(sx - 2, sy - 2, sx - 1, sy + height + 2, 0x2600D9FF); // left glow
        }

        int curY = sy + padding;

        // Title
        Text title = Text.literal("⚡ ELYTRA COMBAT").formatted(Formatting.AQUA, Formatting.BOLD);
        ctx.drawText(client.textRenderer, title, sx + padding, curY, 0xFFFFFFFF, true);
        curY += lineHeight + 2;

        // Divider
        ctx.fill(sx + padding, curY, sx + width - padding, curY + 1, 0x30FFFFFF);
        curY += 4;

        if (config.hud.showElytra) {
            boolean equipped = ElytraManager.isElytraEquipped(player);
            boolean hasElytra = equipped || ItemUtils.findElytra(player) != -1;
            String icon = "🪽";
            String status = equipped ? "EQUIPPED" : (hasElytra ? "READY" : "MISSING");
            int color = equipped ? 0xFF00FF88 : (hasElytra ? 0xFF00D9FF : 0xFFFF5555);
            drawLine(ctx, client, sx + padding, curY, icon + " Elytra:", status, color);
            curY += lineHeight;
        }

        if (config.hud.showMace) {
            int maceSlot = ItemUtils.findBestMaceSlot(player);
            boolean has = maceSlot != -1;
            String status = has ? "READY" : "MISSING";
            int color = has ? 0xFFFFD700 : 0xFFFF5555;
            drawLine(ctx, client, sx + padding, curY, "⚔ Mace:", status, color);
            curY += lineHeight;
        }

        if (config.hud.showRockets) {
            int count = ItemUtils.countRockets(player);
            String status = count > 0 ? String.valueOf(count) : "NONE";
            int color = count > 0 ? 0xFFFF8844 : 0xFFFF5555;
            drawLine(ctx, client, sx + padding, curY, "🚀 Rockets:", status, color);
            curY += lineHeight;
        }

        if (config.hud.showChestplate) {
            boolean hasSaved = ElytraManager.hasSavedChestplate();
            ItemStack chest = player.getInventory().getArmorStack(2);
            boolean isChestplate = !chest.isEmpty() && !chest.isOf(Items.ELYTRA);
            String status;
            int color;
            if (isChestplate) {
                status = "EQUIPPED";
                color = 0xFF55FF55;
            } else if (hasSaved) {
                status = "SAVED";
                color = 0xFF00D9FF;
            } else {
                status = "NONE";
                color = 0xFFAAAAAA;
            }
            drawLine(ctx, client, sx + padding, curY, "🛡 Chestplate:", status, color);
            curY += lineHeight;
        }

        if (config.hud.showCombatMode) {
            boolean combat = config.combatMode.enabled;
            String status = combat ? "ON" : "OFF";
            int color = combat ? 0xFF00FF88 : 0xFF888888;
            drawLine(ctx, client, sx + padding, curY, "⚡ Combat:", status, color);
        }

        ctx.getMatrices().pop();
    }

    private int computeHeight(ModConfig config) {
        int lines = 1; // title
        if (config.hud.showElytra) lines++;
        if (config.hud.showMace) lines++;
        if (config.hud.showRockets) lines++;
        if (config.hud.showChestplate) lines++;
        if (config.hud.showCombatMode) lines++;
        int lineHeight = config.hud.compact ? 10 : 12;
        return 14 + lines * lineHeight + 8;
    }

    private void drawLine(DrawContext ctx, MinecraftClient client, int x, int y, String label, String value, int valueColor) {
        ctx.drawText(client.textRenderer, Text.literal(label).formatted(Formatting.GRAY), x, y, 0xFFCCCCCC, true);
        int labelW = client.textRenderer.getWidth(label) + 4;
        ctx.drawText(client.textRenderer, Text.literal(value), x + labelW, y, valueColor, true);
    }
}
