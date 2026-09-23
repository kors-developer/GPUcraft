package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.GpuCraft;
import dev.kors.gpucraft.GpuCraftConfig;
import dev.kors.gpucraft.OcclusionCuller;
import dev.kors.gpucraft.Stats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void gpucraft$cullEntity(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        GpuCraftConfig config = GpuCraft.config();

        // 1. Distance culling
        double maxDist = (entity instanceof PlayerEntity) ? config.entityDistance * 1.5 : config.entityDistance;
        double distSq = x * x + y * y + z * z;
        if (distSq > maxDist * maxDist) {
            Stats.culledEntities.incrementAndGet();
            cir.setReturnValue(false);
            return;
        }

        // 2. Item drop limiter
        if (entity instanceof ItemEntity && config.maxItemEntities > 0) {
            if (entity.getId() % 1000 > config.maxItemEntities) {
                Stats.culledEntities.incrementAndGet();
                cir.setReturnValue(false);
                return;
            }
        }

        // 3. Frustum culling: if outside camera view (e.g. looking up at sky), cull immediately!
        if (!frustum.isVisible(entity.getBoundingBox())) {
            Stats.culledEntities.incrementAndGet();
            cir.setReturnValue(false);
            return;
        }

        // 4. Occlusion culling: cull mobs hidden behind solid blocks
        if (config.occlusionCulling && !(entity instanceof PlayerEntity)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null && client.gameRenderer != null && client.gameRenderer.getCamera() != null) {
                var camPos = client.gameRenderer.getCamera().getPos();
                if (!OcclusionCuller.isVisible(client.world, camPos.x, camPos.y, camPos.z, entity)) {
                    Stats.culledEntities.incrementAndGet();
                    cir.setReturnValue(false);
                    return;
                }
            }
        }

        Stats.renderedEntities.incrementAndGet();
    }
}
