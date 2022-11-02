package io.percy.appium.metadata;

import java.util.Map;

import org.openqa.selenium.Dimension;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.percy.appium.AppPercy;
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
        Integer deviceScreenWidth;
        try {
            deviceScreenWidth = ((Long) getViewportRect().get("width")).intValue();
        } catch (Exception e) {
            AppPercy.log("Could not fetch deviceScreenWidth from viewportRect, using static config", "debug");
            Cache.CACHE_MAP.put("viewportRectFallback_" + sessionId, "true");
            Integer scaleFactor = MetadataHelper.valueFromStaticDevicesInfo("scale_factor",
                    this.deviceName().toLowerCase());
            deviceScreenWidth = getWindowSize().getWidth() * scaleFactor;
        }
        return deviceScreenWidth;
    }

    public Integer deviceScreenHeight() {
        Integer deviceScreenHeight;
        try {
            if (Cache.CACHE_MAP.get("viewportRectFallback_" + sessionId) == "true") {
                throw new Exception("viewportRectFallback");
            }
            Integer height = ((Long) getViewportRect().get("height")).intValue();
            Integer top = ((Long) getViewportRect().get("top")).intValue();
            deviceScreenHeight = height + top;
        } catch (Exception e) {
            AppPercy.log("Could not fetch deviceScreenHeight from viewportRect, using static config", "debug");
            Integer scaleFactor = MetadataHelper.valueFromStaticDevicesInfo("scale_factor",
                    this.deviceName().toLowerCase());
            deviceScreenHeight = getWindowSize().getHeight() * scaleFactor;
        }
        return deviceScreenHeight;
    }

    public Integer statBarHeight() {
        Integer statBarHeight;
        try {
            if (Cache.CACHE_MAP.get("viewportRectFallback_" + sessionId) == "true") {
                throw new Exception("viewportRectFallback");
            }
            statBarHeight = ((Long) getViewportRect().get("top")).intValue();
        } catch (Exception e) {
            AppPercy.log("Could not fetch statBarHeight from viewportRect, using static config", "debug");
            Integer scaleFactor = MetadataHelper.valueFromStaticDevicesInfo("scale_factor",
                    this.deviceName().toLowerCase());
            Integer statusBarHeight = MetadataHelper.valueFromStaticDevicesInfo("status_bar",
                    this.deviceName().toLowerCase());
            statBarHeight = statusBarHeight * scaleFactor;
        }
        return statBarHeight;
    }

    private Map getViewportRect() {
        if (Cache.CACHE_MAP.get("viewportRect_" + sessionId) == null) {
            Cache.CACHE_MAP.put("viewportRect_" + sessionId, driver.executeScript("mobile: viewportRect"));
        }
        return (Map) Cache.CACHE_MAP.get("viewportRect_" + sessionId);
    }

    private Dimension getWindowSize() {
        if (Cache.CACHE_MAP.get("windowSize_" + sessionId) == null) {
            Cache.CACHE_MAP.put("windowSize_" + sessionId, driver.manage().window().getSize());
        }
        return (Dimension) Cache.CACHE_MAP.get("windowSize_" + sessionId);
    }

    public Integer navBarHeight() {
        return 0;
    }

}
