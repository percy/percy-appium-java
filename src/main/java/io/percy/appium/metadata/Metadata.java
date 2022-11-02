package io.percy.appium.metadata;

import io.appium.java_client.AppiumDriver;

public abstract class Metadata {
    private static AppiumDriver driver;

    public Metadata(AppiumDriver driver) {
        Metadata.driver = driver;
    }

    public String deviceName() {
        return MetadataHelper.getSessionDetails(driver).get("device").toString();
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

    public abstract Integer deviceScreenWidth();

    public abstract Integer deviceScreenHeight();

    public abstract Integer statBarHeight();

    public abstract Integer navBarHeight();

}
