package org.onysand.mc.tempusdynloops.commands.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.onysand.mc.tempusdynloops.commands.SubCommand;
import org.onysand.mc.tempusdynloops.utils.DynLocation;
import org.onysand.mc.tempusdynloops.utils.MarkerUtils;

import java.util.List;

public class ListCorners implements SubCommand {
    @Override
    public String getName() {
        return "listcorners";
    }

    @Override
    public String getDescription() {
        return "Вывести список всех созданных маркеров для ветки";
    }

    @Override
    public String getSyntax() {
        return "/tempusdynloops listcorners";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            TextComponent message = Component.text("---------------------\n");

            List<DynLocation> cornersList = MarkerUtils.listCorners(player);

            if (cornersList == null) {
                player.sendMessage("Не найдено маркеров ветки.");
                return;
            }

            for (DynLocation corner : cornersList) {
                message = message.append(Component.text(String.format("id: %s.world: %s, x: %d; y: %d; z: %d", cornersList.indexOf(corner), corner.world, (int) corner.x, (int) corner.y, (int) corner.z)))
                        .append(Component.text("\n"));
            }

            message = message.append(Component.text("---------------------"));

            player.sendMessage(message);
        }
    }
}
