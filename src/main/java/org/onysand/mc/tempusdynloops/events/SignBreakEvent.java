package org.onysand.mc.tempusdynloops.events;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.components.LoopComponent;
import org.onysand.mc.tempusdynloops.components.MarkerComponent;
import org.onysand.mc.tempusdynloops.utils.Database;
import org.onysand.mc.tempusdynloops.utils.LocUtils;
import org.onysand.mc.tempusdynloops.utils.PluginConfig;

import java.util.UUID;

public class SignBreakEvent implements Listener {

    private final TempusDynLoops plugin;
    private final PluginConfig config;
    private final AsyncScheduler asyncScheduler;
    private final BukkitScheduler syncScheduler;
    private final Database database;
    private final MarkerComponent markerComponent;
    private final LoopComponent loopComponent;

    public SignBreakEvent(TempusDynLoops plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
        this.database = plugin.getDatabase();
        this.asyncScheduler = Bukkit.getAsyncScheduler();
        this.markerComponent = new MarkerComponent(plugin);
        this.loopComponent = new LoopComponent(plugin);
        this.syncScheduler = Bukkit.getScheduler();
    }

    @EventHandler
    public void onSignBreakEvent(BlockBreakEvent e) {

        Block block = e.getBlock();
        BlockState blockState = block.getState();
        if (!(blockState instanceof Sign)) return;

        Location blockLocation = block.getLocation();
        String stringLoc = LocUtils.stringLoc(blockLocation);
        Player player = e.getPlayer();
        String playerName = player.getName();
        UUID playerUUID = e.getPlayer().getUniqueId();


        asyncScheduler.runNow(plugin, asyncTask -> {
            if (!(database.isSignOwner(stringLoc, playerUUID))) return;
            Integer markerID = database.getMarkerID(stringLoc);
            if (markerID == null) return;

            boolean isLoop = database.isLoop(stringLoc);

            syncScheduler.runTask(plugin, syncTask -> {
                if (isLoop) {
                    new BlockBreakEvent(block, player);
                    loopComponent.remove(markerID, playerName);
                    return;
                }
                new BlockBreakEvent(block, player);
                markerComponent.remove(markerID, playerName);
            });
        });
    }
}
