package dev.kors.gpucraft.gui;

import dev.kors.gpucraft.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class GpuCraftConfigScreen extends Screen {

    private final Screen parent;

    public GpuCraftConfigScreen(Screen parent) {
        super(Text.literal("GPUCraft Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        GpuCraftConfig config = GpuCraft.config();
        int leftX = this.width / 2 - 155;
        int rightX = this.width / 2 + 5;
        int startY = 40;
        int stepY = 24;

        // Row 1: Preset & Max FPS
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Preset: " + config.preset.label()),
                btn -> {
                    Preset next = cyclePreset(config.preset);
                    config.applyPreset(next);
                    btn.setMessage(Text.literal("Preset: " + next.label()));
                    this.clearAndInit();
                }
        ).dimensions(leftX, startY, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Max FPS: " + (config.maxFps == 0 ? "Unlimited" : String.valueOf(config.maxFps))),
                btn -> {
                    int[] fpsSteps = {0, 30, 60, 120, 144, 240, 360};
                    config.maxFps = nextInt(fpsSteps, config.maxFps);
                    config.save();
                    btn.setMessage(Text.literal("Max FPS: " + (config.maxFps == 0 ? "Unlimited" : String.valueOf(config.maxFps))));
                }
        ).dimensions(rightX, startY, 150, 20).build());

        // Row 2: Entity Distance & Background FPS
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Entity Dist: " + config.entityDistance + " blk"),
                btn -> {
                    int[] distances = {16, 24, 28, 40, 64, 96, 128, 256};
                    config.entityDistance = nextInt(distances, config.entityDistance);
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Text.literal("Entity Dist: " + config.entityDistance + " blk"));
                }
        ).dimensions(leftX, startY + stepY, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Background FPS: " + (config.backgroundFps == 0 ? "Unlimited" : String.valueOf(config.backgroundFps))),
                btn -> {
                    int[] bgSteps = {0, 15, 30, 60};
                    config.backgroundFps = nextInt(bgSteps, config.backgroundFps);
                    config.save();
                    btn.setMessage(Text.literal("Background FPS: " + (config.backgroundFps == 0 ? "Unlimited" : String.valueOf(config.backgroundFps))));
                }
        ).dimensions(rightX, startY + stepY, 150, 20).build());

        // Row 3: Block Entity Distance & Occlusion Culling
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Chest/Block Dist: " + config.blockEntityDistance + " blk"),
                btn -> {
                    int[] beDistances = {16, 20, 28, 48, 64, 96, 128};
                    config.blockEntityDistance = nextInt(beDistances, config.blockEntityDistance);
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Text.literal("Chest/Block Dist: " + config.blockEntityDistance + " blk"));
                }
        ).dimensions(leftX, startY + stepY * 2, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Occlusion Culling: " + (config.occlusionCulling ? "ON" : "OFF")),
                btn -> {
                    config.occlusionCulling = !config.occlusionCulling;
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Text.literal("Occlusion Culling: " + (config.occlusionCulling ? "ON" : "OFF")));
                }
        ).dimensions(rightX, startY + stepY * 2, 150, 20).build());

        // Row 4: Item Limit & Weather
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Item Limit: " + (config.maxItemEntities == 0 ? "Unlimited" : String.valueOf(config.maxItemEntities))),
                btn -> {
                    int[] itemLimits = {0, 48, 96, 192, 256, 512};
                    config.maxItemEntities = nextInt(itemLimits, config.maxItemEntities);
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Text.literal("Item Limit: " + (config.maxItemEntities == 0 ? "Unlimited" : String.valueOf(config.maxItemEntities))));
                }
        ).dimensions(leftX, startY + stepY * 3, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Weather: " + (config.renderWeather ? "ON" : "OFF")),
                btn -> {
                    config.renderWeather = !config.renderWeather;
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Text.literal("Weather: " + (config.renderWeather ? "ON" : "OFF")));
                }
        ).dimensions(rightX, startY + stepY * 3, 150, 20).build());

        // Row 5: Particle Distance & HUD Overlay
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Particle Dist: " + config.particleDistance + " blk"),
                btn -> {
                    int[] pDistances = {16, 20, 28, 48, 64, 96};
                    config.particleDistance = nextInt(pDistances, config.particleDistance);
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Text.literal("Particle Dist: " + config.particleDistance + " blk"));
                }
        ).dimensions(leftX, startY + stepY * 4, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("HUD Overlay: " + (config.overlay ? "ON" : "OFF")),
                btn -> {
                    config.overlay = !config.overlay;
                    config.save();
                    btn.setMessage(Text.literal("HUD Overlay: " + (config.overlay ? "ON" : "OFF")));
                }
        ).dimensions(rightX, startY + stepY * 4, 150, 20).build());

        // Row 6: Hotkeys button
        String keyLabel = "Hotkeys (Overlay: " + GpuCraftClient.getOverlayKeyName() + ", Preset: " + GpuCraftClient.getPresetKeyName() + ")";
        addDrawableChild(ButtonWidget.builder(
                Text.literal(keyLabel),
                btn -> {
                    if (this.client != null) {
                        this.client.setScreen(new KeybindsScreen(this, this.client.options));
                    }
                }
        ).dimensions(leftX, startY + stepY * 5 + 4, 310, 20).build());

        // Row 7: Done button
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                btn -> {
                    config.save();
                    this.close();
                }
        ).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("GPUCraft — Optimization Settings"), this.width / 2, 12, 0xFFFFFF);

        GpuInfo.init();
        String gpu = "Active GPU: " + GpuInfo.renderer();
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(gpu), this.width / 2, 25, 0x88FF88);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    private static Preset cyclePreset(Preset current) {
        return GpuCraftClient.nextPreset(current);
    }

    private static int nextInt(int[] array, int current) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == current) {
                return array[(i + 1) % array.length];
            }
        }
        return array[0];
    }
}
