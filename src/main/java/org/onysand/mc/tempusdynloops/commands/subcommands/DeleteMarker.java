package org.onysand.mc.tempusdynloops.commands.subcommands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.onysand.mc.tempusdynloops.TempusDynLoops;
import org.onysand.mc.tempusdynloops.components.MarkerComponent;
import org.onysand.mc.tempusdynloops.utils.Database;

import java.util.ArrayList;

public class DeleteMarker implements SubCommand{
    private final TempusDynLoops plugin;
    private final Database database;
    private final MarkerComponent markerComponent;

    public DeleteMarker(TempusDynLoops plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.markerComponent = new MarkerComponent(plugin);
    }
    @Override
    public String getName() {
        return "deletemarker";
    }

    @Override
    public String getDescription() {
        return "Удаляет маркер";
    }

    @Override
    public String getSyntax() {
        return "/tdl deletemarker <Владелец Маркера> <ID Маркера>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player && !player.hasPermission("tdl.deletemarker")) return;

        if (args.length <= 2) {
            sender.sendMessage(getSyntax());
            return;
        }

        try {
            if (markerComponent.remove(Integer.parseInt(args[2]), args[1])) { // 2 - markerID, 1 - ownerName
                sender.sendMessage("Маркер " + args[2] + " удален");
            } else {
                sender.sendMessage("Произошла ошибка при удалении маркера");
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<color:#9e1710>Ошибка. ID может содержать только цифровое значение</color>"));
        }
    }
}
