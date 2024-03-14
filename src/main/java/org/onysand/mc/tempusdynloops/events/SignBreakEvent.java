package org.onysand.mc.tempusdynloops.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.utils.MarkerUtils;
import org.onysand.mc.tempusdynloops.utils.PluginConfig;

public class SignBreakEvent implements Listener {
    private final PluginConfig config;

    public SignBreakEvent(TempusDynLoops plugin) {
        this.config = plugin.getPluginConfig();
    }

    @EventHandler
    public void onSignBreakEvent(BlockBreakEvent e) {
        Block block = e.getBlock();

        if (block.getState() instanceof Sign sign) {
            Player player = e.getPlayer();
            Location signLoc = sign.getLocation();
            Player owner = MarkerUtils.getOwner(signLoc);

            if (owner != null) {
                if (player != owner) {
                    e.setCancelled(true);
                    player.sendMessage(config.getOnlyOwnerMessage());
                }
            }
            if (player == owner) {
                if (MarkerUtils.getSignLine(signLoc, player) != null) {
                    MarkerUtils.removeLine(signLoc, player);
                }   else if (MarkerUtils.getSignMarker(signLoc, player) != null){
                    MarkerUtils.removeMarker(signLoc, player);
                }

                MarkerUtils.removeSignOwner(signLoc);
            }
        }
    }
}
