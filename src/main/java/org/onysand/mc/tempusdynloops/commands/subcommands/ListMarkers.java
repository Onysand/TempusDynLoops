package org.onysand.mc.tempusdynloops.commands.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.dynmap.markers.Marker;
import org.onysand.mc.tempusdynloops.commands.SubCommand;
import org.onysand.mc.tempusdynloops.utils.MarkerUtils;

public class ListMarkers implements SubCommand {
    @Override
    public String getName() {
        return "listmarkers";
    }

    @Override
    public String getDescription() {
        return "Список ваших маркеров";
    }

    @Override
    public String getSyntax() {
        return "/tempusdynloops listmarkers";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            TextComponent message = Component.text("---------------------\n");

            for (Marker marker : MarkerUtils.listMarkers(player)) {
                message = message.append(Component.text(String.format("id: %s.world: %s, x: %d; y: %d; z: %d", marker.getMarkerID(), marker.getWorld(), (int) marker.getX(), (int) marker.getY(), (int) marker.getZ())))
                        .append(Component.text("\n"));
            }

            message = message.append(Component.text("---------------------"));

            player.sendMessage(message);
        }

        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            consoleCommandSender.sendMessage("Only player can execute this command");
        }
    }
}
