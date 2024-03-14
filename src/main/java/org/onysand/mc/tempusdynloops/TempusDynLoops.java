package org.onysand.mc.tempusdynloops;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.onysand.mc.tempusdynloops.commands.CommandsHandler;
import org.onysand.mc.tempusdynloops.events.SignBreakEvent;
import org.onysand.mc.tempusdynloops.events.SignEvent;
import org.onysand.mc.tempusdynloops.utils.DatabaseManager;
import org.onysand.mc.tempusdynloops.utils.MarkerUtils;
import org.onysand.mc.tempusdynloops.utils.PluginConfig;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class TempusDynLoops extends JavaPlugin {

    private static TempusDynLoops plugin;
    private DynmapAPI dynmapAPI;
    private final PluginConfig pluginConfig = new PluginConfig(this);
    public static DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        plugin = this;
        plugin.saveDefaultConfig();

        databaseManager = new DatabaseManager(getDataFolder().getAbsolutePath() + File.separator + "database.db");
        databaseManager.initializeDatabase();

        Plugin dynmap = getServer().getPluginManager().getPlugin("dynmap");
        this.dynmapAPI = (DynmapAPI) dynmap;


        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
    }

    public static TempusDynLoops getPlugin() {
        return plugin;
    }

    public DynmapAPI getDynmapAPI() {
        return dynmapAPI;
    }
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SignEvent(plugin), this);
        pm.registerEvents(new SignBreakEvent(plugin), this);
    }

    private void registerCommands() {
        getCommand("tempusdynloops").setExecutor(new CommandsHandler());
    }
}
