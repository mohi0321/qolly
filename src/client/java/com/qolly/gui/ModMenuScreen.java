package com.qolly.gui;

import com.qolly.features.Feature;
import com.qolly.features.FeatureManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.List;

public class ModMenuScreen extends Screen {
    private final Screen parent;

    public ModMenuScreen(Screen parent) {
        super(Text.of("Qolly Mod Menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        List<Feature> features = FeatureManager.getInstance().getFeatures();
        int y = 50;

        for (Feature feature : features) {
            ButtonWidget button = ButtonWidget.builder(
                    Text.of(feature.getName() + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                    b -> {
                        feature.toggle();
                        b.setMessage(Text.of(feature.getName() + ": " + (feature.isEnabled() ? "ON" : "OFF")));
                    })
                    .dimensions(this.width / 2 - 100, y, 200, 20)
                    .build();

            // We need to add a custom click handler for right click, but ButtonWidget
            // doesn't support it directly in the builder easily without checking mouse
            // button in onClick or similar.
            // Actually, newer MC versions use `mouseClicked` overrides or custom widgets.
            // For simplicity, let's just make a custom widget that extends ButtonWidget to
            // handle right click.

            this.addDrawableChild(new FeatureButton(
                    this.width / 2 - 100, y, 200, 20,
                    feature,
                    this));

            y += 25;
        }
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

    private static class FeatureButton extends ButtonWidget {
        private final Feature feature;
        private final Screen parentScreen;

        public FeatureButton(int x, int y, int width, int height, Feature feature, Screen parentScreen) {
            super(x, y, width, height, Text.of(feature.getName() + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                    (b) -> {
                        feature.toggle();
                        b.setMessage(Text.of(feature.getName() + ": " + (feature.isEnabled() ? "ON" : "OFF")));
                    }, DEFAULT_NARRATION_SUPPLIER);
            this.feature = feature;
            this.parentScreen = parentScreen;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.clicked(mouseX, mouseY)) {
                if (button == 1) { // Right click
                    this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                    MinecraftClient.getInstance().setScreen(new FeatureSettingsScreen(parentScreen, feature));
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
