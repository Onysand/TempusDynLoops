package org.onysand.mc.tempusdynloops.components;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.PolyLineMarker;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.utils.Database;
import org.onysand.mc.tempusdynloops.utils.LocUtils;
import org.onysand.mc.tempusdynloops.utils.PluginConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class LoopComponent {

    private final MiniMessage mm;
    private final PluginConfig config;
    private final Database database;
    private final HashMap<String, HashMap<Integer, Location>> processingSigns;
    private final HashMap<String, String> loopNames;

    private final MarkerAPI markerAPI;

    public LoopComponent(TempusDynLoops plugin) {
        this.mm = MiniMessage.miniMessage();
        this.config = plugin.getPluginConfig();
        this.database = plugin.getDatabase();
        this.processingSigns = new HashMap<>();
        this.loopNames = new HashMap<>();

        DynmapAPI dynmapAPI = plugin.getDynmapAPI();
        this.markerAPI = dynmapAPI.getMarkerAPI();
    }

    public void create(Player player, int rgbColor) {
        String playerName = player.getName();

        if (!isProcessing(playerName)) {
            player.sendMessage("Сначала установите первую точку, используя тег " + config.getLineTag());
            return;
        }

        if (!hasMinPoints(playerName)) {
            player.sendMessage("Установите как минимум одну дополнительную точку, использя тег " + config.getCornerTag());
            return;
        }

        int markerId = database.getMarkerId();
        String linesLabel = loopNames.getOrDefault(playerName, "null");
        UUID playerUUID = player.getUniqueId();
        String setName = playerName + "_markers";

        if (markerAPI.getMarkerSet(setName) == null) {
            markerAPI.createMarkerSet(setName, player.getName(), null, true);
        }

        PolyLineMarker linesMarker = createMarker(playerName, markerId, setName, linesLabel);
        if (linesMarker == null) {
            player.sendMessage("Ошибка при создании маркера");
            return;
        }

        linesMarker.setLineStyle(4, 1, rgbColor);

        String startLoc = LocUtils.stringLoc(getStartLocation(playerName));
        database.addSignMarker(startLoc, markerId, playerUUID);
        processingSigns.remove(playerName);
        player.sendMessage("Марер лупы успешно создан и бла бла бла.");

    }

    public void setLoopName(String playerName, String linesLabel) {
        loopNames.put(playerName, linesLabel);
    }

    public void addProcessingSign(Player player, Location signLocation, boolean isStart) {
        String playerName = player.getName();
        if (isStart) {
            processingSigns.remove(playerName);
        }
        HashMap<Integer, Location> locations = processingSigns.getOrDefault(playerName, new HashMap<>());
        locations.put(locations.size(), signLocation);
        processingSigns.put(playerName, locations);

        String startLoc = LocUtils.noWorldStringLoc(getStartLocation(playerName));
        player.sendMessage("Точка добавлена!\nДля добавления дополнительной точки, установите табличку с тегом " + config.getCornerTag() + "\n Для завершения перекрасьте начальную табличу по координатам " + startLoc);
    }

    public boolean isStartLocation(String playerName, Location location) {
        Location startLoc = processingSigns.get(playerName).get(0);
        String startLocString = LocUtils.stringLoc(startLoc);
        String currentLocString = LocUtils.stringLoc(location);
        return startLocString.equals(currentLocString);
    }

    private Location getStartLocation(String playerName) {
        return processingSigns.get(playerName).get(0);
    }

    private boolean isProcessing(String playerName) {
        return processingSigns.containsKey(playerName);
    }

    private boolean hasMinPoints(String playerName) {
        HashMap<Integer, Location> locations = processingSigns.get(playerName);
        if (locations == null) return false;
        return locations.size() >= 2;
    }

    private double[] getLocations(String playerName, LocationType type) {
        HashMap<Integer, Location> locations = processingSigns.get(playerName);
        ArrayList<Double> locationList = new ArrayList<>();
        for (Location location : locations.values()) {
            // Извлекаем координату в зависимости от типа
            double coordinate = 0;
            switch (type) {
                case X:
                    coordinate = location.getX();
                    break;
                case Y:
                    coordinate = location.getY();
                    break;
                case Z:
                    coordinate = location.getZ();
                    break;
            }
            locationList.add(coordinate);
        }
        double[] locationArray = new double[locationList.size()];
        for (int i = 0; i < locationArray.length; i++) {
            locationArray[i] = locationList.get(i);
        }
        return locationArray;
    }

    private PolyLineMarker createMarker(String playerName, int markerId, String setName, String linesLabel) {
        return markerAPI.getMarkerSet(setName).createPolyLineMarker(String.valueOf(markerId), linesLabel, false, config.getLinesWorldName(), getLocations(playerName, LocationType.X), getLocations(playerName, LocationType.Y), getLocations(playerName, LocationType.Z), true);
    }
}
