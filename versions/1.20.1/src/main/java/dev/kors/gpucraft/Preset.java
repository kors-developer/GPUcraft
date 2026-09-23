package dev.kors.gpucraft;

public enum Preset {
    QUALITY("Quality") {
        @Override
        public void apply(GpuCraftConfig c) {
            c.entityDistance = 128;
            c.blockEntityDistance = 96;
            c.shadowDistance = 48;
            c.maxItemEntities = 0;
            c.particleBudget = 96;
            c.particleDistance = 64;
            c.occlusionCulling = false;
            c.renderWeather = true;
        }
    },
    BALANCED("Balanced") {
        @Override
        public void apply(GpuCraftConfig c) {
            c.entityDistance = 64;
            c.blockEntityDistance = 48;
            c.shadowDistance = 32;
            c.maxItemEntities = 256;
            c.particleBudget = 48;
            c.particleDistance = 32;
            c.occlusionCulling = true;
            c.renderWeather = true;
        }
    },
    PERFORMANCE("Performance") {
        @Override
        public void apply(GpuCraftConfig c) {
            c.entityDistance = 28;
            c.blockEntityDistance = 20;
            c.shadowDistance = 16;
            c.maxItemEntities = 96;
            c.particleBudget = 20;
            c.particleDistance = 20;
            c.occlusionCulling = true;
            c.renderWeather = false;
        }
    },
    CUSTOM("Custom") {
        @Override
        public void apply(GpuCraftConfig c) {
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
