package org.onysand.mc.tempusdynloops.utils;

import org.bukkit.Location;

import java.util.HashMap;

public class LocUtils {

    public static String noWorldStringLoc(Location location) {
        return String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static String stringLoc(Location location) {
        return String.format("%s;%d;%d;%d", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static HashMap<String, String> getLocationMap(String stringLoc) {
        String[] str = stringLoc.split(";");
        HashMap<String, String> locMap = new HashMap<>();
        locMap.put("worldName", str[0]);
        locMap.put("x", str[1]);
        locMap.put("y", str[2]);
        locMap.put("z", str[3]);

        return locMap;
    }
}
