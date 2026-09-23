package dev.kors.gpucraft;

import java.util.concurrent.atomic.AtomicLong;

public final class PipelineOptimizer {

    public static final AtomicLong redundantTextureBinds = new AtomicLong(0);
    public static final AtomicLong redundantStateSwitches = new AtomicLong(0);

    private static int currentTexture = -1;
    private static int depthTestState = -1;
    private static int depthMaskState = -1;
    private static int cullState = -1;
    private static int blendState = -1;

    private PipelineOptimizer() {}

    public static void resetFrameCache() {
        currentTexture = -1;
        depthTestState = -1;
        depthMaskState = -1;
        cullState = -1;
        blendState = -1;
    }

    public static boolean checkTexture(int textureId) {
        if (!GpuCraft.config().pipelineOptimization) return false;
        if (currentTexture == textureId && textureId != 0) {
            redundantTextureBinds.incrementAndGet();
            return true;
        }
        currentTexture = textureId;
        return false;
    }

    public static boolean checkDepthTest(boolean enable) {
        if (!GpuCraft.config().pipelineOptimization) return false;
        int target = enable ? 1 : 0;
        if (depthTestState == target) {
            redundantStateSwitches.incrementAndGet();
            return true;
        }
        depthTestState = target;
        return false;
    }

    public static boolean checkDepthMask(boolean mask) {
        if (!GpuCraft.config().pipelineOptimization) return false;
        int target = mask ? 1 : 0;
        if (depthMaskState == target) {
            redundantStateSwitches.incrementAndGet();
            return true;
        }
        depthMaskState = target;
        return false;
    }

    public static boolean checkCull(boolean enable) {
        if (!GpuCraft.config().pipelineOptimization) return false;
        int target = enable ? 1 : 0;
        if (cullState == target) {
            redundantStateSwitches.incrementAndGet();
            return true;
        }
        cullState = target;
        return false;
    }

    public static boolean checkBlend(boolean enable) {
        if (!GpuCraft.config().pipelineOptimization) return false;
        int target = enable ? 1 : 0;
        if (blendState == target) {
            redundantStateSwitches.incrementAndGet();
            return true;
        }
        blendState = target;
        return false;
    }
}
