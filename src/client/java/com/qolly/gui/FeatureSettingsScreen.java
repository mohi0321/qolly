package com.qolly.gui;

import com.qolly.features.Feature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class FeatureSettingsScreen extends Screen {
    private final Screen parent;
    private final Feature feature;
    private ButtonWidget keybindButton;
    private boolean listeningForKey = false;

    public FeatureSettingsScreen(Screen parent, Feature feature) {
        super(Text.of(feature.getName() + " Settings"));
        this.parent = parent;
        this.feature = feature;
    }

    @Override
    protected void init() {
        int y = 50;

        KeyBinding kb = feature.getKeyBinding();
        String keyName = kb != null ? kb.getBoundKeyLocalizedText().getString() : "None";

        keybindButton = ButtonWidget.builder(
                Text.of("Hotkey: " + keyName),
                b -> {
                    listeningForKey = !listeningForKey;
                    updateKeybindButton();
                })
                .dimensions(this.width / 2 - 100, y, 200, 20)
                .build();

        this.addDrawableChild(keybindButton);

        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Back"), b -> this.close())
                .dimensions(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private void updateKeybindButton() {
        if (listeningForKey) {
            keybindButton.setMessage(Text.of("Press any key... (Esc to cancel, Del to clear)"));
        } else {
            KeyBinding kb = feature.getKeyBinding();
            String keyName = kb != null ? kb.getBoundKeyLocalizedText().getString() : "None";
            keybindButton.setMessage(Text.of("Hotkey: " + keyName));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningForKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                listeningForKey = false;
            } else if (keyCode == GLFW.GLFW_KEY_DELETE) {
                feature.setKeyBinding(null);
                listeningForKey = false;
            } else {
                // Create a new keybinding or update existing one
                // Since KeyBinding registry is static and usually pre-defined, creating dynamic
                // ones is tricky for persistance.
                // But for runtime, we can just assign a new KeyBinding object or update a
                // proxy.
                // However, `KeyBinding` constructor registers it to the global list which might
                // be bad if we do it repeatedly.
                // Better approach: Feature holds a KeyBinding. If null, we create one.

                // For simplicity in this mod context, let's create a new detached KeyBinding if
                // none exists
                // Note: This won't show up in standard controls menu unless we register it
                // there.

                KeyBinding kb = new KeyBinding(
                        "key.qolly." + feature.getName().toLowerCase().replace(" ", "_"),
                        InputUtil.Type.KEYSYM,
                        keyCode,
                        "category.qolly.features");
                feature.setKeyBinding(kb);
                listeningForKey = false;
            }
            updateKeybindButton();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (listeningForKey) {
            // Handle mouse buttons for keybinds if desired, skipping for now to keep it
            // simple
            // listeningForKey = false;
            // updateKeybindButton();
            // return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
