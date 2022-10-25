package io.percy.appium.metadata;

import java.util.Map;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.percy.appium.lib.Cache;

public class IosMetadata extends Metadata {
    private IOSDriver driver;
    private String sessionId;

    public IosMetadata(AppiumDriver driver) {
        super(driver);
        this.driver = (IOSDriver) driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public Integer deviceScreenWidth() {
        return ((Long) getViewportRect().get("width")).intValue();
    }

    public Integer deviceScreenHeight() {
        Integer height = ((Long) getViewportRect().get("height")).intValue();
        Integer top = ((Long) getViewportRect().get("top")).intValue();
        return height + top;
    }

    public Integer statBarHeight() {
        return ((Long) getViewportRect().get("top")).intValue();
    }

    private Map getViewportRect() {
        if (Cache.CACHE_MAP.get("viewportRect_" + sessionId) == null) {
            Cache.CACHE_MAP.put("viewportRect_" + sessionId, driver.executeScript("mobile: viewportRect"));
        }
        return (Map) Cache.CACHE_MAP.get("viewportRect_" + sessionId);
    }

    public Integer navBarHeight() {
        return 0;
    }

}
