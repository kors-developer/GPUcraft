package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.GpuCraft;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public abstract class WeatherEffectRendererMixin {

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void gpucraft$skipWeather(CallbackInfo ci) {
        if (!GpuCraft.config().renderWeather) {
            ci.cancel();
        }
    }
}
