package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.GpuCraft;
import dev.kors.gpucraft.GpuCraftConfig;
import dev.kors.gpucraft.GpuInfo;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Hud.class)
public abstract class HudMixin {

    private static final int PANEL_BG = 0x90000000;
    private static final int TEXT_COLOR = 0xFFE0E0E0;
    private static final int ACCENT_COLOR = 0xFF7BD88F;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void gpucraft$overlay(GuiGraphicsExtractor graphics, DeltaTracker delta, CallbackInfo ci) {
        GpuInfo.init();
        GpuCraft.STATS.endFrame();

        GpuCraftConfig config = GpuCraft.config();
        if (!config.overlay) return;

        Minecraft client = Minecraft.getInstance();
        if (client.getDebugOverlay().showDebugScreen()) return;

        Font font = client.font;
        List<String> lines = new ArrayList<>();
        lines.add("GPUcraft — " + config.preset.label());
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
}
