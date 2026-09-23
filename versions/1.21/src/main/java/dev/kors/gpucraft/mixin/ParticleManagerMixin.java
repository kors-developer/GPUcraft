package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.GpuCraft;
import dev.kors.gpucraft.GpuCraftConfig;
import dev.kors.gpucraft.Stats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    private static int spawnedThisTick = 0;
    private static long lastTickTime = 0;

    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
    private void gpucraft$budgetParticle(Particle particle, CallbackInfo ci) {
        GpuCraftConfig config = GpuCraft.config();

        long now = System.currentTimeMillis();
        if (now - lastTickTime >= 50) {
            spawnedThisTick = 0;
            lastTickTime = now;
        }

        if (config.particleBudget > 0 && spawnedThisTick >= config.particleBudget) {
            Stats.culledParticles.incrementAndGet();
            ci.cancel();
            return;
        }

        if (config.particleDistance > 0 && particle instanceof ParticleAccessor acc) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.gameRenderer != null) {
                Camera cam = client.gameRenderer.getCamera();
                if (cam != null) {
                    Vec3d camPos = cam.getPos();
                    double dx = acc.gpucraft$getX() - camPos.x;
                    double dy = acc.gpucraft$getY() - camPos.y;
                    double dz = acc.gpucraft$getZ() - camPos.z;
                    double maxD = config.particleDistance;
                    if (dx * dx + dy * dy + dz * dz > maxD * maxD) {
                        Stats.culledParticles.incrementAndGet();
                        ci.cancel();
                        return;
                    }
                }
            }
        }

        spawnedThisTick++;
    }
}
