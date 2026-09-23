package dev.kors.gpucraft;

import org.lwjgl.opengl.GL11;

public final class GpuInfo {

    private static String renderer;
    private static String vendor;
    private static String version;

    private GpuInfo() {
    }

    
    public static void init() {
        if (renderer != null) return;
        renderer = safe(GL11.GL_RENDERER);
        vendor = safe(GL11.GL_VENDOR);
        version = safe(GL11.GL_VERSION);
        GpuCraft.LOGGER.info("GPU: {} ({}), OpenGL {}", renderer, vendor, version);
    }

    private static String safe(int name) {
        try {
            String value = GL11.glGetString(name);
            return value == null ? "Unknown" : value;
        } catch (RuntimeException e) {
            return "Unknown";
        }
    }

    public static String renderer() {
        return renderer == null ? "…" : renderer;
    }

    public static String vendor() {
        return vendor == null ? "…" : vendor;
    }
}
