package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.GpuCraft;
import dev.kors.gpucraft.Stats;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {

    @Shadow
    public Camera camera;

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void gpucraft$cullBlockEntity(E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (this.camera == null) return;

        int maxDist = GpuCraft.config().blockEntityDistance;
        BlockPos pos = blockEntity.getPos();
        Vec3d camPos = this.camera.getPos();

        double dx = pos.getX() + 0.5 - camPos.x;
        double dy = pos.getY() + 0.5 - camPos.y;
        double dz = pos.getZ() + 0.5 - camPos.z;

        if (dx * dx + dy * dy + dz * dz > maxDist * maxDist) {
            Stats.culledBlockEntities.incrementAndGet();
            ci.cancel();
            return;
        }

        Stats.renderedBlockEntities.incrementAndGet();
    }
}
