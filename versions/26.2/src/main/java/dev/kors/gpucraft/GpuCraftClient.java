package dev.kors.gpucraft;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class GpuCraftClient implements ClientModInitializer {

    private static KeyMapping overlayKey;
    private static KeyMapping presetKey;

    @Override
    public void onInitializeClient() {
        GpuCraft.setConfig(GpuCraftConfig.load());

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(GpuCraft.MOD_ID, "main"));

        overlayKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.gpucraft.overlay", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F9, category));
        presetKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.gpucraft.preset", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F10, category));

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

        GpuCraft.LOGGER.info("GPUcraft v2 loaded for Minecraft 26.2, preset: {}", GpuCraft.config().preset.label());
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
