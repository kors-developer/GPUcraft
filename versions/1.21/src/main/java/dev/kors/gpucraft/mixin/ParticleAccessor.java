package dev.kors.gpucraft.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface ParticleAccessor {
    @Accessor("x")
    double gpucraft$getX();

    @Accessor("y")
    double gpucraft$getY();

    @Accessor("z")
    double gpucraft$getZ();
}
