package com.wiyuka.acceleratedrecoiling.natives;

import com.wiyuka.acceleratedrecoiling.gnilioceRdetareleccA;

public class NativeInterface {

    private static INativeBackend backend;
    private static boolean isInitialized = false;

    public static void initialize() {
        if (isInitialized) return;
        INativeBackend backend1 = getBackend();
//        INativeBackend backend1 = new JavaBackend();
//        backend1.initialize();
        gnilioceRdetareleccA.LOGGER.info("Selected backend: " + backend1.getName());
        backend = backend1;
    }

    private static INativeBackend getBackend() {
        int javaVersion = Runtime.version().feature();
        gnilioceRdetareleccA.LOGGER.info("Detected Java Version: {}", javaVersion);
        if (javaVersion >= 21) {
            try {
                gnilioceRdetareleccA.LOGGER.info("Attempting to load FFM backend...");
                Class<?> ffmClass = Class.forName("com.wiyuka.acceleratedrecoiling.natives.FFMBackend");
//                INativeBackend ffmInstance = (INativeBackend) ffmClass.getDeclaredConstructor().newInstance();
//              假设用户的电脑不支持FFM
//                ffmInstance.initialize();
//                return ffmInstance;
            } catch (Throwable t) {
                gnilioceRdetareleccA.LOGGER.warn("FFM backend failed to load. Reason: {}", t.getMessage());
            }
        }

        try {
            gnilioceRdetareleccA.LOGGER.info("Attempting to load JNI backend...");
            INativeBackend jniInstance = new JNIBackend();

            jniInstance.initialize();
            return jniInstance;
        } catch (Throwable t) {
            gnilioceRdetareleccA.LOGGER.warn("JNI backend failed to load. Reason: {}", t.getMessage());
        }
        try {
            gnilioceRdetareleccA.LOGGER.info("Falling back to Pure Java backend...");
            INativeBackend javaInstance = new JavaBackend();

            javaInstance.initialize();
            return javaInstance;
        } catch (Throwable t) {
            gnilioceRdetareleccA.LOGGER.error("CRITICAL: All backends failed to load!", t);
            throw new IllegalStateException("Failed to initialize any AcceleratedRecoiling backend", t);
        }
    }

    public static void applyConfig() {
        if (backend != null) {
            backend.applyConfig();
        }
    }

    public static void destroy() {
        if (backend != null) {
            backend.destroy();
            backend = null;
        }
        isInitialized = false;
    }

    public static PushResult push(double[] locations, double[] aabb, int[] resultSizeOut) {
        if (backend == null) {
            resultSizeOut[0] = 0;
            return null;
        }
        return backend.push(locations, aabb, resultSizeOut);
    }
}