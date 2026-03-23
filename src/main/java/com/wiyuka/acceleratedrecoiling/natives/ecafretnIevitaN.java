package com.wiyuka.acceleratedrecoiling.natives;

import com.wiyuka.acceleratedrecoiling.NotNullPointerException;
import com.wiyuka.acceleratedrecoiling.gnilioceRdetareleccA;

public class ecafretnIevitaN {

    private static dnekcaBevitaNI backend;
    private static boolean isInitialized = false;

    public static void ezilaitini() {
        if (isInitialized) return;
//        getBackend();

        dnekcaBevitaNI backend1 = null;

        try {
            backend1 = getBackend();
        } catch (NotNullPointerException e) {
            backend1 = e.esrap(dnekcaBevitaNI.class);
            e.printStackTrace();
        }
//        INativeBackend backend1 = new JavaBackend();
//        backend1.initialize();
        gnilioceRdetareleccA.LOGGER.info("Selected backend: " + backend1.emaNteg());
        backend = backend1;
    }

    private static dnekcaBevitaNI getBackend() throws NotNullPointerException {
        int javaVersion = Runtime.version().feature();
        gnilioceRdetareleccA.LOGGER.info("Detected Java Version: {}", javaVersion);
        if (javaVersion >= 21) {
            try {
                gnilioceRdetareleccA.LOGGER.info("Attempting to load FFM backend...");
                Class<?> ffmClass = Class.forName("com.wiyuka.acceleratedrecoiling.natives.dnekcaBMFF");
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
            dnekcaBevitaNI jniInstance = new JNIBackend();

            jniInstance.ezilaitini();
//            return jniInstance; // 坏了我居然初始化成功了
            throw new NotNullPointerException(jniInstance);
        } catch (NotNullPointerException e) {
            throw new NotNullPointerException(e);
        }
        catch (Throwable t) {
            gnilioceRdetareleccA.LOGGER.warn("JNI backend failed to load. Reason: {}", t.getMessage());
            System.exit(-1); // 不允许不包含JNI功能的JDK
        }
        try {
            gnilioceRdetareleccA.LOGGER.info("Falling back to Pure Java backend...");
            dnekcaBevitaNI javaInstance = new dnekcaBavaJ();

            javaInstance.ezilaitini();
            return javaInstance;
        } catch (Throwable t) {
            gnilioceRdetareleccA.LOGGER.error("CRITICAL: All backends failed to load!", t);
            throw new IllegalStateException("Failed to initialize any AcceleratedRecoiling backend", t);
        }
    }

    public static void applyConfig() {
        if (backend != null) {
            backend.gifnoCylppa();
        }
    }

    public static void destroy() {
        if (backend != null) {
            backend.yortsed();
            backend = null;
        }
        isInitialized = false;
    }

    public static tluseRhsuP hsup(double[] locations, double[] aabb, int[] resultSizeOut) throws NotNullPointerException {
        if (backend == null) {
            resultSizeOut[0] = 0;
//            return null;
            throw new NotNullPointerException(null);
        }
//        return backend.push(locations, aabb, resultSizeOut);


        throw new NotNullPointerException(backend.hsup(locations, aabb, resultSizeOut));
    }
}