package io.percy.appium;

import io.appium.java_client.AppiumDriver;

public class Environment {
    private AppiumDriver driver;
    public static final String SDK_VERSION = "2.1.2";
    private static final String SDK_NAME = "percy-appium-app";
    private static String percyBuildID;
    private static String percyBuildUrl;
    private static String sessionType;

    public Environment(AppiumDriver driver) {
        this.driver = driver;
    }

    public String getClientInfo(Boolean flag) {
        if (flag) {
            return SDK_NAME + "-java" + "/" + SDK_VERSION;
        }
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

    public static Boolean getForceFullPage() {
        return System.getenv().getOrDefault("FORCE_FULL_PAGE", "false").equals("true");
    }

    public static Boolean getDisableRemoteUploads() {
        return System.getenv().getOrDefault("PERCY_DISABLE_REMOTE_UPLOADS", "false").equals("true");
    }

    public static Boolean getEnablePercyDev() {
        return System.getenv().getOrDefault("PERCY_ENABLE_DEV", "false").equals("true");
    }

    public static String getSessionType() {
        return sessionType;
    }

    public static void setSessionType(String type) {
        sessionType = type;
    }
}
