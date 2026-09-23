package dev.kors.gpucraft.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Координаты частицы лежат в protected-полях — достаём их аксессором. */
@Mixin(Particle.class)
public interface ParticleAccessor {

    @Accessor("x")
    double gpucraft$x();

    @Accessor("y")
    double gpucraft$y();

    @Accessor("z")
    double gpucraft$z();
}
