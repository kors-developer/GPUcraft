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

    /** 0 — без ограничения. */
    public int maxFps = 0;
    /** Лимит FPS, когда окно не в фокусе. 0 — не ограничивать отдельно. */
    public int backgroundFps = 30;

    public boolean entityCulling = true;
    /** Дистанция отрисовки сущностей в блоках. */
    public int entityDistance = 64;
    /** Дистанция отрисовки блок-энтити (сундуки, таблички, баннеры). */
    public int blockEntityDistance = 48;

    /** Не рисовать сущностей, полностью закрытых блоками. */
    public boolean occlusionCulling = true;
    /** Как часто перепроверять видимость одной сущности, мс. */
    public int occlusionIntervalMs = 100;

    /** Дистанция показа ников над головой. 0 — как в ванили. */
    public int nameplateDistance = 48;
    /** Потолок одновременно рисуемых дропов. 0 — без лимита. */
    public int maxItemEntities = 0;
    /** Рисовать дождь и снег. */
    public boolean renderWeather = true;
    /** Дистанция отрисовки теней под сущностями в блоках. 0 — как в ванили. */
    public int shadowDistance = 24;

    public boolean particleCulling = true;
    /** Дистанция отрисовки частиц в блоках. */
    public int particleDistance = 48;
    /** Максимум новых частиц за тик. 0 — без лимита. */
    public int particlesPerTick = 512;

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
