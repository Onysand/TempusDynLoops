package org.onysand.mc.tempusdynloops.events;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.utils.Database;
import org.onysand.mc.tempusdynloops.utils.LocUtils;

import java.util.ArrayList;
import java.util.List;

public class SignExplode implements Listener {
    private final TempusDynLoops plugin;
    private final Database database;

    public SignExplode(TempusDynLoops plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    @EventHandler
    public void onSignExplode(EntityExplodeEvent e) {
        List<Block> blockList = new ArrayList<>();
        for (Block block : e.blockList()) {
            if (!block.getType().toString().contains("SIGN")) continue;
            String blockLocation = LocUtils.stringLoc(block.getLocation());
            if (database.getMarkerID(blockLocation) == null) continue;
            blockList.add(block);
        }
        e.blockList().removeAll(blockList);
        System.out.println(blockList.stream().map(it -> it.getType()).toList());
    }
}
