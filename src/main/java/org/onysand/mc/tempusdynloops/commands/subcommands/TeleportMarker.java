package org.onysand.mc.tempusdynloops.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.utils.Database;


public class TeleportMarker implements SubCommand{
    private final TempusDynLoops plugin;
    private final Database database;

    public TeleportMarker(TempusDynLoops plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    @Override
    public String getName() {
        return "tpmarker";
    }

    @Override
    public String getDescription() {
        return "Телепортироваться к маркеру по его ID";
    }

    @Override
    public String getSyntax() {
        return "/tdl tpmarker 1";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player && player.hasPermission("tdl.tpmarker")) {
            if (args.length == 1) {
                sender.sendMessage(getSyntax());
                return;
            }

            player.teleport(database.getMarkerLoc(args[1]));
        }
    }
}
