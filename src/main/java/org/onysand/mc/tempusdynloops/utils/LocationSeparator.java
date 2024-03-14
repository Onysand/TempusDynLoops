package org.onysand.mc.tempusdynloops.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class LocationSeparator {
    public static Location stringToLocation(@NotNull String locString) {
        String[] locationSeparated = locString.split(";");
        World world = Bukkit.getWorld(locationSeparated[0]);
        double x = Double.parseDouble(locationSeparated[1]);
        double y = Double.parseDouble(locationSeparated[2]);
        double z = Double.parseDouble(locationSeparated[3]);

        return new Location(world, x, y, z);
    }

    public static String locationToString(@NotNull Location location) {
        return String.format("%s;%g;%g;%g", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }
}
