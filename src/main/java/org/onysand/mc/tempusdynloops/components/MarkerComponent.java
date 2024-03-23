package org.onysand.mc.tempusdynloops.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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

    public void create(Location location, Player player, String markerName, String stringURL) {
        int markerId = database.getNextID();
        String playerName = player.getName();
        UUID playerUUID = player.getUniqueId();
        String setName = playerName + "_markers";
        String stringLoc = LocUtils.stringLoc(location);

        if (markerAPI.getMarkerSet(setName) == null) {
            markerAPI.createMarkerSet(setName, player.getName(), null, true);
        }


        boolean addIcon = addIcon(markerId, markerName, stringURL);
        if (!addIcon) {
            TextComponent component = Component.text("Третяя строка должна содержать следующую часть ссылки: \n").append(Component.text("https://imgur.com/")).append(Component.text("f5u8L8A").color(TextColor.color(16, 177, 201)));
            player.sendMessage(component);
            return;
        }
        Marker marker = createMarker(markerId, setName, markerName, location);

        if (marker == null) {
            player.sendMessage("Ошибка при создании маркера");
            return;
        }

        database.addSignMarker(stringLoc, playerUUID, false);
        player.sendMessage(mm.deserialize(config.getCreatedMarker(), Placeholder.parsed("id", marker.getMarkerID())));
    }

    public boolean remove(int markerID, String playerName) {
        Marker marker = markerAPI.getMarkerSet(playerName + "_markers").findMarker(String.valueOf(markerID));
        if (marker == null) {
            return false;
        }
        marker.deleteMarker();
        database.removeMarkerByID(markerID);
        return true;
    }

    private boolean addIcon(int markerId, String markerName, String streamURL) {
        String url = "https://proxy.duckduckgo.com/iu/?u=https://i.imgur.com/" + streamURL + "m.png";
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDefaultUseCaches(false);
            urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            urlConnection.addRequestProperty("Pragma", "no-cache");

            InputStream in = urlConnection.getInputStream();
            BufferedImage originalImage = ImageIO.read(in);
            in.close();

            Image resizedImage = originalImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            BufferedImage outputImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(resizedImage, 0, 0, null);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(outputImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            InputStream is = new ByteArrayInputStream(imageBytes);

            // Использование обработанного изображения в markerAPI
            markerAPI.createMarkerIcon(String.valueOf(markerId), markerName, is);

            System.out.println("Обработанное изображение: " + url);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    private Marker createMarker(int markerId, String setName, String markerName, Location location) {
        MarkerIcon icon = markerAPI.getMarkerIcon(String.valueOf(markerId));
        return markerAPI.getMarkerSet(setName).createMarker(String.valueOf(markerId), markerName, true, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), icon, true);
    }
}
