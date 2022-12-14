package io.percy.appium.lib;

public class ScreenshotOptions {
  private String deviceName = null;
  private Integer statusBarHeight = null;
  private Integer navBarHeight = null;
  private String orientation = null;

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
}
