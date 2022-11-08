package io.percy.appium.providers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
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

    public JSONObject getTag(String deviceName, String orientation) {
        JSONObject tag = new JSONObject();
        String name = deviceName;
        String appOrientation = orientation;
        if (deviceName == null) {
            name = metadata.deviceName();
        }
        if (orientation == null) {
            appOrientation = metadata.orientation();
        }
        tag.put("name", name);
        tag.put("osName", metadata.osName());
        tag.put("osVersion", metadata.platformVersion());
        tag.put("width", metadata.deviceScreenWidth());
        tag.put("height", metadata.deviceScreenHeight());
        tag.put("orientation", appOrientation);
        return tag;
    }

    public List<Tile> captureTiles(Boolean fullScreen, Integer statusBarHeight, Integer navBarHeight) {
        Integer statusBar = statusBarHeight;
        Integer navBar = navBarHeight;
        if (statusBarHeight == null) {
            statusBar = metadata.statBarHeight();
        }
        if (navBarHeight == null) {
            navBar = metadata.navBarHeight();
        }
        String srcString = captureScreenshot();
        String localFilePath = getAbsolutePath(srcString);
        Integer headerHeight = 0;
        Integer footerHeight = 0;
        List<Tile> tiles = new ArrayList<Tile>();
        tiles.add(new Tile(localFilePath, statusBar, navBar, headerHeight, footerHeight, fullScreen));
        return tiles;
    }

    public static Boolean supports(AppiumDriver driver) {
        return true;
    }

    private String getAbsolutePath(String srcString) {
        String dirPath = getDirPath();
        String filePath = dirPath + UUID.randomUUID().toString() + ".png";
        try {
            byte[] data = Base64.decodeBase64(srcString);
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            AppPercy.log("Failed to write to file");
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

    private String captureScreenshot() {
        return driver.getScreenshotAs(OutputType.BASE64);
    }

    public String screenshot(String name, String deviceName, Integer statusBarHeight, Integer navBarHeight,
            String orientation, Boolean fullScreen, String debugUrl) {
        JSONObject tag = getTag(deviceName, orientation);
        List<Tile> tiles = captureTiles(fullScreen, statusBarHeight, navBarHeight);
        return cliWrapper.postScreenshot(name, tag, tiles, debugUrl);
    }

    public String getDebugUrl() {
        return null;
    }

}
