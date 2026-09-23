package dev.kors.gpucraft.mixin;

import com.mojang.blaze3d.platform.FramerateLimitTracker;
import dev.kors.gpucraft.GpuCraft;
import dev.kors.gpucraft.GpuCraftConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Свой лимит FPS поверх ванильного. Берём минимум из двух, чтобы не ломать
 * ванильные троттлинги (свёрнутое окно, AFK) — они всё так же работают.
 */
@Mixin(FramerateLimitTracker.class)
public abstract class FramerateLimitTrackerMixin {

    @Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
    private void gpucraft$applyLimit(CallbackInfoReturnable<Integer> cir) {
        GpuCraftConfig config = GpuCraft.config();
        int limit = cir.getReturnValue();

        Minecraft client = Minecraft.getInstance();
        boolean focused = client.getWindow() == null || client.isWindowActive();

        if (!focused && config.backgroundFps > 0) {
            limit = Math.min(limit, config.backgroundFps);
        } else if (config.maxFps > 0) {
            limit = Math.min(limit, config.maxFps);
        }

        cir.setReturnValue(limit);
    }
}
