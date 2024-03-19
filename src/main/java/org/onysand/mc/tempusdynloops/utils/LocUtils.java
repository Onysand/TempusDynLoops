package org.onysand.mc.tempusdynloops.utils;

import org.bukkit.Location;

public class LocUtils {

    public static String noWorldStringLoc(Location location) {
        return String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static String stringLoc(Location location) {
        return String.format("%s;%d;%d;%d", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
