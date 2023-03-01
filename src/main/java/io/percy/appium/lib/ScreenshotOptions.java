package io.percy.appium.lib;

public class ScreenshotOptions {
    private String deviceName = null;
    private Integer statusBarHeight = null;
    private Integer navBarHeight = null;
    private String orientation = null;
    private Boolean fullPage = false;
    private Boolean fullScreen = false;
    private Integer screenLengths = 4;

    public String getDeviceName() {
        return deviceName;
    }

    public Integer getStatusBarHeight() {
        return statusBarHeight;
    }

    public Integer getNavBarHeight() {
        return navBarHeight;
    }

    public String getOrientation() {
        return orientation;
    }

    public Boolean getFullPage() {
        return fullPage;
    }

    public Boolean getFullScreen() {
        return fullScreen;
    }

    public Integer getScreenLengths() {
        return screenLengths;
    }

    public void setDeviceName(String deviceNameParam) {
        deviceName = deviceNameParam;
    }

    public void setStatusBarHeight(Integer statusBarHeightParam) {
        statusBarHeight = statusBarHeightParam;
    }

    public void setNavBarHeight(Integer navBarHeightParam) {
        navBarHeight = navBarHeightParam;
    }

    public void setOrientation(String orientationParam) {
        orientation = orientationParam;
    }

    public void setFullPage(Boolean fullPageParam) {
        fullPage = fullPageParam;
    }

    public void setFullScreen(Boolean fullScreenParam) {
        fullScreen = fullScreenParam;
    }

    public void setScreenLengths(Integer screenLengthsParam) {
        screenLengths = screenLengthsParam;
    }
}
