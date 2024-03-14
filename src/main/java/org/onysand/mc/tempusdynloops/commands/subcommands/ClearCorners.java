package org.onysand.mc.tempusdynloops.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.onysand.mc.tempusdynloops.commands.SubCommand;
import org.onysand.mc.tempusdynloops.utils.MarkerUtils;

public class ClearCorners implements SubCommand {
    @Override
    public String getName() {
        return "clearcorners";
    }

    @Override
    public String getDescription() {
        return "Удалить маркеры ветки";
    }

    @Override
    public String getSyntax() {
        return "/tempusdynloops clearcorners";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            MarkerUtils.clearCorners(player);

            player.sendMessage("Маркеры ветки удалены");
        }
    }
}
