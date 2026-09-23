package dev.kors.gpucraft;

/** Готовые наборы настроек. CUSTOM ничего не трогает — значения задаёт игрок. */
public enum Preset {
    QUALITY("Quality") {
        @Override
        public void apply(GpuCraftConfig c) {
            c.entityCulling = true;
            c.entityDistance = 128;
            c.blockEntityDistance = 96;
            c.particleDistance = 96;
            c.particlesPerTick = 0;
            c.occlusionCulling = false;
            c.occlusionIntervalMs = 200;
            c.nameplateDistance = 0;
            c.maxItemEntities = 0;
            c.renderWeather = true;
            c.shadowDistance = 32;
        }
    },
    BALANCED("Balanced") {
        @Override
        public void apply(GpuCraftConfig c) {
            c.entityCulling = true;
            c.entityDistance = 64;
            c.blockEntityDistance = 48;
            c.particleDistance = 48;
            c.particlesPerTick = 512;
            c.occlusionCulling = true;
            c.occlusionIntervalMs = 150;
            c.nameplateDistance = 64;
            c.maxItemEntities = 0;
            c.renderWeather = true;
            c.shadowDistance = 24;
        }
    },
    PERFORMANCE("Performance") {
        @Override
        public void apply(GpuCraftConfig c) {
            c.entityCulling = true;
            c.entityDistance = 28;
            c.blockEntityDistance = 20;
            c.particleDistance = 20;
            c.particlesPerTick = 96;
            c.occlusionCulling = true;
            c.occlusionIntervalMs = 80;
            c.nameplateDistance = 16;
            c.maxItemEntities = 96;
            c.renderWeather = false;
            c.shadowDistance = 12;
        }
    },
    CUSTOM("Custom") {
        @Override
        public void apply(GpuCraftConfig c) {
            // intentionally empty
        }
    };

    private final String label;

    Preset(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public abstract void apply(GpuCraftConfig config);
}
