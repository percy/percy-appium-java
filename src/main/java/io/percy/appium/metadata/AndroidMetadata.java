package io.percy.appium.metadata;

import java.util.Map;

import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Cache;
import io.percy.appium.lib.Utils;

public class AndroidMetadata extends Metadata {
    private AndroidDriver driver;
    private String sessionId;
    private String orientation;

    public AndroidMetadata(AppiumDriver driver, String deviceName, Integer statusBar, Integer navBar,
            String orientation, String platformVersion) {
        super(driver, deviceName, statusBar, navBar, orientation, platformVersion);
        this.driver = (AndroidDriver) driver;
        this.orientation = orientation;
        this.sessionId = driver.getSessionId().toString();
    }

    public String deviceName() {
        String deviceName = getDeviceName();
        if (deviceName != null) {
            return deviceName;
        }
        Object device = driver.getCapabilities().getCapability("device");
        if (device == null) {
            Map desiredCaps = (Map) driver.getCapabilities().getCapability("desired");
            device = desiredCaps.get("deviceName");
        }
        return device.toString();
    }

    public Integer deviceScreenWidth() {
        return Integer.parseInt(driver.getCapabilities().getCapability("deviceScreenSize")
                .toString().split("x")[0]);
    }

    public Integer deviceScreenHeight() {
        return Integer.parseInt(driver.getCapabilities().getCapability("deviceScreenSize")
                .toString().split("x")[1]);
    }

    public Integer statBarHeight() {

        Integer statBar = getStatusBar();
        if (statBar == null && orientation != null && orientation.toLowerCase().equals("auto")) {
            statBar = Utils.extractStatusBarHeight(getDisplaySysDump());
        }
        if (statBar != null) {
            return statBar;
        }
        return ((Long) getViewportRect().get("top")).intValue();
    }

    public Integer navBarHeight() {
        Integer navBar = getNavBar();
        if (navBar == null && orientation != null && orientation.toLowerCase().equals("auto")) {
            navBar = Utils.extractNavigationBarHeight(getDisplaySysDump());
        }
        if (navBar != null) {
            return navBar;
        }
        Integer fullDeviceScreenHeight = deviceScreenHeight();
        Integer deviceScreenHeight = ((Long) getViewportRect().get("height")).intValue();
        return fullDeviceScreenHeight - (deviceScreenHeight + statBarHeight());
    }

    private Map getViewportRect() {
        if (Cache.CACHE_MAP.get("viewportRect_" + sessionId) == null) {
            Cache.CACHE_MAP.put("viewportRect_" + sessionId, getSession().get("viewportRect"));
        }
        return (Map) Cache.CACHE_MAP.get("viewportRect_" + sessionId);
    }

    public Integer scaleFactor() {
        return 1;
    }

    public String getDisplaySysDump() {
        if (Cache.CACHE_MAP.get("getDisplaySysDump_" + sessionId) == null) {

            JSONObject arguments = new JSONObject();
            arguments.put("action", "adbShell");
            JSONObject command = new JSONObject();
            command.put("command", "dumpsys window displays");
            arguments.put("arguments", command);
            String resultString = driver
                    .executeScript(String.format("browserstack_executor: %s", arguments.toString())).toString();
            Cache.CACHE_MAP.put("getDisplaySysDump_" + sessionId, resultString);
        }
        return (String) Cache.CACHE_MAP.get("getDisplaySysDump_" + sessionId);
    }

}
