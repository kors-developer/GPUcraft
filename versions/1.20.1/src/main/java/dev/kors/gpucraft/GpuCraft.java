package dev.kors.gpucraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GpuCraft {
    public static final String MOD_ID = "gpucraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("GPUcraft");

    private static GpuCraftConfig config = GpuCraftConfig.load();

    private GpuCraft() {}

    public static GpuCraftConfig config() {
        return config;
    }

    public static void setConfig(GpuCraftConfig newConfig) {
        config = newConfig;
    }
}
