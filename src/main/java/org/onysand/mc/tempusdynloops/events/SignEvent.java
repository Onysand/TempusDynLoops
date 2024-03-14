package org.onysand.mc.tempusdynloops.events;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.dynmap.markers.PolyLineMarker;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.utils.DynLocation;
import org.onysand.mc.tempusdynloops.utils.MarkerUtils;
import org.onysand.mc.tempusdynloops.utils.PluginConfig;

public class SignEvent implements Listener {
    private final TempusDynLoops plugin;
    private final PluginConfig config;
    private final BukkitScheduler scheduler;

    public SignEvent (TempusDynLoops plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
        this.scheduler = Bukkit.getScheduler();
    }

    @EventHandler
    public void onSignChangeEvent (SignChangeEvent e) {
        Player player = e.getPlayer();

        scheduler.runTaskLater(plugin, bukkitTask -> {
            if (player.hasPermission("tdl.createmarkers")) {
                Sign sign = (Sign) e.getBlock().getState();
                String[] lines = sign.getTargetSide(player).getLines();
                Location signLoc = e.getBlock().getLocation();

                if (lines[0].equals(config.getMarkerTag())) {
                    if (sign.getLocation().getWorld().getName().equals(config.getMarkersWorldName())) {
                        MarkerUtils.createMarker(player, lines[2], e.getBlock().getLocation());

                        if (MarkerUtils.getOwner(signLoc) == null) {
                            MarkerUtils.addSignOwner(signLoc, player);
                        }
                        sign.setEditable(false);
                    } else {
                        player.sendMessage(config.getMarkerWorldMessage());
                    }
                }

                if (lines[0].equals(config.getCornerTag())) {
                    String world = e.getBlock().getWorld().getName();
                    DynLocation loc = new DynLocation(world, signLoc.x(), signLoc.y(), signLoc.z());

                    if (signLoc.getWorld().getName().equals(config.getLinesWorldName())) {
                        if (!lines[1].isEmpty() && lines[1].chars().allMatch(Character::isDigit)) {
                            int id = Integer.parseInt(lines[1]);
                            MarkerUtils.addCorner(player, id, loc);

                            if (MarkerUtils.getOwner(signLoc) == null) {
                                MarkerUtils.addSignOwner(signLoc, player);
                            }
                            sign.setEditable(false);
                        } else {
                            MarkerUtils.addCorner(player, loc);
                        }
                    } else {
                        player.sendMessage(config.getLinesWorldMessage());
                    }
                }

                if (lines[0].equals(config.getLineTag())) {
                    if (signLoc.getWorld().getName().equals(config.getLinesWorldName())) {
                        if (!lines[1].isEmpty()) {
                            MarkerUtils.createLine(player, lines[1], signLoc);

                            if (MarkerUtils.getOwner(signLoc) == null) {
                                MarkerUtils.addSignOwner(signLoc, player);
                            }
                            sign.setEditable(false);
                        } else {
                            player.sendMessage(config.getCreatingLineEmptyLinesMessage());
                        }
                    } else {
                        player.sendMessage(config.getLinesWorldMessage());
                    }
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onSignColor (PlayerInteractEvent e) {
        Block block = e.getClickedBlock();

        if (block != null && block.getState() instanceof Sign sign) {
            Player player = e.getPlayer();
            Player owner = MarkerUtils.getOwner(sign.getLocation());
            boolean action = e.getAction() == Action.RIGHT_CLICK_BLOCK;
            boolean isDye = player.getInventory().getItemInMainHand().getType().toString().contains("DYE");
            if (owner != null && action && isDye) {
                if (player == owner) {
                    PolyLineMarker line = MarkerUtils.getSignLine(sign.getLocation(), owner);

                    if (line != null) {
                        scheduler.runTaskLater(plugin, bukkitTask1 -> {
                            Sign signColored = (Sign) e.getClickedBlock().getState();
                            DyeColor color = signColored.getTargetSide(player).getColor();

                            if (color != null) {
                                line.setLineStyle(4, 1, color.getColor().asRGB());
                            }
                        }, 1L);
                    }
                } else e.setCancelled(true);
            }
        }
    }
}
