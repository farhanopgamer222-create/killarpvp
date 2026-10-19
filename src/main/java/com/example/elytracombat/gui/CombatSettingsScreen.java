package com.example.elytracombat.gui;

import com.example.elytracombat.ElytraCombatClient;
import com.example.elytracombat.config.ModConfig;
import com.example.elytracombat.util.SoundUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Detailed settings screen — all yaml options exposed with live preview.
 */
public class CombatSettingsScreen extends Screen {

    private final Screen parent;
    private ModConfig config;

    public CombatSettingsScreen(Screen parent) {
        super(Text.literal("⚙ Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        config = ElytraCombatClient.getConfig();
        int cx = width / 2;
        int y = height / 2 - 110;
        int w = 140;

        // Row 1: Elytra restore toggle
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Restore Chestplate: " + onOff(config.elytra.restoreChestplate)).formatted(config.elytra.restoreChestplate ? Formatting.GREEN : Formatting.RED),
                btn -> {
                    config.elytra.restoreChestplate = !config.elytra.restoreChestplate;
                    btn.setMessage(Text.literal("Restore Chestplate: " + onOff(config.elytra.restoreChestplate)).formatted(config.elytra.restoreChestplate ? Formatting.GREEN : Formatting.RED));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx - 150, y, w, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Elytra Enabled: " + onOff(config.elytra.enabled)).formatted(config.elytra.enabled ? Formatting.GREEN : Formatting.RED),
                btn -> {
                    config.elytra.enabled = !config.elytra.enabled;
                    btn.setMessage(Text.literal("Elytra Enabled: " + onOff(config.elytra.enabled)).formatted(config.elytra.enabled ? Formatting.GREEN : Formatting.RED));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx + 10, y, w, 20).build());

        y += 24;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Mace Move→Hotbar: " + onOff(config.mace.moveToHotbar)).formatted(config.mace.moveToHotbar ? Formatting.GREEN : Formatting.GRAY),
                btn -> {
                    config.mace.moveToHotbar = !config.mace.moveToHotbar;
                    btn.setMessage(Text.literal("Mace Move→Hotbar: " + onOff(config.mace.moveToHotbar)).formatted(config.mace.moveToHotbar ? Formatting.GREEN : Formatting.GRAY));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx - 150, y, w, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Mace Slot: " + (config.mace.preferredSlot + 1)).formatted(Formatting.GOLD),
                btn -> {
                    config.mace.preferredSlot = (config.mace.preferredSlot + 1) % 9;
                    btn.setMessage(Text.literal("Mace Slot: " + (config.mace.preferredSlot + 1)).formatted(Formatting.GOLD));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx + 10, y, w, 20).build());

        y += 24;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Rocket Auto-Move: " + onOff(config.rockets.autoMove)).formatted(config.rockets.autoMove ? Formatting.GREEN : Formatting.GRAY),
                btn -> {
                    config.rockets.autoMove = !config.rockets.autoMove;
                    btn.setMessage(Text.literal("Rocket Auto-Move: " + onOff(config.rockets.autoMove)).formatted(config.rockets.autoMove ? Formatting.GREEN : Formatting.GRAY));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx - 150, y, w, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Rocket Slot: " + (config.rockets.preferredSlot + 1)).formatted(Formatting.RED),
                btn -> {
                    config.rockets.preferredSlot = (config.rockets.preferredSlot + 1) % 9;
                    btn.setMessage(Text.literal("Rocket Slot: " + (config.rockets.preferredSlot + 1)).formatted(Formatting.RED));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx + 10, y, w, 20).build());

        y += 24;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("HUD: " + onOff(config.hud.enabled)).formatted(config.hud.enabled ? Formatting.AQUA : Formatting.GRAY),
                btn -> {
                    config.hud.enabled = !config.hud.enabled;
                    btn.setMessage(Text.literal("HUD: " + onOff(config.hud.enabled)).formatted(config.hud.enabled ? Formatting.AQUA : Formatting.GRAY));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx - 150, y, w, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Sounds: " + onOff(config.effects.sounds)).formatted(config.effects.sounds ? Formatting.GREEN : Formatting.GRAY),
                btn -> {
                    config.effects.sounds = !config.effects.sounds;
                    btn.setMessage(Text.literal("Sounds: " + onOff(config.effects.sounds)).formatted(config.effects.sounds ? Formatting.GREEN : Formatting.GRAY));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx + 10, y, w, 20).build());

        y += 24;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Particles: " + onOff(config.effects.particles)).formatted(config.effects.particles ? Formatting.GREEN : Formatting.GRAY),
                btn -> {
                    config.effects.particles = !config.effects.particles;
                    btn.setMessage(Text.literal("Particles: " + onOff(config.effects.particles)).formatted(config.effects.particles ? Formatting.GREEN : Formatting.GRAY));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx - 150, y, w, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("ActionBar: " + onOff(config.effects.actionBarMessages)).formatted(config.effects.actionBarMessages ? Formatting.GREEN : Formatting.GRAY),
                btn -> {
                    config.effects.actionBarMessages = !config.effects.actionBarMessages;
                    btn.setMessage(Text.literal("ActionBar: " + onOff(config.effects.actionBarMessages)).formatted(config.effects.actionBarMessages ? Formatting.GREEN : Formatting.GRAY));
                    config.save();
                    SoundUtil.playClick(MinecraftClient.getInstance());
                }).dimensions(cx + 10, y, w, 20).build());

        y += 30;
        // HUD Position controls
        addDrawableChild(ButtonWidget.builder(Text.literal("HUD X −").formatted(Formatting.GRAY),
                btn -> { config.hud.x = Math.max(0, config.hud.x - 10); config.save(); SoundUtil.playClick(MinecraftClient.getInstance()); })
                .dimensions(cx - 150, y, 68, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("HUD X +").formatted(Formatting.GRAY),
                btn -> { config.hud.x += 10; config.save(); SoundUtil.playClick(MinecraftClient.getInstance()); })
                .dimensions(cx - 78, y, 68, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("HUD Y −").formatted(Formatting.GRAY),
                btn -> { config.hud.y = Math.max(0, config.hud.y - 10); config.save(); SoundUtil.playClick(MinecraftClient.getInstance()); })
                .dimensions(cx + 10, y, 68, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("HUD Y +").formatted(Formatting.GRAY),
                btn -> { config.hud.y += 10; config.save(); SoundUtil.playClick(MinecraftClient.getInstance()); })
                .dimensions(cx + 82, y, 68, 20).build());

        y += 26;
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset to Defaults").formatted(Formatting.RED),
                btn -> {
                    config = new ModConfig();
                    config.save();
                    // Reopen to refresh
                    MinecraftClient.getInstance().setScreen(new CombatSettingsScreen(parent));
                    SoundUtil.playError(MinecraftClient.getInstance());
                }).dimensions(cx - 70, y, 140, 20).build());

        // Back
        addDrawableChild(ButtonWidget.builder(Text.literal("← Back").formatted(Formatting.WHITE),
                btn -> close()).dimensions(cx - 50, height / 2 + 90, 100, 20).build());
    }

    private String onOff(boolean v) { return v ? "ON" : "OFF"; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        int cx = width / 2;
        int top = height / 2 - 140;
        ctx.fill(cx - 160, top, cx + 160, top + 290, 0xFF121821);
        ctx.fill(cx - 160, top, cx + 160, top + 2, 0xFF00D9FF);
        Text title = Text.literal("⚙  SETTINGS").formatted(Formatting.WHITE, Formatting.BOLD);
        ctx.drawText(textRenderer, title, cx - textRenderer.getWidth(title) / 2, top + 10, 0xFFFFFFFF, true);
        Text footer = Text.literal("config/elytra-combat.json • Changes save instantly").formatted(Formatting.DARK_GRAY);
        ctx.drawText(textRenderer, footer, cx - textRenderer.getWidth(footer) / 2, top + 26, 0xFF666666, false);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(new CombatScreen(null));
    }
}
