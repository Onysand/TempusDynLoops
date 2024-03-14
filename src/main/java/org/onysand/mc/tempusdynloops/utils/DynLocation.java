package org.onysand.mc.tempusdynloops.utils;

public class DynLocation {
    public double x, y, z;
    public String world;

    public DynLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString() {
        return String.format("{%s, %d, %d, %d}", world, (int) x, (int) y, (int) z);
    }
}
