package io.percy.appium;

import io.appium.java_client.AppiumDriver;

public class Environment {
    private AppiumDriver driver;
    public final static String SDK_VERSION = "0.0.1";
    private final static String SDK_NAME = "percy-appium-java";

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
