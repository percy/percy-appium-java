package io.percy.appium.metadata;

import java.util.Map;

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

    public Integer deviceScreenWidth() {
        return Integer.parseInt(driver.getCapabilities().getCapability("deviceScreenSize")
                .toString().split("x")[0]);
    }

    public Integer deviceScreenHeight() {
        return Integer.parseInt(driver.getCapabilities().getCapability("deviceScreenSize")
                .toString().split("x")[1]);
    }

    public Integer statBarHeight() {
        try {
            return Integer.parseInt(
                    ((Map) getSystemBars().get("statusBar")).get("height").toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer navBarHeight() {
        try {
            return Integer.parseInt(
                    ((Map) getSystemBars().get("navigationBar")).get("height").toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private Map getSystemBars() {
        if (Cache.CACHE_MAP.get("systemBars_" + sessionId) == null) {
            Cache.CACHE_MAP.put("systemBars_" + sessionId, driver.getSystemBars());
        }
        return (Map) Cache.CACHE_MAP.get("systemBars_" + sessionId);
    }

}