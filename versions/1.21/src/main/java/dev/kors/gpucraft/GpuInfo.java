package dev.kors.gpucraft;

import org.lwjgl.opengl.GL11;

public final class GpuInfo {

    private static String renderer = null;
    private static String vendor = null;

    private GpuInfo() {}

    public static void init() {
        if (renderer == null) {
            try {
                renderer = GL11.glGetString(GL11.GL_RENDERER);
                vendor = GL11.glGetString(GL11.GL_VENDOR);
            } catch (Throwable t) {
                renderer = "Unknown";
                vendor = "Unknown";
            }
            if (renderer == null) renderer = "Unknown";
            if (vendor == null) vendor = "Unknown";
        }
    }

    public static String renderer() {
        init();
        return renderer;
    }

    public static String vendor() {
        init();
        return vendor;
    }
}
