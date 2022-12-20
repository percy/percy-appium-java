package io.percy.appium;

import io.appium.java_client.AppiumDriver;

public class Environment {
    private AppiumDriver driver;
    public static final String SDK_VERSION = "0.0.5";
    private static final String SDK_NAME = "percy-appium-app";

    public Environment(AppiumDriver driver) {
        this.driver = driver;
    }

    public String getClientInfo() {
        return SDK_NAME + "/" + SDK_VERSION;
    }

    public String getEnvironmentInfo() {
        String[] splitDriverName = driver.getClass().getName().split("\\.");
        String driverName = splitDriverName[splitDriverName.length - 1];

        // We don't know this type of driver. Report its classname as environment info.
        return String.format("appium-java; %s", driverName);
    }

}
