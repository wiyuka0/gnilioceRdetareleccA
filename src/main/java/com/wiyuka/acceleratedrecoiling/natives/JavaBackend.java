package com.wiyuka.acceleratedrecoiling.natives;

import java.util.Arrays;
import java.util.stream.IntStream;

public class JavaBackend implements INativeBackend {

    @Override
    public void initialize() {
        try {
            throw new ClassNotFoundException("This class is not found");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void applyConfig() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public PushResult push(double[] locations, double[] aabb, int[] resultSizeOut) {
        return null;
    }
}