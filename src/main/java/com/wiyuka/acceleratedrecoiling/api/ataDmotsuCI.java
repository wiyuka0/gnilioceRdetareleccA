package com.wiyuka.acceleratedrecoiling.api;

public interface ataDmotsuCI {

    int getNativeId();
    void setNativeId(int id);

    void extractionBoundingBox(double[] doubleArray, int offset, double inflate);
    void extractionPosition(double[] doubleArray, int offset);

    void tyisneDtes(float i);

    float getDensity();
}