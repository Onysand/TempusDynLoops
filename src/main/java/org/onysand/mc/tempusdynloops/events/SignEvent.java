package org.onysand.mc.tempusdynloops.events;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.components.LoopComponent;
import org.onysand.mc.tempusdynloops.components.MarkerComponent;
import org.onysand.mc.tempusdynloops.utils.PluginConfig;

import java.util.List;

public class SignEvent implements Listener {
    private final TempusDynLoops plugin;
    private final PluginConfig config;
    private final AsyncScheduler asyncScheduler;
    private final BukkitScheduler syncShceduler;
    private final MarkerComponent markerComponent;
    private final LoopComponent loopComponent;

    public SignEvent(TempusDynLoops plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
        this.asyncScheduler = Bukkit.getAsyncScheduler();
        this.syncShceduler = Bukkit.getScheduler();

        this.markerComponent = new MarkerComponent(plugin);
        this.loopComponent = new LoopComponent(plugin);
    }

    @EventHandler
    public void onLoopInit(SignChangeEvent event) {
        asyncScheduler.runNow(plugin, task -> {
            //Проверяем права
            Player player = event.getPlayer();
            if (!player.hasPermission("tdl.createmarkers")) return;
            //Проверяем мир
            String playerWorld = player.getWorld().getName();
            String allowedWorld = config.getLinesWorldName();
            if (!playerWorld.equals(allowedWorld)) return;
            //Проверяем тег
            List<Component> signLines = event.lines();
            String markerTag = config.getLineTag();
            String markerName = null;

            for (int i = 0; i < signLines.size(); i++) {
                String lineText = ((TextComponent) signLines.get(i)).content();
                if (lineText.equals(markerTag)) {
                    if (i < signLines.size() - 1) {
                        markerName = ((TextComponent) signLines.get(i + 1)).content();
                        break;
                    } else {
                        player.sendMessage("Тег указан на последней строке, имя маркера не может быть прочитано.");
                        return;
                    }
                }
            }

            if (markerName == null || markerName.isEmpty()) {
                player.sendMessage("Название для создания лупы не найдено.");
                return;
            }
            if (markerName.equals(markerTag)) {
                player.sendMessage("Имя лупы не должно совпадать с тегом " + config.getLineTag());
                return;
            }

            Location signLocation = event.getBlock().getLocation();
            String playerName = player.getName();
            loopComponent.setLoopName(playerName, markerName);
            loopComponent.addProcessingSign(player, signLocation, true);
            waxSign(event.getBlock().getLocation());
        });
    }

    @EventHandler
    public void onPointSetup(SignChangeEvent event) {
        asyncScheduler.runNow(plugin, task -> {
            //Проверяем права
            Player player = event.getPlayer();
            if (!player.hasPermission("tdl.createmarkers")) return;
            //Проверяем мир
            String playerWorld = player.getWorld().getName();
            String allowedWorld = config.getLinesWorldName();
            if (!playerWorld.equals(allowedWorld)) return;
            //Проверяем тег
            List<Component> signLines = event.lines();
            String markerTag = config.getCornerTag();
            boolean hasTag = false;

            for (int i = 0; i < signLines.size(); i++) {
                String lineText = ((TextComponent) signLines.get(i)).content();
                if (lineText.equals(markerTag)) {
                    hasTag = true;
                    break;
                }
            }
            if (!hasTag) return;
            Location signLocation = event.getBlock().getLocation();
            loopComponent.addProcessingSign(player, signLocation, false);
            waxSign(signLocation);
        });
    }

    @EventHandler
    public void onMarkerSetup(SignChangeEvent event) {
        asyncScheduler.runNow(plugin, task -> {
            Player player = event.getPlayer();
            if (!player.hasPermission("tdl.createmarkers")) return;
            List<Component> signLines = event.lines();
            String markerTag = config.getMarkerTag();

            boolean hasTag = false;
            String markerName = null;
            String stringURL = null;

            for (int i = 0; i < signLines.size(); i++) {
                String lineText = ((TextComponent) signLines.get(i)).content();
                if (lineText.equals(markerTag)) {
                    hasTag = true;
                    if (i < signLines.size() - 2) {
                        markerName = ((TextComponent) signLines.get(i + 1)).content();
                        stringURL = ((TextComponent) signLines.get(i + 2)).content();
                        break;
                    } else {
                        player.sendMessage("Тег указан на последней строке, имя маркера не может быть прочитано.");
                        return;
                    }
                }
            }

            if (!hasTag) return;
            if (markerName.isEmpty()) {
                player.sendMessage("Название для создания маркера не найдено.");
                return;
            }
            if (markerName.equals(markerTag)) {
                player.sendMessage("Имя маркера не должно совпадать с тегом маркера!");
                return;
            }

            Location signLocation = event.getBlock().getLocation();
            markerComponent.create(signLocation, player, markerName, stringURL);
            waxSign(signLocation);
        });
    }

    @EventHandler
    public void onSignColor(PlayerInteractEvent event) {

        asyncScheduler.runNow(plugin, asyncTask -> {
            Player player = event.getPlayer();
            String playerName = player.getName();
            if (!player.hasPermission("tdl.createmarkers")) return;
            boolean isDye = player.getInventory().getItemInMainHand().getType().toString().contains("DYE");

            if (!isDye) return;
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            Block block = event.getClickedBlock();
            if (block == null) return;

            Material blockType = block.getType();
            if (!blockType.toString().contains("SIGN")) return;

            boolean isStartLoc = loopComponent.isStartLocation(playerName, block.getLocation());
            if (!isStartLoc) return;

            syncShceduler.runTask(plugin, bukkitTask -> {
                Sign sign = (Sign) block.getState();
                DyeColor color = sign.getTargetSide(player).getColor();

                asyncScheduler.runNow(plugin, scheduledTask -> {
                    if (color == null) return;
                    int rgbColor = color.getColor().asRGB();
                    loopComponent.create(player, rgbColor);
                });

            });
        });
    }

    private void waxSign(Location loc) {
        syncShceduler.runTask(plugin, waxTask -> {
            Sign sign = (Sign) loc.getBlock().getState();
            sign.setWaxed(true);
            sign.update();
        });
    }
}
