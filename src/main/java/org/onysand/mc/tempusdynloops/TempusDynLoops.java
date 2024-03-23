package org.onysand.mc.tempusdynloops;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.onysand.mc.tempusdynloops.commands.CommandsHandler;
import org.onysand.mc.tempusdynloops.events.SignBreakEvent;
import org.onysand.mc.tempusdynloops.events.SignEvent;
import org.onysand.mc.tempusdynloops.events.SignExplode;
import org.onysand.mc.tempusdynloops.utils.Database;
import org.onysand.mc.tempusdynloops.utils.PluginConfig;

public final class TempusDynLoops extends JavaPlugin {

    private TempusDynLoops plugin;
    private DynmapAPI dynmapAPI;

    private Database database;
    private PluginConfig pluginConfig;

    @Override
    public void onEnable() {
        plugin = this;
        plugin.saveDefaultConfig();

        pluginConfig = new PluginConfig(this);
        database = new Database(this);

        Plugin dynmap = getServer().getPluginManager().getPlugin("dynmap");
        this.dynmapAPI = (DynmapAPI) dynmap;

        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
    }

    public Database getDatabase() {
        return database;
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
        pm.registerEvents(new SignExplode(plugin), this);
    }

    private void registerCommands() {
        getCommand("tempusdynloops").setExecutor(new CommandsHandler(plugin));
    }
}
