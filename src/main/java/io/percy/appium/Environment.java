package io.percy.appium;

import io.appium.java_client.AppiumDriver;

public class Environment {
    private AppiumDriver driver;
    public static final String SDK_VERSION = "2.0.0-beta.0";
    private static final String SDK_NAME = "percy-appium-app";
    private static String percyBuildID;
    private static String percyBuildUrl;
    private static String sessionType;

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

    public static String getPercyBuildID() {
        return percyBuildID;
    }

    public static void setPercyBuildID(String buildIDParam) {
        percyBuildID = buildIDParam;
    }

    public static String getPercyBuildUrl() {
        return percyBuildUrl;
    }

    public static void setPercyBuildUrl(String buildUrlParam) {
        percyBuildUrl = buildUrlParam;
    }

    public static String getSessionType() {
        return sessionType;
    }

    public static void setSessionType(String type) {
        sessionType = type;
    }
}
