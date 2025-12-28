package com.qolly;

import com.qolly.features.FeatureManager;
import com.qolly.gui.ModMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class QollyClient implements ClientModInitializer {
    public static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        // Initialize Feature Manager
        FeatureManager.getInstance().registerFeature(new com.qolly.features.ElytraSwapFeature());
        FeatureManager.getInstance().registerFeature(new com.qolly.features.AutoHarvestFeature());
        FeatureManager.getInstance().init();

        // Register Global Key
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.qolly.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                "category.qolly.general"));

        // Register Tick Event for Menu Opening
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMenuKey.wasPressed()) {
                client.setScreen(new ModMenuScreen(null)); // We'll implement this screen next
            }
        });
    }
}
