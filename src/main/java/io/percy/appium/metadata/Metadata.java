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

    public String osName() {
        return driver.getCapabilities().getCapability("platformName").toString();
    }

    public String platformVersion() {
        return driver.getCapabilities().getCapability("platformVersion").toString();
    }

    public String orientation() {
        try {
            return driver.getOrientation().toString().toLowerCase();
        } catch (java.lang.NoSuchMethodError e) {
            // TODO Need to fix this for appium client v8
            return "PORTRAIT";
        }
    }

    public abstract Integer deviceScreenWidth();

    public abstract Integer deviceScreenHeight();

    public abstract Integer statBarHeight();

    public abstract Integer navBarHeight();

}
