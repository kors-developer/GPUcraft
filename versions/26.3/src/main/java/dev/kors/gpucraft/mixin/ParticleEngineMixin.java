package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.GpuCraft;
import dev.kors.gpucraft.GpuCraftConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void gpucraft$budget(Particle particle, CallbackInfo ci) {
        GpuCraftConfig config = GpuCraft.config();
        if (!config.particleCulling) return;

        if (config.particlesPerTick > 0
                && GpuCraft.STATS.particlesThisTick() >= config.particlesPerTick) {
            GpuCraft.STATS.particleCulled();
            ci.cancel();
            return;
        }

        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera != null && particle instanceof ParticleAccessor at) {
            double limit = config.particleDistance;
            double dx = at.gpucraft$x() - camera.getX();
            double dy = at.gpucraft$y() - camera.getEyeY();
            double dz = at.gpucraft$z() - camera.getZ();
            if (dx * dx + dy * dy + dz * dz > limit * limit) {
                GpuCraft.STATS.particleCulled();
                ci.cancel();
                return;
            }
        }

        GpuCraft.STATS.particleAdded();
    }
}
