package dev.kors.gpucraft;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class OcclusionCuller {

    private static final double MIN_DISTANCE = 8.0D;
    private static final long ENTRY_TTL_MS = 5_000L;

    private static final Map<Integer, Entry> CACHE = new HashMap<>();
    private static long lastPurge;

    private OcclusionCuller() {}

    private static final class Entry {
        boolean visible;
        long checkedAt;
    }

    public static boolean isVisible(Entity entity, double camX, double camY, double camZ) {
        Level level = entity.level();
        if (level == null) return true;

        AABB box = entity.getBoundingBox();
        double centerX = (box.minX + box.maxX) * 0.5D;
        double centerY = (box.minY + box.maxY) * 0.5D;
        double centerZ = (box.minZ + box.maxZ) * 0.5D;
        double dx = centerX - camX;
        double dy = centerY - camY;
        double dz = centerZ - camZ;
        if (dx * dx + dy * dy + dz * dz < MIN_DISTANCE * MIN_DISTANCE) return true;

        long now = System.currentTimeMillis();
        purge(now);

        Entry entry = CACHE.get(entity.getId());
        long interval = GpuCraft.config().occlusionIntervalMs;
        if (entry != null && now - entry.checkedAt < interval) {
            return entry.visible;
        }

        Vec3 camera = new Vec3(camX, camY, camZ);
        boolean visible = raycast(entity, level, camera, box);

        if (entry == null) {
            entry = new Entry();
            CACHE.put(entity.getId(), entry);
        }
        entry.visible = visible;
        entry.checkedAt = now;
        return visible;
    }

    public static boolean isVisible(Entity entity, Vec3 camera) {
        return isVisible(entity, camera.x, camera.y, camera.z);
    }

    private static boolean raycast(Entity entity, Level level, Vec3 camera, AABB rawBox) {
        AABB box = rawBox.inflate(0.15D);

        if (reaches(level, entity, camera, box.getCenter())) return true;
        if (reaches(level, entity, camera, new Vec3(box.minX, box.minY, box.minZ))) return true;
        if (reaches(level, entity, camera, new Vec3(box.maxX, box.minY, box.maxZ))) return true;
        if (reaches(level, entity, camera, new Vec3(box.minX, box.maxY, box.maxZ))) return true;
        return reaches(level, entity, camera, new Vec3(box.maxX, box.maxY, box.minZ));
    }

    private static boolean reaches(Level level, Entity entity, Vec3 from, Vec3 to) {
        BlockHitResult hit = level.clip(new ClipContext(
                from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
        return hit.getType() == HitResult.Type.MISS;
    }

    private static void purge(long now) {
        if (now - lastPurge < ENTRY_TTL_MS) return;
        lastPurge = now;
        Iterator<Entry> it = CACHE.values().iterator();
        while (it.hasNext()) {
            if (now - it.next().checkedAt > ENTRY_TTL_MS) {
                it.remove();
            }
        }
    }

    public static void reset() {
        CACHE.clear();
    }
}
