package dev.kors.gpucraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GpuCraft {

    public static final String MOD_ID = "gpucraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("GPUcraft");
    public static final Stats STATS = new Stats();

    private static GpuCraftConfig config = new GpuCraftConfig();

    private GpuCraft() {}

    public static GpuCraftConfig config() {
        return config;
    }

    static void setConfig(GpuCraftConfig loaded) {
        config = loaded;
    }
}
