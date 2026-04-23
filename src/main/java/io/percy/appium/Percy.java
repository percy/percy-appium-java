package io.percy.appium;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.lib.CliWrapper;
import io.percy.appium.lib.ScreenshotOptions;

import java.util.Map;

import org.json.JSONObject;

public class Percy {
    private AppiumDriver driver;
    private IPercy percy;

    private CliWrapper cliWrapper;
    public Percy(AppiumDriver driver) {
        this.driver = driver;
        this.cliWrapper = new CliWrapper(driver);
        Boolean isPercyEnabled = cliWrapper.healthcheck();

        if (Environment.getSessionType().equals("automate")) {
            percy = new PercyOnAutomate(driver);
        } else {
            percy = new AppPercy(driver);
        }
    }

    /**
     * Override the client info reported to Percy.
     * Used by framework wrappers (e.g., Cucumber) to identify themselves.
     */
    public void setClientInfo(String clientInfo, String environmentInfo) {
        cliWrapper.getEnvironment().setClientInfo(clientInfo);
        cliWrapper.getEnvironment().setEnvironmentInfo(environmentInfo);
    }

    /**
     * Get the SDK version string.
     */
    public static String getSdkVersion() {
        return Environment.getSdkVersion();
    }

    public JSONObject screenshot(String name) {
        try {
            return percy.screenshot(name);
        } catch (Exception ignore) {
            return null;
        }
    }

    public JSONObject screenshot(String name, Map<String, Object> options) {
        try {
            return percy.screenshot(name, options);
        } catch (Exception ignore) {
            return null;
        }
    }

    public JSONObject screenshot(String name, Boolean fullScreen) {
        try {
            return percy.screenshot(name, fullScreen);
        } catch (Exception ignore) {
            return null;
        }
    }

    public JSONObject screenshot(String name, ScreenshotOptions options) {
        try {
            return percy.screenshot(name, options);
        } catch (Exception ignore) {
            return null;
        }
    }

    public JSONObject screenshot(String name, Boolean fullScreen, ScreenshotOptions options) {
        try {
            return percy.screenshot(name, fullScreen, options);
        } catch (Exception ignore) {
            return null;
        }
    }
}
