package org.onysand.mc.tempusdynloops.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.onysand.mc.tempusdynloops.TempusDynLoops;

import java.util.*;


public class MarkerUtils {
    private static final TempusDynLoops plugin = TempusDynLoops.getPlugin();
    private static final DynmapAPI dynmapAPI = plugin.getDynmapAPI();
    private static final MarkerAPI markerAPI = dynmapAPI.getMarkerAPI();
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final PluginConfig config = plugin.getPluginConfig();
    private static final HashMap<String, List<DynLocation>> locMap = new HashMap<String, List<DynLocation>>();

    public static void createMarker (Player player, String label, Location location) {
        String markerID = player.getName() + "_" + DatabaseManager.getCurrentID();
        String setName = player.getName() + "_markers";

        if (markerAPI.getMarkerSet(setName) == null){
            markerAPI.createMarkerSet(setName, player.getName(), null, true);
        }

        MarkerIcon icon = markerAPI.getMarkerIcon(label);
        Marker marker = markerAPI.getMarkerSet(setName).createMarker(markerID, label, true, location.getWorld().getName(), location.x(), location.y(), location.z(), icon, true);

        if (marker == null) {
            player.sendMessage("Ошибка при создании маркера");
            return;
        }

        player.sendMessage(mm.deserialize(config.getCreatedMarker(), Placeholder.parsed("id", marker.getMarkerID())));

        DatabaseManager.addOneToID();
        DatabaseManager.addSignMarker(LocationSeparator.locationToString(location), marker.getMarkerID());
    }

    public static Set<Marker> listMarkers (Player player) {
        String setName = player.getName() + "_markers";

        if (markerAPI.getMarkerSet(setName) == null) markerAPI.createMarkerSet(setName, player.getName(), null, true);

        return markerAPI.getMarkerSet(setName).getMarkers();
    }

    public static void addCorner (Player player, int id, DynLocation location) {
        String setName = player.getName() + "_markers";

        List<DynLocation> ll = locMap.get(setName);

        if (ll == null) {
            ll = new ArrayList<DynLocation>();
            locMap.put(setName, ll);
        }

        if (id >= ll.size() || id < 0) {
            ll.add(location);
        } else {
            ll.set(id, location);
        }

        player.sendMessage("Добавлен маркер ветки #" + ll.indexOf(location) + " на {" + location.x + "," + location.y + "," + location.z + "} в список");
    }

    public static void addCorner (Player player, DynLocation location) {
        String setName = player.getName() + "_markers";

        List<DynLocation> ll = locMap.get(setName);

        if (ll == null) {
            ll = new ArrayList<DynLocation>();
            locMap.put(setName, ll);
        }

        ll.add(location);
        player.sendMessage("Добавлен маркер ветки #" + ll.indexOf(location) + " на {" + location.x + "," + location.y + "," + location.z + "} в список");
    }

    public static void clearCorners(Player player) {
        String setName = player.getName() + "_markers";

        locMap.remove(setName);
    }

    public static List<DynLocation> listCorners (Player player) {
        String setName = player.getName() + "_markers";
        List<DynLocation> ll = locMap.get(setName);

        return ll;
    }

    public static void createLine (Player player, String label, Location location) {
        String setName = player.getName() + "_markers";
        List<DynLocation> ll = locMap.get(setName);
        String id = player.getName() + "_" + DatabaseManager.getCurrentID();

        if (ll == null) {
            player.sendMessage(config.getAtLeastTwoCorners());
            return;
        }

        MarkerSet set = markerAPI.getMarkerSet(setName);
        if (set == null) {
            markerAPI.createMarkerSet(setName, player.getName(), null, true);
            set = markerAPI.getMarkerSet(setName);
        }

        double[] xx = new double[ll.size()];
        double[] yy = new double[ll.size()];
        double[] zz = new double[ll.size()];
        for(int i = 0; i < ll.size(); i++) {
            DynLocation loc = ll.get(i);
            xx[i] = loc.x;
            yy[i] = loc.y;
            zz[i] = loc.z;
        }

        if (set.getPolyLineMarkers().stream().map(PolyLineMarker::getMarkerID).toList().contains(id)) {
            player.sendMessage("Ветка с таким ID уже создана");
            return;
        }

        PolyLineMarker line = set.createPolyLineMarker(id, label, true, ll.get(0).world, xx, yy, zz, true);
        if (line == null) {
            player.sendMessage("Ошибка при создании ветки");
            return;
        }

        player.sendMessage("Добавлена ветка id:'" + line.getMarkerID() + "' (" + line.getLabel() + ") to set '" + set.getMarkerSetID() + "'");
        locMap.remove(setName);

        DatabaseManager.addOneToID();
        DatabaseManager.addSignLine(LocationSeparator.locationToString(location), line.getMarkerID());
    }

    public static void removeLine (Location location, @NotNull Player player) {
        String setName = player.getName() + "_markers";
        String lineID = DatabaseManager.getValueByKey("signlines", LocationSeparator.locationToString(location));
        if (lineID != null) {
            PolyLineMarker lineMarker = markerAPI.getMarkerSet(setName).findPolyLineMarker(lineID);

            lineMarker.deleteMarker();
            DatabaseManager.deleteFromTable("signlines", LocationSeparator.locationToString(location));
        }
    }

    public static void removeMarker (Location location, Player player) {
        String setName = player.getName() + "_markers";
        String markerID = DatabaseManager.getValueByKey("signmarkers", LocationSeparator.locationToString(location));

        if (markerID != null) {
            markerAPI.getMarkerSet(setName).findMarker(markerID).deleteMarker();
            DatabaseManager.deleteFromTable("signmarkers", LocationSeparator.locationToString(location));
        }
    }

    public static void addSignOwner (Location location, Player player) {
        DatabaseManager.addSignOwner(LocationSeparator.locationToString(location), player.getUniqueId().toString());
    }

    public static @Nullable Player getOwner (Location location) {
        String uuid = DatabaseManager.getValueByKey("signowners", LocationSeparator.locationToString(location));
        if (uuid == null) return null;
        return Bukkit.getPlayer(UUID.fromString(uuid));
    }

    public static void removeSignOwner (Location location) {
        DatabaseManager.deleteFromTable("signowners", LocationSeparator.locationToString(location));
    }

    public static @Nullable PolyLineMarker getSignLine (Location location, Player player) {
        String setName = player.getName() + "_markers";
        String lineID = DatabaseManager.getValueByKey("signlines", LocationSeparator.locationToString(location));

        if (lineID == null) return null;
        return markerAPI.getMarkerSet(setName).findPolyLineMarker(lineID);
    }

    public static @Nullable Marker getSignMarker (Location location, Player player) {
        String setName = player.getName() + "_markers";
        String markerID = DatabaseManager.getValueByKey("signmarkers", LocationSeparator.locationToString(location));

        if (markerID == null) return null;
        return markerAPI.getMarkerSet(setName).findMarker(markerID);
    }
}