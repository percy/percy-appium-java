package io.percy.appium.metadata;

import java.util.Map;

import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.http.HttpMethod;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.percy.appium.lib.Cache;

public class IosMetadata extends Metadata {
    private IOSDriver driver;
    private String sessionId;

    public IosMetadata(AppiumDriver driver, String deviceName, Integer statusBar, Integer navBar, String orientation,
            String platformVersion) {
        super(driver, deviceName, statusBar, navBar, orientation, platformVersion);
        this.driver = (IOSDriver) driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public String deviceName() {
        String deviceName = getDeviceName();
        if (deviceName != null) {
            return deviceName;
        }
        return driver.getCapabilities().getCapability("deviceName").toString();
    }

    public String osName() {
        String osName = driver.getCapabilities().getCapability("platformName").toString();
        return osName.substring(0, 1).toLowerCase() + osName.substring(1).toUpperCase();
    }

    public Integer deviceScreenWidth() {
        Integer deviceScreenWidth = MetadataHelper.valueFromStaticDevicesInfo("screenWidth",
                this.deviceName().toLowerCase());
        if (deviceScreenWidth == 0) {
            deviceScreenWidth = ((Long) getViewportRect().get("width")).intValue();
        }
        return deviceScreenWidth;
    }

    public Integer deviceScreenHeight() {
        Integer deviceScreenHeight = MetadataHelper.valueFromStaticDevicesInfo("screenHeight",
                this.deviceName().toLowerCase());
        if (deviceScreenHeight == 0) {
            deviceScreenHeight = ((Long) getViewportRect().get("height")).intValue();
        }
        return deviceScreenHeight;
    }

    public Integer statBarHeight() {
        Integer statBar = getStatusBar();
        if (statBar != null) {
            return statBar;
        }
        Integer statBarHeight = MetadataHelper.valueFromStaticDevicesInfo("statusBarHeight",
                this.deviceName().toLowerCase());
        Integer pixelRatio = MetadataHelper.valueFromStaticDevicesInfo("pixelRatio",
                this.deviceName().toLowerCase());
        if (statBarHeight == 0) {
            return ((Long) getViewportRect().get("top")).intValue();
        }
        return statBarHeight * pixelRatio;
    }

    private Map getViewportRect() {
        if (Cache.CACHE_MAP.get("viewportRect_" + sessionId) == null) {
            try {
                Cache.CACHE_MAP.put("viewportRect_" + sessionId, getSession().get("viewportRect"));
            } catch (java.lang.NoSuchMethodError e) {
                Cache.CACHE_MAP.put("viewportRect_" + sessionId, driver.getSessionDetails().get("viewportRect"));
            }
        }
        return (Map) Cache.CACHE_MAP.get("viewportRect_" + sessionId);
    }

    private Map getSession() {
        driver.addCommand(HttpMethod.GET, "/session/" + driver.getSessionId(), "getSession");
        Response session = driver.execute("getSession");
        return (Map) session.getValue();
    }

    public Integer navBarHeight() {
        Integer navBar = getNavBar();
        if (navBar != null) {
            return navBar;
        }
        return 0;
    }

    public Integer scaleFactor() {
        return (Integer) driver.getSessionDetails().get("pixelRatio");
    }

}
