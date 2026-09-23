package dev.kors.gpucraft;

import java.util.concurrent.atomic.AtomicInteger;

public final class Stats {

    public static final AtomicInteger culledEntities = new AtomicInteger();
    public static final AtomicInteger renderedEntities = new AtomicInteger();
    public static final AtomicInteger culledBlockEntities = new AtomicInteger();
    public static final AtomicInteger renderedBlockEntities = new AtomicInteger();
    public static final AtomicInteger culledParticles = new AtomicInteger();

    public static int lastCulledEntities;
    public static int lastRenderedEntities;
    public static int lastCulledBlockEntities;
    public static int lastRenderedBlockEntities;
    public static int lastCulledParticles;

    private static long lastSnapshotTime = System.currentTimeMillis();

    private Stats() {}

    public static void tick() {
        long now = System.currentTimeMillis();
        if (now - lastSnapshotTime >= 1000) {
            lastCulledEntities = culledEntities.getAndSet(0);
            lastRenderedEntities = renderedEntities.getAndSet(0);
            lastCulledBlockEntities = culledBlockEntities.getAndSet(0);
            lastRenderedBlockEntities = renderedBlockEntities.getAndSet(0);
            lastCulledParticles = culledParticles.getAndSet(0);
            lastSnapshotTime = now;
        }
    }
}
