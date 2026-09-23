package dev.kors.gpucraft.gui;

import dev.kors.gpucraft.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

public class GpuCraftConfigScreen extends Screen {

    private final Screen parent;

    public GpuCraftConfigScreen(Screen parent) {
        super(Component.literal("GPUcraft Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        GpuCraftConfig config = GpuCraft.config();

        int startY = 40;
        int stepY = 24;
        int leftX = this.width / 2 - 155;
        int rightX = this.width / 2 + 5;

        addRenderableWidget(Button.builder(
                Component.literal("Preset: " + config.preset.label()),
                btn -> {
                    config.preset = cyclePreset(config.preset);
                    config.applyPreset(config.preset);
                    config.save();
                    if (this.minecraft != null) {
                        this.minecraft.setScreenAndShow(new GpuCraftConfigScreen(this.parent));
                    }
                }
        ).bounds(leftX, startY, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Max FPS: " + (config.maxFps == 0 ? "Unlimited" : String.valueOf(config.maxFps))),
                btn -> {
                    int[] fpsSteps = {0, 30, 60, 120, 144, 240, 360};
                    config.maxFps = nextInt(fpsSteps, config.maxFps);
                    config.save();
                    btn.setMessage(Component.literal("Max FPS: " + (config.maxFps == 0 ? "Unlimited" : String.valueOf(config.maxFps))));
                }
        ).bounds(rightX, startY, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Entity Dist: " + config.entityDistance + " blk"),
                btn -> {
                    int[] distances = {16, 24, 28, 40, 64, 96, 128, 256};
                    config.entityDistance = nextInt(distances, config.entityDistance);
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Component.literal("Entity Dist: " + config.entityDistance + " blk"));
                }
        ).bounds(leftX, startY + stepY, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Background FPS: " + (config.backgroundFps == 0 ? "Unlimited" : String.valueOf(config.backgroundFps))),
                btn -> {
                    int[] bgSteps = {0, 15, 30, 60};
                    config.backgroundFps = nextInt(bgSteps, config.backgroundFps);
                    config.save();
                    btn.setMessage(Component.literal("Background FPS: " + (config.backgroundFps == 0 ? "Unlimited" : String.valueOf(config.backgroundFps))));
                }
        ).bounds(rightX, startY + stepY, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Chest/Block Dist: " + config.blockEntityDistance + " blk"),
                btn -> {
                    int[] beDistances = {16, 20, 28, 48, 64, 96, 128};
                    config.blockEntityDistance = nextInt(beDistances, config.blockEntityDistance);
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Component.literal("Chest/Block Dist: " + config.blockEntityDistance + " blk"));
                }
        ).bounds(leftX, startY + stepY * 2, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Occlusion Culling: " + (config.occlusionCulling ? "ON" : "OFF")),
                btn -> {
                    config.occlusionCulling = !config.occlusionCulling;
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Component.literal("Occlusion Culling: " + (config.occlusionCulling ? "ON" : "OFF")));
                }
        ).bounds(rightX, startY + stepY * 2, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Item Limit: " + (config.maxItemEntities == 0 ? "Unlimited" : String.valueOf(config.maxItemEntities))),
                btn -> {
                    int[] itemLimits = {0, 48, 96, 192, 256, 512};
                    config.maxItemEntities = nextInt(itemLimits, config.maxItemEntities);
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Component.literal("Item Limit: " + (config.maxItemEntities == 0 ? "Unlimited" : String.valueOf(config.maxItemEntities))));
                }
        ).bounds(leftX, startY + stepY * 3, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Weather: " + (config.renderWeather ? "ON" : "OFF")),
                btn -> {
                    config.renderWeather = !config.renderWeather;
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Component.literal("Weather: " + (config.renderWeather ? "ON" : "OFF")));
                }
        ).bounds(rightX, startY + stepY * 3, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Particle Dist: " + config.particleDistance + " blk"),
                btn -> {
                    int[] pDistances = {16, 20, 28, 48, 64, 96};
                    config.particleDistance = nextInt(pDistances, config.particleDistance);
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Component.literal("Particle Dist: " + config.particleDistance + " blk"));
                }
        ).bounds(leftX, startY + stepY * 4, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("GPU Pipeline: " + (config.pipelineOptimization ? "ON" : "OFF")),
                btn -> {
                    config.pipelineOptimization = !config.pipelineOptimization;
                    config.preset = Preset.CUSTOM;
                    config.save();
                    btn.setMessage(Component.literal("GPU Pipeline: " + (config.pipelineOptimization ? "ON" : "OFF")));
                }
        ).bounds(rightX, startY + stepY * 4, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("HUD Overlay: " + (config.overlay ? "ON" : "OFF")),
                btn -> {
                    config.overlay = !config.overlay;
                    config.save();
                    btn.setMessage(Component.literal("HUD Overlay: " + (config.overlay ? "ON" : "OFF")));
                }
        ).bounds(leftX, startY + stepY * 5 + 4, 150, 20).build());

        String keyLabel = "Hotkeys: " + GpuCraftClient.getOverlayKeyName() + " / " + GpuCraftClient.getPresetKeyName();
        addRenderableWidget(Button.builder(
                Component.literal(keyLabel),
                btn -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreenAndShow(new KeyBindsScreen(this, this.minecraft.options));
                    }
                }
        ).bounds(rightX, startY + stepY * 5 + 4, 150, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Done"),
                btn -> {
                    config.save();
                    this.onClose();
                }
        ).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.centeredText(this.font, Component.literal("GPUCraft — Optimization Settings"), this.width / 2, 12, 0xFFFFFF);

        GpuInfo.init();
        String gpu = "Active GPU: " + GpuInfo.renderer();
        graphics.centeredText(this.font, Component.literal(gpu), this.width / 2, 25, 0x88FF88);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreenAndShow(this.parent);
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
