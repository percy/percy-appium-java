package io.percy.appium.metadata;

import io.appium.java_client.AppiumDriver;

import io.percy.appium.providers.AppAutomate;

public abstract class Metadata {
    private static AppiumDriver driver;

    public Metadata(AppiumDriver driver) {
        Metadata.driver = driver;
    }

    public String deviceName() {
        // TODO Temp code, will fix when we add support to multiple providers
        return AppAutomate.getSessionDetails(driver).get("device").toString();
    }

    public String platformVersion() {
        Object platformVersion = driver.getCapabilities().getCapability("platformVersion");
        if (platformVersion == null) {
            platformVersion = driver.getCapabilities().getCapability("os_version");
            if (platformVersion == null) {
                return null;
            }
        }
        return platformVersion.toString();
    }

    public String orientation(String orientation) {
        if (orientation != null) {
            if (orientation.toLowerCase().equals("portrait") || orientation.toLowerCase().equals("landscape")) {
                return orientation.toLowerCase();
            } else if (orientation.toLowerCase().equals("auto")) {
                try {
                    return driver.getOrientation().toString().toLowerCase();
                } catch (java.lang.NoSuchMethodError e) {
                    return "portrait";
                }
            } else {
                return "portrait";
            }
        } else {
            Object orientationCapability = driver.getCapabilities().getCapability("orientation");
            if (orientationCapability != null) {
                return orientationCapability.toString().toLowerCase();
            } else {
                return "portrait";
            }
        }
    }

    public abstract String osName();

    public abstract Integer deviceScreenWidth();

    public abstract Integer deviceScreenHeight();

    public abstract Integer statBarHeight();

    public abstract Integer navBarHeight();

}
