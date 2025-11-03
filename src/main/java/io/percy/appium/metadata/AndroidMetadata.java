package io.percy.appium.metadata;

import java.util.Map;

import org.json.JSONObject;
import org.openqa.selenium.ScreenOrientation;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Cache;
import io.percy.appium.lib.Utils;

public class AndroidMetadata extends Metadata {
    private AndroidDriver driver;

    public AndroidMetadata(AppiumDriver driver, String deviceName, Integer statusBar, Integer navBar,
            String orientation, String platformVersion) {
        super(driver, deviceName, statusBar, navBar, orientation, platformVersion);
        this.driver = (AndroidDriver) driver;
    }

    public String deviceName() {
        String deviceName = getDeviceName();
        if (deviceName != null) {
            return deviceName;
        }
        Object device = driver.getCapabilities().getCapability("device");

        // Try desired capabilities (legacy format)
        if (device == null) {
            Map desiredCaps = (Map) driver.getCapabilities().getCapability("desired");
            if (desiredCaps != null) {
                device = desiredCaps.get("deviceName");
            }
        }

        // For espresso driver
        if (device == null) {
            device = driver.getCapabilities().getCapability("appium:deviceName");
        }
        return device.toString();
    }

    public Integer deviceScreenWidth() {
        // Try deviceScreenSize from capabilities
        Object screenSize = driver.getCapabilities().getCapability("deviceScreenSize");
        if (screenSize != null) {
            return Integer.parseInt(screenSize.toString().split("x")[0]);
        }

        // Fall back to viewportRect if deviceScreenSize is not available
        Map viewportRect = getViewportRect();
        if (viewportRect != null && viewportRect.get("width") != null) {
            return ((Long) viewportRect.get("width")).intValue();
        }

        // Try to get from device info cache first (Espresso driver)
        String realDisplaySize = getRealDisplaySize();
        if (realDisplaySize != null) {
            return Integer.parseInt(realDisplaySize.split("x")[0]);
        }
        return 0; // Default fallback
    }

    public Integer deviceScreenHeight() {
        // Try deviceScreenSize from capabilities
        Object screenSize = driver.getCapabilities().getCapability("deviceScreenSize");
        if (screenSize != null) {
            return Integer.parseInt(screenSize.toString().split("x")[1]);
        }

        // Fall back to viewportRect if deviceScreenSize is not available
        Map viewportRect = getViewportRect();
        if (viewportRect != null && viewportRect.get("height") != null) {
            return ((Long) viewportRect.get("height")).intValue();
        }

        // Try to get from device info cache last (Espresso driver)
        String realDisplaySize = getRealDisplaySize();
        if (realDisplaySize != null) {
            return Integer.parseInt(realDisplaySize.split("x")[1]);
        }
        return 0; // Default fallback
    }

    // Method to get real display size using mobile: deviceInfo for Espresso driver
    private String getRealDisplaySize() {
        if (Cache.CACHE_MAP.get("realDisplaySize_" + sessionId) == null) {
            try {
                // Try to get device info using Appium's device info endpoint
                Map<String, Object> deviceInfo = (Map<String, Object>) driver.executeScript("mobile: deviceInfo");
                if (deviceInfo != null && deviceInfo.containsKey("realDisplaySize")) {
                    String displaySize = deviceInfo.get("realDisplaySize").toString();
                    Cache.CACHE_MAP.put("realDisplaySize_" + sessionId, displaySize);
                    return displaySize;
                }
            } catch (Exception e) {
                // If mobile: deviceInfo fails, return null to try other methods
            }
        }
        return (String) Cache.CACHE_MAP.get("realDisplaySize_" + sessionId);
    }

    public Integer statBarHeight() {
        Integer statBar = getStatusBar();
        if (statBar == null) {
            try {
              if (orientation != null && orientation.toLowerCase().equals("auto")) {
                statBar = Utils.extractStatusBarHeight(getDisplaySysDump());
              } else {
                statBar = Utils.extractStatusBarHeight(getDisplaySysDumpCache());
              }
            } catch (Exception e) {
              statBar = ((Long) getViewportRect().get("top")).intValue();
            }
        }

        return statBar;
    }

    public Integer navBarHeight() {
        Integer navBar = getNavBar();
        if (navBar == null) {
          try {
            if (orientation != null && orientation.toLowerCase().equals("auto")) {
              navBar = Utils.extractNavigationBarHeight(getDisplaySysDump());
            } else {
              navBar = Utils.extractNavigationBarHeight(getDisplaySysDumpCache());
            }
          } catch (Exception e) {
            Integer fullDeviceScreenHeight = deviceScreenHeight();
            Integer deviceScreenHeight = ((Long) getViewportRect().get("height")).intValue();
            navBar = fullDeviceScreenHeight - (deviceScreenHeight + statBarHeight());
          }
        }

        return navBar;
    }

    private Map getViewportRect() {
        if (Cache.CACHE_MAP.get("viewportRect_" + sessionId) == null) {
            Cache.CACHE_MAP.put("viewportRect_" + sessionId, driver.getCapabilities().getCapability("viewportRect"));
        }
        return (Map) Cache.CACHE_MAP.get("viewportRect_" + sessionId);
    }

    private String getDisplaySysDumpCache() {
      if (Cache.CACHE_MAP.get("getDisplaySysDump_" + sessionId) == null) {
          Cache.CACHE_MAP.put("getDisplaySysDump_" + sessionId, getDisplaySysDump());
      }
      return (String) Cache.CACHE_MAP.get("getDisplaySysDump_" + sessionId);
  }

    public Integer scaleFactor() {
        return 1;
    }

    private String getDisplaySysDump() {
        JSONObject arguments = new JSONObject();
        arguments.put("action", "adbShell");
        JSONObject command = new JSONObject();
        command.put("command", "dumpsys window displays");
        arguments.put("arguments", command);
        String resultString = driver
                .executeScript(String.format("browserstack_executor: %s", arguments.toString())).toString();
        return resultString;
    }

    protected ScreenOrientation driverGetOrientation() {
        return this.driver.getOrientation();
    }
}
