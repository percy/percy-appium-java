package io.percy.appium.lib;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Tile {
    // File path where screenshot is stored
    private String localFilePath;
    private String sha;
    private Integer statusBarHeight;
    private Integer navBarHeight;
    private Integer headerHeight;
    private Integer footerHeight;
    private Boolean fullScreen;

    public Tile(String localFilePath, Integer statusBarHeight, Integer navBarHeight, Integer headerHeight,
            Integer footerHeight, Boolean fullScreen, String sha) {
        this.localFilePath = localFilePath;
        this.statusBarHeight = statusBarHeight;
        this.navBarHeight = navBarHeight;
        this.headerHeight = headerHeight;
        this.footerHeight = footerHeight;
        this.fullScreen = fullScreen;
        this.sha = sha;
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
            tileData.put("sha", tile.sha);
            tiles.add(tileData);
        }
        return tiles;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public String getSha() {
        return sha;
    }

    public Integer getStatusBarHeight() {
        return statusBarHeight;
    }

    public Integer getNavBarHeight() {
        return navBarHeight;
    }

    public Integer getHeaderHeight() {
        return headerHeight;
    }

    public Integer getFooterHeight() {
        return footerHeight;
    }

    public Boolean getFullScreen() {
        return fullScreen;
    }

}
