package io.percy.appium.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebElement;

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
    private List<WebElement> ignoreRegionAppiumElements = new ArrayList<WebElement>();
    private List<Region> customIgnoreRegions = new ArrayList<Region>();
    private List<String> considerRegionXpaths = new ArrayList<String>();
    private List<String> considerRegionAccessibilityIds = new ArrayList<String>();
    private List<WebElement> considerRegionAppiumElements = new ArrayList<WebElement>();
    private List<Region> customConsiderRegions = new ArrayList<Region>();
    private String scrollableXpath = null;
    private String scrollableId = null;
    private Integer topScrollviewOffset = 0;
    private Integer bottomScrollviewOffset = 0;

    public String getDeviceName() {
        return deviceName;
    }

    public Integer getStatusBarHeight() {
        return statusBarHeight;
    }

    public Integer getTopScrollviewOffset() {
        return topScrollviewOffset;
    }

    public Integer getBottomScrollviewOffset() {
        return bottomScrollviewOffset;
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

    public void setBottomScrollviewOffset(Integer bottomScrollviewOffsetParam) {
        bottomScrollviewOffset = bottomScrollviewOffsetParam;
    }

    public void setTopScrollviewOffset(Integer topScrollviewOffsetParam) {
        topScrollviewOffset = topScrollviewOffsetParam;
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

    public List<WebElement> getIgnoreRegionAppiumElements() {
        return ignoreRegionAppiumElements;
    }

    public void setIgnoreRegionAppiumElements(List<Object> ignoreRegionAppiumElements) {
        this.ignoreRegionAppiumElements = castMobileElementsToWebElements(ignoreRegionAppiumElements);
    }

    public List<Region> getCustomIgnoreRegions() {
        return customIgnoreRegions;
    }

    public void setCustomIgnoreRegions(List<Region> customIgnoreRegions) {
        this.customIgnoreRegions = customIgnoreRegions;
    }

    public List<String> getConsiderRegionXpaths() {
        return considerRegionXpaths;
    }

    public void setConsiderRegionXpaths(List<String> considerRegionXpaths) {
        this.considerRegionXpaths = considerRegionXpaths;
    }

    public List<String> getConsiderRegionAccessibilityIds() {
        return considerRegionAccessibilityIds;
    }

    public void setConsiderRegionAccessibilityIds(List<String> considerRegionAccessibilityIds) {
        this.considerRegionAccessibilityIds = considerRegionAccessibilityIds;
    }

    public List<WebElement> getConsiderRegionAppiumElements() {
        return considerRegionAppiumElements;
    }

    public void setConsiderRegionAppiumElements(List<Object> considerRegionAppiumElements) {
        this.considerRegionAppiumElements = castMobileElementsToWebElements(considerRegionAppiumElements);
    }

    public List<Region> getCustomConsiderRegions() {
        return customConsiderRegions;
    }

    public void setCustomConsiderRegions(List<Region> customConsiderRegions) {
        this.customConsiderRegions = customConsiderRegions;
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

    public List<WebElement> castMobileElementsToWebElements(List<Object> mobileElements) {
        List<WebElement> webElements = mobileElements.stream()
                .filter(obj -> WebElement.class.isAssignableFrom(obj.getClass())) // Check if the object is a WebElement
                .map(obj -> (WebElement) obj) // Cast the object to WebElement
                .collect(Collectors.toList());

        return webElements;
    }
}
