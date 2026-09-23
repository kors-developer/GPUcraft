package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    private void gpucraft$renderOverlay(DrawContext context, float tickDelta, CallbackInfo ci) {
        GpuCraftConfig config = GpuCraft.config();
        if (!config.overlay || this.client.options.hudHidden) return;

        TextRenderer font = getTextRenderer();
        if (font == null) return;

        List<String> lines = new ArrayList<>();
        lines.add("GPUcraft | " + config.preset.label());
        lines.add("FPS: " + this.client.getCurrentFps());

        GpuInfo.init();
        lines.add("GPU: " + GpuInfo.renderer());

        lines.add(String.format("Entities: %d culled / %d rendered",
                Stats.lastCulledEntities, Stats.lastRenderedEntities));
        lines.add(String.format("BlockEnt: %d culled / %d rendered",
                Stats.lastCulledBlockEntities, Stats.lastRenderedBlockEntities));
        lines.add(String.format("Particles: %d culled", Stats.lastCulledParticles));

        int x = 6;
        int y = 6;
        int lineHeight = font.fontHeight + 2;
        int padding = 4;

        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, font.getWidth(line));
        }

        int boxW = maxWidth + padding * 2;
        int boxH = lines.size() * lineHeight + padding * 2;

        context.fill(x, y, x + boxW, y + boxH, 0x90000000);

        for (int i = 0; i < lines.size(); i++) {
            int color = (i == 0) ? 0x55FF55 : 0xEEEEEE;
            context.drawText(font, Text.literal(lines.get(i)), x + padding, y + padding + i * lineHeight, color, true);
        }
    }
}
