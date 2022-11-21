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

    public AndroidMetadata(AppiumDriver driver) {
        super(driver);
        this.driver = (AndroidDriver) driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public String osName() {
        String osName = driver.getCapabilities().getCapability("platformName").toString();
        osName = osName.substring(0, 1).toUpperCase() + osName.substring(1).toLowerCase();
        return osName;
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
        return ((Long) getViewportRect().get("top")).intValue();
    }

    public Integer navBarHeight() {
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
