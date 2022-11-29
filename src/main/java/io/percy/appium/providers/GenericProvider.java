package io.percy.appium.providers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import io.percy.appium.metadata.MetadataHelper;

public class GenericProvider {
    private AppiumDriver driver;
    private Metadata metadata;
    private CliWrapper cliWrapper;

    public GenericProvider(AppiumDriver driver) {
        this.driver = driver;
        this.cliWrapper = new CliWrapper(driver);
    }

    public JSONObject getTag() {
        JSONObject tag = new JSONObject();
        tag.put("name", metadata.deviceName());
        tag.put("osName", metadata.osName());
        tag.put("osVersion", metadata.platformVersion());
        tag.put("width", metadata.deviceScreenWidth());
        tag.put("height", metadata.deviceScreenHeight());
        tag.put("orientation", metadata.orientation());
        return tag;
    }

    public List<Tile> captureTiles(Boolean fullScreen) throws IOException {
        Integer statusBar = metadata.statBarHeight();
        Integer navBar = metadata.navBarHeight();
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

    private String getAbsolutePath(String srcString) throws IOException {
        Path dirPath = getDirPath();
        String filePath = Paths.get(
            dirPath.toString(), UUID.randomUUID().toString() + ".png").toAbsolutePath().toString();
        try {
            byte[] data = Base64.decodeBase64(srcString);
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            AppPercy.log("Failed to write to file: " + filePath);
            throw e;
        }
        return filePath;
    }

    private Path getDirPath() throws IOException {
        String tempDir = System.getenv().getOrDefault("PERCY_TMP_DIR", null);
        if (tempDir == null) {
            tempDir = System.getProperty("java.io.tmpdir");
        }
        Path tempDirPath = Paths.get(tempDir);
        Files.createDirectories(tempDirPath);
        return tempDirPath;
    }

    private String captureScreenshot() {
        return driver.getScreenshotAs(OutputType.BASE64);
    }

    public String screenshot(String name, String deviceName, Integer statusBarHeight, Integer navBarHeight,
            String orientation, Boolean fullScreen, String debugUrl) throws IOException {
        return screenshot(name, deviceName, statusBarHeight, navBarHeight, orientation, fullScreen, debugUrl, null);
    }

    public String screenshot(String name, String deviceName, Integer statusBarHeight, Integer navBarHeight,
            String orientation, Boolean fullScreen, String debugUrl, String platformVersion) throws IOException {
        this.metadata = MetadataHelper.resolve(driver, deviceName, statusBarHeight, navBarHeight, orientation,
                platformVersion);
        JSONObject tag = getTag();
        List<Tile> tiles = captureTiles(fullScreen);
        return cliWrapper.postScreenshot(name, tag, tiles, debugUrl);
    }

    public String getDebugUrl() {
        return null;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

}
