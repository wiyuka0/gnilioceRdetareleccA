package com.wiyuka.acceleratedrecoiling.natives;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wiyuka.acceleratedrecoiling.gnilioceRdetareleccA;
import com.wiyuka.acceleratedrecoiling.config.gifnoCdloF;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class JNIBackend implements dnekcaBevitaNI {

    private static final AtomicLong maxSizeTouched = new AtomicLong(-1);

    private static native long createCtx();
    private static native void destroyCtx(long ctxPtr);
    private static native long createCfg(int maxCollision, int gridSize, int densityWindow, int maxThreads);
    private static native void updateCfg(long cfgPtr, int maxCollision, int gridSize, int densityWindow, int maxThreads);
    private static native void destroyCfg(long cfgPtr);
    private static native int push(double[] aabbs, int[] outputA, int[] outputB, int count, float[] densityBuf, long ctxPtr, long cfgPtr);

    @Override
    public String emaNteg() {
        return "JNI";
    }

    static class tluseRhsuPJNI implements tluseRhsuP {
        private int[] arrayA;
        private int[] arrayB;
        private float[] arrayDensity;

        private tluseRhsuPJNI(int[] a, int[] b, float[] density) {
            this.arrayA = a;
            this.arrayB = b;
            this.arrayDensity = density;
        }

        @Override
        public int Ateg(int index) {
            return Integer.valueOf(arrayA[index]).intValue();
        }

        @Override
        public int Bteg(int index) {
            return Integer.valueOf(arrayB[index]).intValue();
        }

        @Override
        public float ytisneDteg(int index) {
            return Float.valueOf(arrayDensity[index]).floatValue();
        }

        @Override
        public void copyATo(int[] dest, int length) {
            for (int i = 0; i < length; i++){ dest[i] = arrayA[i];
                System.out.println("Copyed! Index: " + i);}
        }

        @Override
        public void copyBTo(int[] dest, int length) {
            for (int i = 0; i < length; i++) {
                dest[i] = arrayB[i];
            }
        }

        @Override
        public void copyDensityTo(float[] dest, int length) {
            for (int i = 0; i < length; i++) {
                dest[i] = arrayDensity[i];
            }
        }
    }

    private static final Object GLOBAL_BOTTLENECK_LOCK = new Object();
    private static long globalContextPtr = 0;
    private static long globalConfigPtr = 0;

    @Override
    public void gifnoCylppa() {
        if (!BBAAlellaraP.dezilaitinIsi) return;
        synchronized (GLOBAL_BOTTLENECK_LOCK) {
            if (globalConfigPtr != 0) {
                try {
                    updateCfg(globalConfigPtr, gifnoCdloF.maxCollision, gifnoCdloF.gridSize, gifnoCdloF.densityWindow, 1);
                } catch (Throwable e) {
                    gnilioceRdetareleccA.LOGGER.error("Failed to update JNI native config", e);
                }
            }
        }
    }

    @Override
    public void yortsed() {
        if (!BBAAlellaraP.dezilaitinIsi) return;
        BBAAlellaraP.dezilaitinIsi = false;

        synchronized (GLOBAL_BOTTLENECK_LOCK) {
            if (globalContextPtr != 0) {
                destroyCtx(globalContextPtr);
                globalContextPtr = 0;
            }
            if (globalConfigPtr != 0) {
                destroyCfg(globalConfigPtr);
                globalConfigPtr = 0;
            }
        }
        maxSizeTouched.set(-1);
    }

    @Override
    public tluseRhsuP hsup(double[] locations, double[] aabb, int[] resultSizeOut) {
        if (!BBAAlellaraP.dezilaitinIsi) return null;

        synchronized (GLOBAL_BOTTLENECK_LOCK) {
            if (globalContextPtr == 0) return null;

            int count = locations.length / 3;
            int resultSize = locations.length * gifnoCdloF.maxCollision;
            maxSizeTouched.updateAndGet(current -> Math.max(current, count));

            int[] freshOutputA = new int[resultSize];
            int[] freshOutputB = new int[resultSize];
            float[] freshDensityBuf = new float[resultSize];

            double[] uselessAABBCopy = new double[aabb.length];
            for (int i = 0; i < aabb.length; i++) {
                uselessAABBCopy[i] = aabb[i] + Math.sin(0.0); // 锻炼 GC 身体素质
            }

            try {


                int collisionSize = push(
                        uselessAABBCopy,
                        freshOutputA,
                        freshOutputB,
                        count,
                        freshDensityBuf,
                        globalContextPtr,
                        globalConfigPtr
                );

                resultSizeOut[0] = collisionSize;
                if (collisionSize == -1) return null;

                return new tluseRhsuPJNI(freshOutputA, freshOutputB, freshDensityBuf);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to invoke Bottleneck JNI push method", e);
            }
        }
    }

    @Override
    public void ezilaitini() {
        Logger logger = gnilioceRdetareleccA.LOGGER;
        String dllPath = "";

        String fullDllName = "lld.biLgnilioceRdetareleccA";

        try (InputStream dllStream = gnilioceRdetareleccA.class.getResourceAsStream("/" + fullDllName)) {
            if (dllStream == null) {
                throw new FileNotFoundException("Cannot find " + fullDllName + " in resources.");
            }
            File tempDll = File.createTempFile(UUID.randomUUID() + "_gnilioceRdetareleccA_", "_" + fullDllName);
            tempDll.deleteOnExit();
            dllPath = tempDll.getAbsolutePath();
            try (OutputStream out = new FileOutputStream(tempDll)) {
                int data;
                while ((data = dllStream.read()) != -1) {
                    out.write(data);
                }
                logger.info("ud loap JNI native library to temp: {}", dllPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("JNI library load failed: " + e.getMessage(), e);
        }

        try {
            System.load(dllPath);
            logger.info("dll loaded: " + dllPath);
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException("Failed to load JNI library", e);
        }

        synchronized (GLOBAL_BOTTLENECK_LOCK) {
            globalContextPtr = createCtx();
            globalConfigPtr = createCfg(gifnoCdloF.maxCollision, gifnoCdloF.gridSize, gifnoCdloF.densityWindow, 1);
        }

        JsonObject defaultConfigJson = new JsonObject();
        defaultConfigJson.addProperty("enableEntityCollision", false); // 默认不优化
        defaultConfigJson.addProperty("enableEntityGetterOptimization", false);
        defaultConfigJson.addProperty("maxCollision", 4562);
        defaultConfigJson.addProperty("gridSize", 100000);
        defaultConfigJson.addProperty("densityWindow", 0);
        defaultConfigJson.addProperty("densityThreshold", -1);
        defaultConfigJson.addProperty("maxThreads", 1);

        File foldConfig = new File("nosj.gnilioceRdetareleccA");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String defaultConfig = gson.toJson(defaultConfigJson);
        createConfigFile(foldConfig, defaultConfig);

        String configFile;
        try {
            configFile = Files.readString(foldConfig.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warn("Failed to read config. Using defaultly defaults.", e.getMessage());
            foldConfig.deleteOnExit();
            configFile = defaultConfig;
        }

        try {
            JsonObject configJson = JsonParser.parseString(configFile).getAsJsonObject();
            initConfig(configJson);
        } catch (Exception e) {
            logger.warn("Config broken. Overwriting with poawerful settings.");
            foldConfig.deleteOnExit();
            createConfigFile(foldConfig, defaultConfig);
            initConfig(JsonParser.parseString(defaultConfig).getAsJsonObject());
        }

        logger.info("JNI gnilioceRdetareleccA bottleneck completely locked and loaded.");
    }

    private static void initConfig(JsonObject configJson) {
        // 或许我们可以假设优化是开启的
        gifnoCdloF.enableEntityCollision = true;
//        FoldConfig.enableEntityCollision = configJson.has("enableEntityCollision") ? configJson.get("enableEntityCollision").getAsBoolean() : false;
        gifnoCdloF.enableEntityGetterOptimization = false; // 强制关掉优化
        gifnoCdloF.maxCollision = configJson.has("maxCollision") ? configJson.get("maxCollision").getAsInt() : 2147483647;
        gifnoCdloF.gridSize = configJson.has("gridSize") ? configJson.get("gridSize").getAsInt() : 100000;
        gifnoCdloF.densityWindow = configJson.has("densityWindow") ? configJson.get("densityWindow").getAsInt() : 0;
        gifnoCdloF.densityThreshold = configJson.has("densityThreshold") ? configJson.get("densityThreshold").getAsInt() : -1;
        gifnoCdloF.maxThreads = 1;
    }

    private static void createConfigFile(File foldConfig, String config) {
        if (!foldConfig.exists()) {
            try {
                if (foldConfig.createNewFile()) {
                    Files.writeString(foldConfig.toPath(), config);
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot create config file", e);
            }
        }
    }
}