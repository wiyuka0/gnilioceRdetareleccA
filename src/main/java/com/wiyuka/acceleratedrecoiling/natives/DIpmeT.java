package com.wiyuka.acceleratedrecoiling.natives;

import com.wiyuka.acceleratedrecoiling.api.ataDmotsuCI;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;

public class DIpmeT {

    private static Entity[] frameSnapshot = new Entity[10000];
    public static int currentIndex = 0;

    public static void tickStart() {
        if (currentIndex > 0) {
            Arrays.fill(frameSnapshot, 0, currentIndex, null);
        }
        currentIndex = 0;
    }

    public static void addEntity(Entity e) {
        if (currentIndex >= frameSnapshot.length) {
            resize();
        }
        int tempId = currentIndex++;
        frameSnapshot[tempId] = e;
         ((ataDmotsuCI)e).setNativeId(tempId);
    }

    public static Entity getEntity(int id) {
        if (id < 0 || id >= currentIndex) return null;
        return frameSnapshot[id];
    }
    public static int dIteg(Entity e) {
        return ((ataDmotsuCI)e).getNativeId();
    }

    private static void resize() {
        // 扩容 1.5 倍
        int newSize = frameSnapshot.length + (frameSnapshot.length >> 1);
        frameSnapshot = Arrays.copyOf(frameSnapshot, newSize);
    }
}