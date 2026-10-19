package com.example.elytracombat.gui;

import com.example.elytracombat.ElytraCombatClient;
import com.example.elytracombat.combat.ElytraManager;
import com.example.elytracombat.combat.MaceManager;
import com.example.elytracombat.combat.RocketManager;
import com.example.elytracombat.config.ModConfig;
import com.example.elytracombat.util.SoundUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Beautiful modern GUI — premium PvP utility feel.
 * - Custom dark theme with aqua accent
 * - Hover effects, click animations, sounds
 * - Status indicators
 * - Quick actions + Settings
 */
public class CombatScreen extends Screen {

    private final Screen parent;
    private ModConfig config;

    // Animation state
    private float fade = 0f;
    private long openTime = 0;

    // Layout
    private static final int PANEL_W = 320;
    private static final int PANEL_H = 280;
    private static final int BTN_W = 260;
    private static final int BTN_H = 28;

    public CombatScreen(Screen parent) {
        super(Text.literal("⚡ Elytra Combat"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.config = ElytraCombatClient.getConfig();
        this.openTime = System.currentTimeMillis();

        int cx = this.width / 2;
        int cy = this.height / 2;
        int left = cx - PANEL_W / 2;
        int btnX = cx - BTN_W / 2;
        int startY = cy - 90;

        // 🪽 Elytra Mode button
        addDrawableChild(createActionButton(btnX, startY, BTN_W, BTN_H,
                Text.literal("🪽  ELYTRA MODE").formatted(Formatting.AQUA, Formatting.BOLD),
                Text.literal("Equip / Restore Elytra").formatted(Formatting.GRAY),
                btn -> {
                    MinecraftClient c = MinecraftClient.getInstance();
                    boolean ok = ElytraManager.toggle(c, config);
                    if (!ok) SoundUtil.playError(c); else SoundUtil.playClick(c);
                }));

        // ⚔ Mace button
        addDrawableChild(createActionButton(btnX, startY + 40, BTN_W, BTN_H,
                Text.literal("⚔  MACE").formatted(Formatting.GOLD, Formatting.BOLD),
                Text.literal("Select Mace").formatted(Formatting.GRAY),
                btn -> {
                    MinecraftClient c = MinecraftClient.getInstance();
                    boolean ok = MaceManager.selectBestMace(c, config);
                    if (!ok) SoundUtil.playError(c); else SoundUtil.playMaceSelect(c);
                    // close to let player see action bar feedback
                    // keep open for rapid use — don't close
                }));

        // 🚀 Rockets button
        addDrawableChild(createActionButton(btnX, startY + 80, BTN_W, BTN_H,
                Text.literal("🚀  ROCKETS").formatted(Formatting.RED, Formatting.BOLD),
                Text.literal("Select Rockets").formatted(Formatting.GRAY),
                btn -> {
                    MinecraftClient c = MinecraftClient.getInstance();
                    boolean ok = RocketManager.selectRockets(c, config);
                    if (!ok) SoundUtil.playError(c); else SoundUtil.playRocketReady(c);
                }));

        // ⚙ Settings
        addDrawableChild(ButtonWidget.builder(Text.literal("⚙  SETTINGS").formatted(Formatting.WHITE), btn -> {
                    SoundUtil.playClick(MinecraftClient.getInstance());
                    this.client.setScreen(new CombatSettingsScreen(this));
                }).dimensions(btnX, startY + 120, BTN_W, BTN_H).build());

        // HUD Toggle quick
        addDrawableChild(ButtonWidget.builder(
                        Text.literal(config.hud.enabled ? "👁 HUD: ON" : "👁 HUD: OFF").formatted(config.hud.enabled ? Formatting.GREEN : Formatting.GRAY),
                        btn -> {
                            config.hud.enabled = !config.hud.enabled;
                            config.save();
                            btn.setMessage(Text.literal(config.hud.enabled ? "👁 HUD: ON" : "👁 HUD: OFF").formatted(config.hud.enabled ? Formatting.GREEN : Formatting.GRAY));
                            SoundUtil.playClick(MinecraftClient.getInstance());
                        }).dimensions(btnX, startY + 150, 126, 20).build());

        // Combat Mode toggle
        addDrawableChild(ButtonWidget.builder(
                        Text.literal(config.combatMode.enabled ? "⚡ Combat: ON" : "⚡ Combat: OFF").formatted(config.combatMode.enabled ? Formatting.AQUA : Formatting.GRAY),
                        btn -> {
                            config.combatMode.enabled = !config.combatMode.enabled;
                            config.save();
                            btn.setMessage(Text.literal(config.combatMode.enabled ? "⚡ Combat: ON" : "⚡ Combat: OFF").formatted(config.combatMode.enabled ? Formatting.AQUA : Formatting.GRAY));
                            SoundUtil.playToggle(MinecraftClient.getInstance(), config.combatMode.enabled);
                        }).dimensions(btnX + 134, startY + 150, 126, 20).build());

        // Done
        addDrawableChild(ButtonWidget.builder(Text.literal("Done").formatted(Formatting.GRAY), btn -> close())
                .dimensions(cx - 50, cy + 110, 100, 20).build());
    }

    private ButtonWidget createActionButton(int x, int y, int w, int h, Text title, Text subtitle, ButtonWidget.PressAction onPress) {
        return new ButtonWidget.Builder(title, onPress)
                .dimensions(x, y, w, h)
                .build();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Fade in animation
        long elapsed = System.currentTimeMillis() - openTime;
        fade = Math.min(1f, elapsed / 220f);

        // Blur / dim background
        if (config.gui.blurBackground) {
            renderBackground(ctx, mouseX, mouseY, delta);
        } else {
            ctx.fill(0, 0, width, height, 0x88000000);
        }

        int cx = width / 2;
        int cy = height / 2;
        int left = cx - PANEL_W / 2;
        int top = cy - PANEL_H / 2 + 10;

        // Shadow
        ctx.fill(left + 4, top + 4, left + PANEL_W + 4, top + PANEL_H + 4, 0x33000000);

        // Main panel — premium dark
        int bg = 0xFF121821;
        int border = 0xFF00D9FF;
        ctx.fill(left, top, left + PANEL_W, top + PANEL_H, bg);
        // Top accent line
        ctx.fill(left, top, left + PANEL_W, top + 2, border);
        // Side subtle glow
        ctx.fill(left, top, left + 1, top + PANEL_H, 0x3300D9FF);
        ctx.fill(left + PANEL_W - 1, top, left + PANEL_W, top + PANEL_H, 0x3300D9FF);
        // Inner highlight
        ctx.fill(left + 8, top + 38, left + PANEL_W - 8, top + 39, 0x20FFFFFF);

        // Title
        Text title = Text.literal(config.gui.title).formatted(Formatting.AQUA, Formatting.BOLD);
        // Center title with subtle scale
        ctx.getMatrices().push();
        float scale = 1.15f * (0.96f + 0.04f * fade);
        ctx.getMatrices().translate(cx, top + 16, 0);
        ctx.getMatrices().scale(scale, scale, 1);
        int tw = textRenderer.getWidth(title);
        ctx.drawText(textRenderer, title, -tw / 2, -4, 0xFFFFFFFF, true);
        ctx.getMatrices().pop();

        // Subtitle hint
        Text hint = Text.literal("Premium Elytra + Mace PvP Utility").formatted(Formatting.DARK_GRAY);
        ctx.drawText(textRenderer, hint, cx - textRenderer.getWidth(hint) / 2, top + 28, 0xFF888888, false);

        // Footer keybind hints
        String footer = "G Elytra  •  R Mace  •  V Rockets  •  K Menu";
        ctx.drawText(textRenderer, footer, cx - textRenderer.getWidth(footer) / 2, top + PANEL_H - 16, 0xFF555555, false);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }
}
