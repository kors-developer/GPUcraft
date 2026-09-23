package dev.kors.gpucraft;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OcclusionCuller {

    private static final int CACHE_DURATION_MS = 250;
    private static final Map<Integer, Entry> CACHE = new ConcurrentHashMap<>();

    private record Entry(boolean visible, long time) {}

    private OcclusionCuller() {}

    public static boolean isVisible(BlockView world, double camX, double camY, double camZ, Entity entity) {
        if (world == null || entity == null) return true;

        long now = System.currentTimeMillis();
        int id = entity.getId();

        Entry entry = CACHE.get(id);
        if (entry != null && (now - entry.time()) < CACHE_DURATION_MS) {
            return entry.visible();
        }

        Box box = entity.getBoundingBox();
        Vec3d start = new Vec3d(camX, camY, camZ);
        Vec3d center = box.getCenter();

        boolean visible = checkRay(world, start, center, entity);
        if (!visible) {
            Vec3d top = new Vec3d(center.x, box.maxY, center.z);
            visible = checkRay(world, start, top, entity);
        }

        CACHE.put(id, new Entry(visible, now));
        return visible;
    }

    private static boolean checkRay(BlockView world, Vec3d start, Vec3d end, Entity entity) {
        RaycastContext ctx = new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.VISUAL,
                RaycastContext.FluidHandling.NONE,
                entity
        );
        BlockHitResult hit = world.raycast(ctx);
        return hit.getType() == HitResult.Type.MISS;
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
