package org.onysand.mc.tempusdynloops.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.onysand.mc.tempusdynloops.commands.subcommands.ClearCorners;
import org.onysand.mc.tempusdynloops.commands.subcommands.ListCorners;
import org.onysand.mc.tempusdynloops.commands.subcommands.ListMarkers;
import org.onysand.mc.tempusdynloops.commands.subcommands.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandsHandler implements TabExecutor {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public CommandsHandler() {
        subCommands.add(new ListMarkers());
        subCommands.add(new ClearCorners());
        subCommands.add(new ListCorners());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length > 0) {
            for (SubCommand subcommand : subCommands) {
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    subcommand.perform(commandSender, args);
                    return true;
                }
            }
        }

        TextComponent component = Component.text("==============================\n");
        for (int i = 0; i < subCommands.size(); i++) {
            component = component.append(Component.text(subCommands.get(i).getSyntax() + " - " + subCommands.get(i).getDescription() + "\n"));
        }

        component = component.append(Component.text("=============================="));

        commandSender.sendMessage(component);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length <= 1)
            return subCommands.stream().map(SubCommand::getName).filter(name -> name.startsWith(args[0])).toList();

        return new ArrayList<>();
    }
}
