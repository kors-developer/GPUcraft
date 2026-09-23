package dev.kors.gpucraft.mixin;

import dev.kors.gpucraft.GpuCraft;
import dev.kors.gpucraft.GpuCraftConfig;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void gpucraft$cullByDistance(BlockEntity blockEntity, Vec3 cameraPos,
                                         CallbackInfoReturnable<Boolean> cir) {
        GpuCraftConfig config = GpuCraft.config();
        if (!config.entityCulling) return;

        BlockPos pos = blockEntity.getBlockPos();
        double limit = config.blockEntityDistance;
        double dx = (pos.getX() + 0.5D) - cameraPos.x;
        double dy = (pos.getY() + 0.5D) - cameraPos.y;
        double dz = (pos.getZ() + 0.5D) - cameraPos.z;
        boolean tooFar = (dx * dx + dy * dy + dz * dz) > limit * limit;

        GpuCraft.STATS.blockEntity(tooFar);
        if (tooFar) {
            cir.setReturnValue(false);
        }
    }
}
