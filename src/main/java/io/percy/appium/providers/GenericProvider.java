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
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.percy.appium.AppPercy;
import io.percy.appium.lib.CliWrapper;
import io.percy.appium.lib.IgnoreRegion;
import io.percy.appium.lib.ScreenshotOptions;
import io.percy.appium.lib.Tile;
import io.percy.appium.metadata.Metadata;
import io.percy.appium.metadata.MetadataHelper;

public class GenericProvider {
    private AppiumDriver driver;
    private Metadata metadata;
    private CliWrapper cliWrapper;
    private String debugUrl = null;

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

    public List<Tile> captureTiles(ScreenshotOptions options) throws IOException, Exception {
        if (options.getFullPage() && !AppAutomate.supports(driver)) {
            AppPercy.log("Full page screeshot is only supported on App Automate. "
                    + "Falling back to single page screenshot.");
        }
        Integer statusBar = metadata.statBarHeight();
        Integer navBar = metadata.navBarHeight();
        String srcString = captureScreenshot();
        String localFilePath = getAbsolutePath(srcString);
        Integer headerHeight = 0;
        Integer footerHeight = 0;
        List<Tile> tiles = new ArrayList<Tile>();
        tiles.add(new Tile(
                localFilePath, statusBar, navBar, headerHeight, footerHeight, options.getFullScreen(), null));
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

    public String screenshot(String name, ScreenshotOptions options) throws Exception {
        return screenshot(name, options, null, null);
    }

    private JSONObject getObjectForArray(String key, JSONArray arr) {
        JSONObject elementsData = new JSONObject();
        elementsData.put(key, arr);

        return elementsData;
    }

    public String screenshot(String name, ScreenshotOptions options,
            String platformVersion, String deviceName) throws Exception {
        this.metadata = MetadataHelper.resolve(driver, deviceName, options.getStatusBarHeight(),
                options.getNavBarHeight(), options.getOrientation(), platformVersion);
        JSONObject tag = getTag();
        List<Tile> tiles = captureTiles(options);
        JSONObject ignoreRegion = findRegions(
            options.getIgnoreRegionXpaths(),
            options.getIgnoreRegionAccessibilityIds(),
            options.getIgnoreRegionAppiumElements(),
            options.getCustomIgnoreRegions()
        );
        JSONObject considerRegion = findRegions(
            options.getConsiderRegionXpaths(),
            options.getConsiderRegionAccessibilityIds(),
            options.getConsiderRegionAppiumElements(),
            options.getCustomConsiderRegions()
        );
        return cliWrapper.postScreenshot(
            name,
            tag,
            tiles,
            debugUrl,
            getObjectForArray("ignoreElementsData", ignoreRegion),
            getObjectForArray("considerElementsData", considerRegion)
        );
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setDebugUrl(String debugUrl) {
        this.debugUrl = debugUrl;
    }

    public JSONArray findRegions(
        List<string> xpaths,
        List<string> accessibilityIds,
        List<MobileElement> elements,
        List<Region> locations
    ) {
        JSONArray elementsArray = new JSONArray();
        getRegionsByXpath(elementsArray, xpaths);
        getRegionsByIds(elementsArray, accessibilityIds);
        getRegionsByElements(elementsArray, elements);
        getRegionsByLocation(elementsArray, locations);

        return elementsArray;
    }

    public JSONObject getRegionObject(String selector, MobileElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        double scaleFactor = metadata.scaleFactor();
        JSONObject coOrdinates = new JSONObject();
        coOrdinates.put("top", location.getY() * scaleFactor);
        coOrdinates.put("bottom", (location.getY() + size.getHeight()) * scaleFactor);
        coOrdinates.put("left", location.getX() * scaleFactor);
        coOrdinates.put("right", (location.getX() + size.getWidth()) * scaleFactor);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("selector", selector);
        jsonObject.put("co_ordinates", coOrdinates);

        return jsonObject;
    }

    public void getRegionsByXpath(JSONArray elementsArray, List<String> xpaths) {
        for (String xpath : xpaths) {
            try {
                MobileElement element = (MobileElement) driver.findElementByXPath(xpath);
                String selector = String.format("xpath: %s", xpath);
                JSONObject region = getRegionObject(selector, element);
                elementsArray.put(region);
            } catch (Exception e) {
                AppPercy.log(String.format("Appium Element with xpath: %s not found. Ignoring this xpath.", xpath));
                AppPercy.log(e.toString(), "debug");
            }
        }
    }

    public void getRegionsByIds(JSONArray elementsArray, List<String> ids) {
        for (String id : ids) {
            try {
                MobileElement element = (MobileElement) driver.findElementByAccessibilityId(id);
                String selector = String.format("id: %s", id);
                JSONObject region = getRegionObject(selector, element);
                elementsArray.put(region);

            } catch (Exception e) {
                AppPercy.log(String.format("Appium Element with id: %d not found. Ignoring this id.", id));
                AppPercy.log(e.toString(), "debug");
            }
        }
    }

    public void getRegionsByElements(JSONArray elementsArray, List<MobileElement> elements) {
        for (int index = 0; index < elements.size(); index++) {
            try {
                String type = elements.get(index).getAttribute("class");
                String selector = String.format("element: %d %s", index, type);

                JSONObject region = getRegionObject(selector, elements.get(index));
                elementsArray.put(region);
            } catch (Exception e) {
                AppPercy.log(String.format("Correct Mobile Element not passed at index %d.", index));
                AppPercy.log(e.toString(), "debug");
            }
        }
    }

    public void getRegionsByLocation(JSONArray elementsArray, List<Region> customLocations) {
        int width = metadata.deviceScreenWidth();
        int height = metadata.deviceScreenHeight();
        for (int index = 0; index < customLocations.size(); index++) {
            try {
                Region customLocation = customLocations.get(index);
                if (customLocation.isValid(width, height)) {
                    String selector = "custom region " + index;
                    JSONObject region = new JSONObject();
                    JSONObject coordinates = new JSONObject();
                    coordinates.put("top", customLocation.getTop());
                    coordinates.put("bottom", customLocation.getBottom());
                    coordinates.put("left", customLocation.getLeft());
                    coordinates.put("right", customLocation.getRight());
                    region.put("selector", selector);
                    region.put("co_ordinates", coordinates);
                    elementsArray.put(region);
                } else {
                    AppPercy.log(
                            String.format("Values passed in custom region at index: %d is not valid", index));
                }
            } catch (Exception e) {
                AppPercy.log(e.toString(), "debug");
            }
        }
    }
}
