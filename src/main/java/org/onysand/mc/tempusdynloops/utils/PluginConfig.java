package org.onysand.mc.tempusdynloops.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.onysand.mc.tempusdynloops.TempusDynLoops;

public class PluginConfig {
    private final TempusDynLoops plugin;
    private FileConfiguration config;
    private String markersWorldName;
    private String markerWorldMessage;
    private String linesWorldName;
    private String linesWorldMessage;
    private String creatingLineEmptyLinesMessage;
    private String createdMarker;
    private String atLeastTwoCorners;
    private String onlyOwnerMessage;

    private String markerTag;
    private String cornerTag;
    private String lineTag;


    public PluginConfig(TempusDynLoops plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        initConfig();
    }

    public String getMarkersWorldName() {
        return markersWorldName;
    }
    public String getMarkerWorldMessage() {
        return markerWorldMessage;
    }
    public String getCreatingLineEmptyLinesMessage() {
        return creatingLineEmptyLinesMessage;
    }
    public String getCreatedMarker() {
        return createdMarker;
    }
    public String getLinesWorldName() {
        return linesWorldName;
    }
    public String getLinesWorldMessage() {
        return linesWorldMessage;
    }
    public String getAtLeastTwoCorners() {
        return atLeastTwoCorners;
    }
    public String getOnlyOwnerMessage() {
        return onlyOwnerMessage;
    }
    public String getMarkerTag() {
        return markerTag;
    }
    public String getCornerTag() {
        return cornerTag;
    }
    public String getLineTag() {
        return lineTag;
    }


    private void initConfig() {
        this.markersWorldName = config.getString("utils.worldName-markers", "check config: utils.worldName-markers");
        this.markerWorldMessage = config.getString("messages.markerWorldName", "check config: messages.markerWorldName");
        this.creatingLineEmptyLinesMessage = config.getString("messages.creatingLine-emptyLines", "check config: messages.creatingLine-emptyLines");
        this.createdMarker = config.getString("messages.createdMarker", "check config: messages.createdMarker");
        this.atLeastTwoCorners = config.getString("messages.atLeast-two-corners", "check config: messages.atLeast-two-corners");
        this.linesWorldName = config.getString("utils.worldName-lines", "check config: utils.worldName-lines");
        this.linesWorldMessage = config.getString("messages.linesWorldName", "check config: messages.linesWorldName");
        this.onlyOwnerMessage = config.getString("messages.onlyOwner-canBreak", "check config: messages.onlyOwner-canBreak");
        this.markerTag = config.getString("tags.create-marker", "check config: tags.create-marker");
        this.cornerTag = config.getString("tags.create-corner", "check config: tags.create-corner");
        this.lineTag = config.getString("tags.create-line", "check config: tags.create-line");
    }
}
