package io.percy.appium.metadata;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.lib.Cache;

import org.json.JSONObject;

public abstract class Metadata {
    private static AppiumDriver driver;
    private String sessionId;

    public Metadata(AppiumDriver driver) {
        Metadata.driver = driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public String deviceName() {
        JSONObject sessionDetails = new JSONObject(getSessionDetails());
        return sessionDetails.get("device").toString();
    }

    public String osName() {
        return driver.getCapabilities().getPlatform().toString();
    }

    public String osVersion() {
        Object osVersion = driver.getCapabilities().getCapability("osVersion");
        if (osVersion == null) {
            return null;
        }
        return osVersion.toString();
    }

    public String orientation() {
        return driver.getOrientation().toString().toLowerCase();
    }

    private String getSessionDetails() {
        if (Cache.CACHE_MAP.get("getSessionDetails_" + sessionId) == null) {
            Cache.CACHE_MAP.put("getSessionDetails_" + sessionId,
                    driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"));
        }
        return Cache.CACHE_MAP.get("getSessionDetails_" + sessionId).toString();
    }

    public abstract Integer deviceScreenWidth();

    public abstract Integer deviceScreenHeight();

    public abstract Integer statBarHeight();

    public abstract Integer navBarHeight();

}
