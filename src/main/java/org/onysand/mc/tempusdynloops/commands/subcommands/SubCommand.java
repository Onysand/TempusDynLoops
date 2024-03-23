package org.onysand.mc.tempusdynloops.commands.subcommands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface SubCommand {
    String getName();

    String getDescription();

    String getSyntax();

    void perform (CommandSender sender, String[] args);
}
