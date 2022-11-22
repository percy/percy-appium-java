package io.percy.appium.metadata;

import java.util.Map;

import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.http.HttpMethod;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Cache;

public class AndroidMetadata extends Metadata {
    private AndroidDriver driver;
    private String sessionId;
    private Integer statusBar;
    private Integer navBar;
    private String deviceName;

    public AndroidMetadata(AppiumDriver driver, String deviceName, Integer statusBar, Integer navBar,
            String orientation, String platformVersion) {
        super(driver, platformVersion, orientation);
        this.statusBar = statusBar;
        this.navBar = navBar;
        this.deviceName = deviceName;
        this.driver = (AndroidDriver) driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public String deviceName() {
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
        if (statusBar != null) {
            return statusBar;
        }
        return ((Long) getViewportRect().get("top")).intValue();
    }

    public Integer navBarHeight() {
        if (navBar != null) {
            return navBar;
        }
        Integer fullDeviceScreenHeight = deviceScreenHeight();
        Integer deviceScreenHeight = ((Long) getViewportRect().get("height")).intValue();
        return fullDeviceScreenHeight - (deviceScreenHeight + statBarHeight());
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

}
