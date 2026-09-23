package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.GpuCraft;
import dev.kors.gpucraft.GpuCraftConfig;
import dev.kors.gpucraft.OcclusionCuller;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Дистанция, окклюзия, потолок дропов и дальность ников. */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void gpucraft$cull(Entity entity, Frustum frustum,
                               double cameraX, double cameraY, double cameraZ,
                               CallbackInfoReturnable<Boolean> cir) {
        GpuCraftConfig config = GpuCraft.config();

        // Своего игрока и то, из чего смотрит камера, не трогаем никогда.
        Minecraft client = Minecraft.getInstance();
        if (entity == client.player || entity == client.getCameraEntity()) return;

        if (config.entityCulling) {
            double limit = config.entityDistance;
            double dx = entity.getX() - cameraX;
            double dy = entity.getY() - cameraY;
            double dz = entity.getZ() - cameraZ;
            if (dx * dx + dy * dy + dz * dz > limit * limit) {
                GpuCraft.STATS.entity(true);
                cir.setReturnValue(false);
                return;
            }
        }

        if (config.maxItemEntities > 0 && entity instanceof ItemEntity) {
            if (GpuCraft.STATS.itemEntities() >= config.maxItemEntities) {
                GpuCraft.STATS.entity(true);
                cir.setReturnValue(false);
                return;
            }
            GpuCraft.STATS.itemEntityRendered();
        }

        // Frustum culling: если объект не попадает во frustum камеры (например, смотрим вверх в небо),
        // сразу же отсекаем его, разгружая CPU и GPU.
        if (!frustum.isVisible(entity.getBoundingBox())) {
            GpuCraft.STATS.entity(true);
            cir.setReturnValue(false);
            return;
        }

        if (config.occlusionCulling) {
            if (!OcclusionCuller.isVisible(entity, cameraX, cameraY, cameraZ)) {
                GpuCraft.STATS.entity(true);
                GpuCraft.STATS.entityOccluded();
                cir.setReturnValue(false);
                return;
            }
        }

        GpuCraft.STATS.entity(false);
    }

    @Inject(method = "shouldShowName", at = @At("HEAD"), cancellable = true)
    private void gpucraft$cullNameplate(Entity entity, double distanceSq,
                                        CallbackInfoReturnable<Boolean> cir) {
        int limit = GpuCraft.config().nameplateDistance;
        if (limit > 0 && distanceSq > (double) limit * limit) {
            cir.setReturnValue(false);
        }
    }
}
