package dev.kors.gpucraft;

/** Счётчики для оверлея. Обновляются из рендер-потока, читаются оттуда же. */
public final class Stats {

    private int entitiesSeen;
    private int entitiesCulled;
    private int blockEntitiesSeen;
    private int blockEntitiesCulled;
    private int entitiesOccluded;
    private int itemEntities;
    private int particlesCulled;
    private int particlesThisTick;

    private int lastEntitiesSeen;
    private int lastEntitiesCulled;
    private int lastBlockEntitiesSeen;
    private int lastBlockEntitiesCulled;
    private int lastEntitiesOccluded;
    private int lastParticlesCulled;

    public void entity(boolean culled) {
        entitiesSeen++;
        if (culled) entitiesCulled++;
    }

    public void blockEntity(boolean culled) {
        blockEntitiesSeen++;
        if (culled) blockEntitiesCulled++;
    }

    public void entityOccluded() {
        entitiesOccluded++;
    }

    /** @return сколько дропов уже отрисовано в этом кадре */
    public int itemEntities() {
        return itemEntities;
    }

    public void itemEntityRendered() {
        itemEntities++;
    }

    public void particleCulled() {
        particlesCulled++;
    }

    /** @return сколько частиц уже добавлено в этом тике */
    public int particlesThisTick() {
        return particlesThisTick;
    }

    public void particleAdded() {
        particlesThisTick++;
    }

    public void onClientTick() {
        particlesThisTick = 0;
    }

    /** Вызывается раз в кадр: замораживает значения для показа и обнуляет накопители. */
    public void endFrame() {
        lastEntitiesSeen = entitiesSeen;
        lastEntitiesCulled = entitiesCulled;
        lastBlockEntitiesSeen = blockEntitiesSeen;
        lastBlockEntitiesCulled = blockEntitiesCulled;
        lastEntitiesOccluded = entitiesOccluded;
        lastParticlesCulled = particlesCulled;

        entitiesSeen = 0;
        entitiesCulled = 0;
        blockEntitiesSeen = 0;
        blockEntitiesCulled = 0;
        entitiesOccluded = 0;
        itemEntities = 0;
        particlesCulled = 0;
    }

    public int entitiesSeen() {
        return lastEntitiesSeen;
    }

    public int entitiesCulled() {
        return lastEntitiesCulled;
    }

    public int blockEntitiesSeen() {
        return lastBlockEntitiesSeen;
    }

    public int blockEntitiesCulled() {
        return lastBlockEntitiesCulled;
    }

    public int entitiesOccluded() {
        return lastEntitiesOccluded;
    }

    public int particlesCulled() {
        return lastParticlesCulled;
    }
}
