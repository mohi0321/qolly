package com.qolly.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public abstract class Feature {
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    private final String name;
    private boolean enabled;
    private KeyBinding keyBinding;

    public Feature(String name) {
        this.name = name;
        this.enabled = false;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setKeyBinding(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public void onTick() {
        // Override me
    }

    public void onKeyPressed() {
        // Override me for instant actions
    }

    protected void onEnable() {
        // Override me
    }

    protected void onDisable() {
        // Override me
    }
}
