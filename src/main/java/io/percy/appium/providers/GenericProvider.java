package io.percy.appium.providers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.openqa.selenium.OutputType;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.lib.CliWrapper;
import io.percy.appium.lib.Tile;
import io.percy.appium.metadata.Metadata;

public class GenericProvider {
    private AppiumDriver driver;
    private Metadata metadata;
    private CliWrapper cliWrapper;

    public GenericProvider(AppiumDriver driver, Metadata metadata) {
        this.driver = driver;
        this.metadata = metadata;
        this.cliWrapper = new CliWrapper(driver);
    }

    public JSONObject getTag() {
        JSONObject tag = new JSONObject();
        tag.put("name", metadata.deviceName());
        tag.put("osName", metadata.osName());
        tag.put("osVersion", metadata.osVersion());
        tag.put("width", metadata.deviceScreenWidth());
        tag.put("height", metadata.deviceScreenHeight());
        tag.put("orientation", metadata.orientation());
        return tag;
    }

    public List<Tile> captureTiles(Boolean fullScreen) {
        byte[] srcBytes = captureScreenshot();
        String localFilePath = getAbsolutePath(srcBytes);
        Integer statusBarHeight = metadata.statBarHeight();
        Integer navBarHeight = metadata.navBarHeight();
        Integer headerHeight = 0;
        Integer footerHeight = 0;
        List<Tile> tiles = new ArrayList<Tile>();
        tiles.add(new Tile(localFilePath, statusBarHeight, navBarHeight, headerHeight, footerHeight, fullScreen));
        return tiles;
    }

    public static Boolean supports(AppiumDriver driver) {
        return true;
    }

    private String getAbsolutePath(byte[] srcBytes) {
        String dirPath = getDirPath();
        String filePath = dirPath + UUID.randomUUID().toString() + ".png";
        try {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(srcBytes);
            outputStream.close();
        } catch (IOException e) {
            AppPercy.log("Failed to write to file", "info");
        }
        return filePath;
    }

    private String getDirPath() {
        String os = System.getProperty("os.name");
        if (os.contains("Mac") || os.contains("Linux")) {
            return "/tmp/";
        } else {
            return "C:\\Users\\AppData\\Local\\Temp\\";
        }
    }

    private byte[] captureScreenshot() {
        return driver.getScreenshotAs(OutputType.BYTES);
    }

    public String screenshot(String name, Boolean fullScreen, String debugUrl) {
        JSONObject tag = getTag();
        List<Tile> tiles = captureTiles(fullScreen);
        return cliWrapper.postScreenshot(name, tag, tiles, debugUrl);
    }

    public String getDebugUrl() {
        return null;
    }

}
