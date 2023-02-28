package io.percy.appium.metadata;

import io.appium.java_client.AppiumDriver;

public abstract class Metadata {
    private static AppiumDriver driver;
    private String orientation;
    private String platformVersion;
    private Integer statusBar;
    private Integer navBar;
    private String deviceName;

    public Metadata(AppiumDriver driver, String deviceName, Integer statusBar, Integer navBar, String orientation,
            String platformVersion) {
        Metadata.driver = driver;
        this.platformVersion = platformVersion;
        this.orientation = orientation;
        this.statusBar = statusBar;
        this.navBar = navBar;
        this.deviceName = deviceName;
    }

    public String osName() {
        String osName = driver.getCapabilities().getCapability("platformName").toString();
        return osName.substring(0, 1).toUpperCase() + osName.substring(1).toLowerCase();
    }

    public String platformVersion() {
        if (platformVersion != null) {
            return platformVersion;
        }
        Object osVersion = driver.getCapabilities().getCapability("platformVersion");
        if (osVersion == null) {
            osVersion = driver.getCapabilities().getCapability("os_version");
            if (osVersion == null) {
                return null;
            }
        }
        return osVersion.toString();
    }

    public String orientation() {
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

    public String getDeviceName() {
        return deviceName;
    }

    public Integer getNavBar() {
        return navBar;
    }

    public Integer getStatusBar() {
        return statusBar;
    }

    public abstract Integer deviceScreenWidth();

    public abstract String deviceName();

    public abstract Integer deviceScreenHeight();

    public abstract Integer statBarHeight();

    public abstract Integer navBarHeight();

    public abstract Integer scaleFactor();

}
