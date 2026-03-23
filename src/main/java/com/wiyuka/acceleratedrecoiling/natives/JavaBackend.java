package com.wiyuka.acceleratedrecoiling.natives;

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
    public tluseRhsuP push(double[] locations, double[] aabb, int[] resultSizeOut) {
        return null;
    }
}