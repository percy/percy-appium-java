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

    public IosMetadata(AppiumDriver driver) {
        super(driver);
        this.driver = (IOSDriver) driver;
        this.sessionId = driver.getSessionId().toString();
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
        Integer statBarHeight = MetadataHelper.valueFromStaticDevicesInfo("screenHeight",
                this.deviceName().toLowerCase());
        if (statBarHeight == 0) {
            statBarHeight = ((Long) getViewportRect().get("top")).intValue();
        }
        return statBarHeight;
    }

    private Map getViewportRect() {
        if (Cache.CACHE_MAP.get("viewportRect_" + sessionId) == null) {
            try {
                Cache.CACHE_MAP.put("viewportRect_" + sessionId, getSession().get("viewportRect"));
            } catch (Exception e) {
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
        return 0;
    }

}
