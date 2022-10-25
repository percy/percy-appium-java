package io.percy.appium.lib;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Tile {
    // File path where screenshot is stored
    public String localFilePath;
    public Integer statusBarHeight;
    public Integer navBarHeight;
    public Integer headerHeight;
    public Integer footerHeight;
    public Boolean fullScreen;

    public Tile(String localFilePath, Integer statusBarHeight, Integer navBarHeight, Integer headerHeight,
            Integer footerHeight, Boolean fullScreen) {
        this.localFilePath = localFilePath;
        this.statusBarHeight = statusBarHeight;
        this.navBarHeight = navBarHeight;
        this.headerHeight = headerHeight;
        this.footerHeight = footerHeight;
        this.fullScreen = fullScreen;
    }

    public static List<JSONObject> getTilesAsJson(List<Tile> tilesList) {
        List<JSONObject> tiles = new ArrayList<JSONObject>();
        for (Tile tile : tilesList) {
            JSONObject tileData = new JSONObject();
            tileData.put("filepath", tile.localFilePath);
            tileData.put("statusBarHeight", tile.statusBarHeight);
            tileData.put("navBarHeight", tile.navBarHeight);
            tileData.put("headerHeight", tile.headerHeight);
            tileData.put("footerHeight", tile.footerHeight);
            tileData.put("fullscreen", tile.fullScreen);
            tiles.add(tileData);
        }
        return tiles;
    }

}
