package org.onysand.mc.tempusdynloops.commands.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.utils.Database;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class ListMarkers implements SubCommand {
    private final TempusDynLoops plugin;
    private final Database database;
    private final MiniMessage mm;

    public ListMarkers(TempusDynLoops plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.mm = MiniMessage.miniMessage();
    }

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
            if (!player.hasPermission("tdl.listmarker")) return;

            TextComponent message = Component.text("---------------------\n");

            for (Map.Entry<Integer, ArrayList<String>> entry : database.listMarkers().entrySet()) {
                Integer id = entry.getKey();
                ArrayList<String> list = entry.getValue();
                message = message.append(Component.text(String.format("ID: %d; Локация: ( %s ) Ник владельца: %s; Loop? %b\n", id, list.get(0), list.get(1), list.get(2)))
                        .clickEvent(ClickEvent.runCommand("/tdl tpmarker " + id))
                        .hoverEvent(HoverEvent.showText(mm.deserialize("<dark_aqua>Телепортироваться к маркеру</dark_aqua>"))));
                message = message.append(mm.deserialize(" <dark_gray>[</dark_gray> <dark_red>Удалить?</dark_red> <dark_gray>]</dark_gray> \n\n")).clickEvent(ClickEvent.suggestCommand("/tdl deletemarker " + id));
            }

            message = message.append(Component.text("---------------------"));

            player.sendMessage(message);
        }

        if (sender instanceof ConsoleCommandSender consoleSender) {
            TextComponent message = Component.text("---------------------\n");

            for (Map.Entry<Integer, ArrayList<String>> entry : database.listMarkers().entrySet()) {
                Integer id = entry.getKey();
                ArrayList<String> list = entry.getValue();
                message = message.append(Component.text(String.format("ID: %d; Локация: %s; Ник владельца: %s; Loop? %b\n", id, list.get(0), list.get(1), list.get(2))));
            }

            message = message.append(Component.text("---------------------"));

            consoleSender.sendMessage(message);
        }
    }
}
