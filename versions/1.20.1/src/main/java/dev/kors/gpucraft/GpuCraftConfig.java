package dev.kors.gpucraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class GpuCraftConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("gpucraft.json");

    public Preset preset = Preset.BALANCED;
    public int maxFps = 0;
    public int backgroundFps = 30;
    public int entityDistance = 64;
    public int blockEntityDistance = 48;
    public int shadowDistance = 32;
    public int maxItemEntities = 256;
    public int particleBudget = 48;
    public int particleDistance = 32;
    public boolean occlusionCulling = true;
    public boolean renderWeather = true;
    public boolean overlay = true;

    public static GpuCraftConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                GpuCraftConfig cfg = GSON.fromJson(reader, GpuCraftConfig.class);
                if (cfg != null) return cfg;
            } catch (Exception e) {
                GpuCraft.LOGGER.error("Failed to load GPUcraft config, using defaults", e);
            }
        }
        GpuCraftConfig cfg = new GpuCraftConfig();
        cfg.preset.apply(cfg);
        cfg.save();
        return cfg;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception e) {
            GpuCraft.LOGGER.error("Failed to save GPUcraft config", e);
        }
    }

    public void applyPreset(Preset p) {
        this.preset = p;
        p.apply(this);
        save();
    }
}
