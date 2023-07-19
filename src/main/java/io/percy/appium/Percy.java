package io.percy.appium;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.lib.CliWrapper;
import io.percy.appium.lib.ScreenshotOptions;

import java.util.Map;

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

    public void screenshot(String name) {
        try {
            percy.screenshot(name);
        } catch (Exception ignore) {

        }
    }

    public void screenshot(String name, Map<String, Object> options) {
        try {
            percy.screenshot(name, options);
        } catch (Exception ignore) {

        }
    }

    public void screenshot(String name, Boolean fullScreen) {
        try {
            percy.screenshot(name, fullScreen);
        } catch (Exception ignore) {

        }
    }

    public void screenshot(String name, ScreenshotOptions options) {
        try {
            percy.screenshot(name, options);
        } catch (Exception ignore) {

        }
    }

    public void screenshot(String name, Boolean fullScreen, ScreenshotOptions options) {
        try {
            percy.screenshot(name, fullScreen, options);
        } catch (Exception ignore) {

        }
    }
}
