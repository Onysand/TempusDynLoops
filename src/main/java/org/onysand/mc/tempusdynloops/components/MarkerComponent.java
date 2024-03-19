package org.onysand.mc.tempusdynloops.components;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.utils.Database;
import org.onysand.mc.tempusdynloops.utils.LocUtils;
import org.onysand.mc.tempusdynloops.utils.PluginConfig;

import java.util.UUID;

public class MarkerComponent {

    private final MiniMessage mm;
    private final PluginConfig config;
    private final Database database;
    private final MarkerAPI markerAPI;

    public MarkerComponent(TempusDynLoops plugin) {
        this.mm = MiniMessage.miniMessage();
        this.config = plugin.getPluginConfig();
        this.database = plugin.getDatabase();

        DynmapAPI dynmapAPI = plugin.getDynmapAPI();
        this.markerAPI = dynmapAPI.getMarkerAPI();
    }

    public void create(Location location, Player player, String markerName) {
        int markerId = database.getMarkerId();
        String playerName = player.getName();
        UUID playerUUID = player.getUniqueId();
        String setName = playerName + "_markers";
        String stringLoc = LocUtils.stringLoc(location);

        if (markerAPI.getMarkerSet(setName) == null) {
            markerAPI.createMarkerSet(setName, player.getName(), null, true);
        }

        MarkerIcon icon = markerAPI.getMarkerIcon(markerName);
        Marker marker = createMarker(markerId, setName, markerName, location, icon);

        if (marker == null) {
            player.sendMessage("Ошибка при создании маркера");
            return;
        }

        database.addSignMarker(stringLoc, markerId, playerUUID);
        player.sendMessage(mm.deserialize(config.getCreatedMarker(), Placeholder.parsed("id", marker.getMarkerID())));
    }


    private Marker createMarker(int markerId, String setName, String markerName, Location location, MarkerIcon icon) {
        return markerAPI.getMarkerSet(setName).createMarker(String.valueOf(markerId), markerName, true, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), icon, true);
    }
}
