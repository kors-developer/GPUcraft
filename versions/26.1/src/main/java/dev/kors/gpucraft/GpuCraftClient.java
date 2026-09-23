package dev.kors.gpucraft;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class GpuCraftClient implements ClientModInitializer {

    private static KeyMapping overlayKey;
    private static KeyMapping presetKey;

    private static final int PANEL_BG = 0x90000000;
    private static final int TEXT_COLOR = 0xFFE0E0E0;
    private static final int ACCENT_COLOR = 0xFF7BD88F;

    @Override
    public void onInitializeClient() {
        GpuCraft.setConfig(GpuCraftConfig.load());

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(GpuCraft.MOD_ID, "main"));

        overlayKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.gpucraft.overlay", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F9, category));
        presetKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.gpucraft.preset", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F10, category));

        // Register HUD overlay via Fabric API (works natively across 26.1 and 26.2 without mixin crashes)
        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(GpuCraft.MOD_ID, "overlay"),
                (graphics, deltaTracker) -> renderOverlay(graphics)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            GpuCraft.STATS.onClientTick();
            if (client.level == null) {
                OcclusionCuller.reset();
            }

            while (overlayKey.consumeClick()) {
                GpuCraftConfig config = GpuCraft.config();
                config.overlay = !config.overlay;
                config.save();
                if (client.player != null) {
                    client.player.sendOverlayMessage(
                            Component.literal("GPUcraft: HUD Overlay — " + (config.overlay ? "ON" : "OFF")));
                }
            }

            while (presetKey.consumeClick()) {
                GpuCraftConfig config = GpuCraft.config();
                Preset next = nextPreset(config.preset);
                config.applyPreset(next);
                if (client.player != null) {
                    client.player.sendOverlayMessage(
                            Component.literal("GPUcraft: Preset — " + next.label()));
                }
            }
        });

        GpuCraft.LOGGER.info("GPUcraft v2 loaded for Minecraft 26.1.x, preset: {}", GpuCraft.config().preset.label());
    }

    private static void renderOverlay(GuiGraphicsExtractor graphics) {
        GpuInfo.init();
        GpuCraft.STATS.endFrame();

        GpuCraftConfig config = GpuCraft.config();
        if (!config.overlay) return;

        Minecraft client = Minecraft.getInstance();
        if (client.getDebugOverlay().showDebugScreen()) return;

        Font font = client.font;
        List<String> lines = new ArrayList<>();
        lines.add("GPUcraft v2 — " + config.preset.label());
        lines.add(client.getFps() + " FPS" + (config.maxFps > 0 ? " (limit " + config.maxFps + ")" : ""));
        lines.add("GPU: " + shorten(GpuInfo.renderer()));
        lines.add("Entities: " + visible(GpuCraft.STATS.entitiesSeen(), GpuCraft.STATS.entitiesCulled())
                + " (dist " + config.entityDistance + ")");
        lines.add("Block Entities: " + visible(GpuCraft.STATS.blockEntitiesSeen(), GpuCraft.STATS.blockEntitiesCulled())
                + " (dist " + config.blockEntityDistance + ")");
        lines.add("  occluded: " + GpuCraft.STATS.entitiesOccluded());
        lines.add("Particles culled: " + GpuCraft.STATS.particlesCulled());
        if (!config.renderWeather) {
            lines.add("Weather: Disabled");
        }

        int width = 0;
        for (String line : lines) {
            width = Math.max(width, font.width(line));
        }

        int x = 4;
        int y = 4;
        int padding = 4;
        int lineHeight = font.lineHeight + 1;

        graphics.fill(x, y, x + width + padding * 2, y + lines.size() * lineHeight + padding * 2, PANEL_BG);

        int textY = y + padding;
        for (int i = 0; i < lines.size(); i++) {
            graphics.text(font, lines.get(i), x + padding, textY, i == 0 ? ACCENT_COLOR : TEXT_COLOR);
            textY += lineHeight;
        }
    }

    private static String visible(int seen, int culled) {
        return (seen - culled) + " / " + seen;
    }

    private static String shorten(String name) {
        return name.length() > 34 ? name.substring(0, 33) + "…" : name;
    }

    public static String getOverlayKeyName() {
        if (overlayKey == null) return "F9";
        return overlayKey.getTranslatedKeyMessage().getString();
    }

    public static String getPresetKeyName() {
        if (presetKey == null) return "F10";
        return presetKey.getTranslatedKeyMessage().getString();
    }

    public static Preset nextPreset(Preset current) {
        // PERFORMANCE -> BALANCED -> QUALITY -> PERFORMANCE
        Preset[] cycle = {Preset.PERFORMANCE, Preset.BALANCED, Preset.QUALITY};
        for (int i = 0; i < cycle.length; i++) {
            if (cycle[i] == current) {
                return cycle[(i + 1) % cycle.length];
            }
        }
        return Preset.PERFORMANCE;
    }
}
