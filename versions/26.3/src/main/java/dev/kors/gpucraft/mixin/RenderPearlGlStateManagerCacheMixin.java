package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.PipelineOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.mojang.renderpearl.backend.opengl.GlStateManager", remap = false)
public abstract class RenderPearlGlStateManagerCacheMixin {

    @Inject(method = "_bindTexture", at = @At("HEAD"), cancellable = true)
    private static void gpucraft$cacheTextureBind(int textureId, CallbackInfo ci) {
        if (PipelineOptimizer.checkTexture(textureId)) {
            ci.cancel();
        }
    }

    @Inject(method = "_enableDepthTest", at = @At("HEAD"), cancellable = true)
    private static void gpucraft$cacheEnableDepth(CallbackInfo ci) {
        if (PipelineOptimizer.checkDepthTest(true)) {
            ci.cancel();
        }
    }

    @Inject(method = "_disableDepthTest", at = @At("HEAD"), cancellable = true)
    private static void gpucraft$cacheDisableDepth(CallbackInfo ci) {
        if (PipelineOptimizer.checkDepthTest(false)) {
            ci.cancel();
        }
    }

    @Inject(method = "_depthMask", at = @At("HEAD"), cancellable = true)
    private static void gpucraft$cacheDepthMask(boolean mask, CallbackInfo ci) {
        if (PipelineOptimizer.checkDepthMask(mask)) {
            ci.cancel();
        }
    }

    @Inject(method = "_enableCull", at = @At("HEAD"), cancellable = true)
    private static void gpucraft$cacheEnableCull(CallbackInfo ci) {
        if (PipelineOptimizer.checkCull(true)) {
            ci.cancel();
        }
    }

    @Inject(method = "_disableCull", at = @At("HEAD"), cancellable = true)
    private static void gpucraft$cacheDisableCull(CallbackInfo ci) {
        if (PipelineOptimizer.checkCull(false)) {
            ci.cancel();
        }
    }
}
