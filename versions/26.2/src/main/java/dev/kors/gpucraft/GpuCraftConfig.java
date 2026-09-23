package dev.kors.gpucraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GpuCraftConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH =
            FabricLoader.getInstance().getConfigDir().resolve("gpucraft.json");

    public Preset preset = Preset.BALANCED;

    public int maxFps = 0;
    public int backgroundFps = 30;

    public boolean entityCulling = true;
    public int entityDistance = 64;
    public int blockEntityDistance = 48;

    public boolean occlusionCulling = true;
    public int occlusionIntervalMs = 100;

    public int nameplateDistance = 48;
    public int maxItemEntities = 0;
    public boolean renderWeather = true;
    public int shadowDistance = 24;

    public boolean particleCulling = true;
    public int particleDistance = 48;
    public int particlesPerTick = 512;

    public boolean pipelineOptimization = true;
    public boolean overlay = false;

    public static GpuCraftConfig load() {
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
                GpuCraftConfig loaded = GSON.fromJson(reader, GpuCraftConfig.class);
                if (loaded != null) {
                    loaded.clamp();
                    return loaded;
                }
            } catch (IOException | RuntimeException e) {
                GpuCraft.LOGGER.warn("Failed to read gpucraft.json, using default settings", e);
            }
        }
        GpuCraftConfig fresh = new GpuCraftConfig();
        fresh.save();
        return fresh;
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            GpuCraft.LOGGER.warn("Failed to save gpucraft.json", e);
        }
    }

    public void applyPreset(Preset newPreset) {
        this.preset = newPreset;
        newPreset.apply(this);
        clamp();
        save();
    }

    /** Значения из файла могут быть какими угодно — приводим в разумные рамки. */
    public void clamp() {
        if (preset == null) preset = Preset.CUSTOM;
        maxFps = clampInt(maxFps, 0, 1000);
        backgroundFps = clampInt(backgroundFps, 0, 1000);
        entityDistance = clampInt(entityDistance, 8, 512);
        occlusionIntervalMs = clampInt(occlusionIntervalMs, 0, 2000);
        nameplateDistance = clampInt(nameplateDistance, 0, 256);
        shadowDistance = clampInt(shadowDistance, 0, 256);
        maxItemEntities = clampInt(maxItemEntities, 0, 10_000);
        blockEntityDistance = clampInt(blockEntityDistance, 8, 512);
        particleDistance = clampInt(particleDistance, 4, 512);
        particlesPerTick = clampInt(particlesPerTick, 0, 100_000);
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
