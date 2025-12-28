package com.qolly.features;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class FeatureManager {
    private static FeatureManager INSTANCE;
    private final List<Feature> features = new ArrayList<>();

    private FeatureManager() {
        // Private constructor
    }

    public static FeatureManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FeatureManager();
        }
        return INSTANCE;
    }

    public void registerFeature(Feature feature) {
        features.add(feature);
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null)
                return;

            for (Feature feature : features) {
                if (feature.isEnabled()) {
                    feature.onTick();
                }

                // Handle feature specific keybindings
                if (feature.getKeyBinding() != null && feature.getKeyBinding().wasPressed()) {
                    feature.onKeyPressed();
                    // Logic depends on feature.
                    // Some might want toggle on keypress, others might want action.
                    // For now, let's assume features handle their own inputs in onTick or we
                    // delegate here?
                    // Actually, standard keybinding usage is usually polling inside tick, or
                    // listening to events.
                    // But if it's a "toggle" key, we might want to toggle.
                    // However, the prompt says "Right control menu... right click options...
                    // hotkey"
                    // "if you press the hotkey it switches..."
                    // This implies the hotkey performs an ACTION, usually. Or toggles the feature?
                    // "1 feature is elytra chestplate swapper... if you press the hotkey it
                    // switches"
                    // So for swap, it's an action.
                    // "another one is auto harvest... it automatically walk..." -> this is a
                    // toggleable state.

                    // Let's let the feature decide what to do with the keypress?
                    // Or we can say `onKeyPressed`?
                }
            }
        });
    }

    public void onTick() {
        // Called from client tick, delegated from init mostly, but kept if we need
        // manual call.
        // The init() registers the callback.
    }
}
