package io.percy.appium.lib;

import java.util.ArrayList;
import java.util.List;

import io.appium.java_client.MobileElement;

public class ScreenshotOptions {
    private String deviceName = null;
    private Integer statusBarHeight = null;
    private Integer navBarHeight = null;
    private String orientation = null;
    private Boolean fullPage = false;
    private Boolean fullScreen = false;
    private Integer screenLengths = 4;
    private List<String> ignoreRegionXpaths = new ArrayList<String>();
    private List<String> ignoreRegionAccessibilityIds = new ArrayList<String>();
    private List<MobileElement> ignoreRegionAppiumElements = new ArrayList<MobileElement>();
    private List<IgnoreRegion> customIgnoreRegions = new ArrayList<IgnoreRegion>();
    private String scrollableXpath = null;
    private String scrollableId = null;
    private Boolean forceFullPage = false;

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

    public Boolean getForceFullPage() {
        return forceFullPage;
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

    public List<String> getIgnoreRegionXpaths() {
        return ignoreRegionXpaths;
    }

    public void setIgnoreRegionXpaths(List<String> ignoreRegionXpaths) {
        this.ignoreRegionXpaths = ignoreRegionXpaths;
    }

    public List<String> getIgnoreRegionAccessibilityIds() {
        return ignoreRegionAccessibilityIds;
    }

    public void setIgnoreRegionAccessibilityIds(List<String> ignoreRegionAccessibilityIds) {
        this.ignoreRegionAccessibilityIds = ignoreRegionAccessibilityIds;
    }

    public List<MobileElement> getIgnoreRegionAppiumElements() {
        return ignoreRegionAppiumElements;
    }

    public void setIgnoreRegionAppiumElements(List<MobileElement> ignoreRegionAppiumElements) {
        this.ignoreRegionAppiumElements = ignoreRegionAppiumElements;
    }

    public List<IgnoreRegion> getCustomIgnoreRegions() {
        return customIgnoreRegions;
    }

    public void setCustomIgnoreRegions(List<IgnoreRegion> customIgnoreRegions) {
        this.customIgnoreRegions = customIgnoreRegions;
    }

    public String getScrollableXpath() {
        return scrollableXpath;
    }

    public void setScrollableXpath(String scrollableXpath) {
        this.scrollableXpath = scrollableXpath;
    }

    public String getScrollableId() {
        return scrollableId;
    }

    public void setScrollableId(String scrollableId) {
        this.scrollableId = scrollableId;
    }

    public void setForceFullPage(Boolean forceFullPage) {
        this.forceFullPage = forceFullPage;
    }
}
