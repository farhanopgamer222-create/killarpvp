package com.example.elytracombat.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * All keybinds are configurable via Options → Controls.
 * Never hard-coded — exposed to vanilla keybind system.
 */
public final class ModKeybinds {

    public static final String CATEGORY = "category.elytra-combat.main";

    public static KeyBinding ELYTRA_TOGGLE;
    public static KeyBinding MACE_SELECT;
    public static KeyBinding ROCKET_SELECT;
    public static KeyBinding COMBAT_MODE;
    public static KeyBinding OPEN_MENU;

    private ModKeybinds() {}

    public static void register() {
        ELYTRA_TOGGLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elytra-combat.elytraToggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G, // default G — user can rebind
                CATEGORY
        ));

        MACE_SELECT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elytra-combat.maceSelect",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                CATEGORY
        ));

        ROCKET_SELECT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elytra-combat.rocketSelect",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY
        ));

        COMBAT_MODE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elytra-combat.combatMode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                CATEGORY
        ));

        OPEN_MENU = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elytra-combat.openMenu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                CATEGORY
        ));
    }
}
