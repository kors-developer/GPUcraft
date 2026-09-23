package dev.kors.gpucraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class GpuCraftClient implements ClientModInitializer {

    private static KeyBinding overlayKey;
    private static KeyBinding presetKey;

    @Override
    public void onInitializeClient() {
        GpuCraft.setConfig(GpuCraftConfig.load());

        overlayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gpucraft.overlay",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "key.category.gpucraft.main"
        ));

        presetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gpucraft.preset",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F10,
                "key.category.gpucraft.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Stats.tick();

            while (overlayKey.wasPressed()) {
                GpuCraftConfig cfg = GpuCraft.config();
                cfg.overlay = !cfg.overlay;
                cfg.save();
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("[GPUcraft] HUD Overlay: " + (cfg.overlay ? "ON" : "OFF")),
                            true
                    );
                }
            }

            while (presetKey.wasPressed()) {
                GpuCraftConfig cfg = GpuCraft.config();
                Preset next = nextPreset(cfg.preset);
                cfg.applyPreset(next);
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("[GPUcraft] Preset: " + next.label()),
                            true
                    );
                }
            }
        });

        GpuCraft.LOGGER.info("GPUcraft v2 initialized for Minecraft 1.21.x (Preset: {})", GpuCraft.config().preset.label());
    }

    public static String getOverlayKeyName() {
        if (overlayKey == null) return "F9";
        return overlayKey.getBoundKeyLocalizedText().getString();
    }

    public static String getPresetKeyName() {
        if (presetKey == null) return "F10";
        return presetKey.getBoundKeyLocalizedText().getString();
    }

    public static Preset nextPreset(Preset current) {
        // PERFORMANCE -> BALANCED -> QUALITY -> PERFORMANCE
        Preset[] presets = {Preset.PERFORMANCE, Preset.BALANCED, Preset.QUALITY};
        for (int i = 0; i < presets.length; i++) {
            if (presets[i] == current) {
                return presets[(i + 1) % presets.length];
            }
        }
        return Preset.PERFORMANCE;
    }
}
