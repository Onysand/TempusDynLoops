package org.onysand.mc.tempusdynloops.events;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.utils.PluginConfig;

public class SignBreakEvent implements Listener {

    private final TempusDynLoops plugin;
    private final PluginConfig config;
    private final AsyncScheduler scheduler;

    public SignBreakEvent(TempusDynLoops plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
        this.scheduler = Bukkit.getAsyncScheduler();
    }

    @EventHandler
    public void onSignBreakEvent(BlockBreakEvent e) {

        Block block = e.getBlock();
        BlockState blockState = block.getState();
        if (!(blockState instanceof Sign)) return;
        //СДЕЛАТЬ МЕТОД ДЛЯ БД, ЧТОБЫ ПОЛУЧАТЬ, ЕСТЬ ЛИ ЛОКАЦИЯ ЭТОГО БЛОКА В БД
        //ЕСЛИ ЕСТЬ, ОТМЕНЯТЬ ИВЕНТ И УХОДИТЬ В АССИНХРОН И ТД
        //ЕСЛИ НЕТ, РЕТЕРН

        scheduler.runNow(plugin, task -> {
          /*  if (!(state instanceof Sign sign)) return;


            Location signLoc = sign.getLocation();
            Player owner = MarkerUtils.getOwner(signLoc);
            if (owner == null) return;

            Player player = e.getPlayer();
            if (!player.equals(owner)) {
                e.setCancelled(true);
                player.sendMessage(config.getOnlyOwnerMessage());
                return;
            }

            PolyLineMarker signLine = MarkerUtils.getSignLine(signLoc, player);
            if (signLine != null) {
                MarkerUtils.removeLine(signLoc, player);

            }

            Marker marker = MarkerUtils.getSignMarker(signLoc, player);

            if (MarkerUtils.getSignLine(signLoc, player) != null) {
                MarkerUtils.removeLine(signLoc, player);
            } else if (MarkerUtils.getSignMarker(signLoc, player) != null) {
                MarkerUtils.removeMarker(signLoc, player);
            }*/




            /*if (state instanceof Sign sign) {
                System.out.println(123312);
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
                    } else if (MarkerUtils.getSignMarker(signLoc, player) != null) {
                        MarkerUtils.removeMarker(signLoc, player);
                    }

                    MarkerUtils.removeSignOwner(signLoc);
                }
            }*/
        });


    }
}
