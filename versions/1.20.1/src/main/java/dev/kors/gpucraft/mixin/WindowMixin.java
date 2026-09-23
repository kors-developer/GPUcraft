package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.GpuCraft;
import dev.kors.gpucraft.GpuCraftConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
public abstract class WindowMixin {

    @Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
    private void gpucraft$applyLimit(CallbackInfoReturnable<Integer> cir) {
        GpuCraftConfig config = GpuCraft.config();
        int limit = cir.getReturnValue();

        MinecraftClient client = MinecraftClient.getInstance();
        boolean focused = client == null || client.isWindowFocused();

        if (!focused && config.backgroundFps > 0) {
            limit = Math.min(limit, config.backgroundFps);
        } else if (config.maxFps > 0) {
            limit = Math.min(limit, config.maxFps);
        }

        cir.setReturnValue(limit);
    }
}
