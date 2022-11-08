package io.percy.appium;

import java.util.HashSet;
import java.util.Set;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.lib.Cache;
import io.percy.appium.lib.CliWrapper;
import io.percy.appium.lib.PercyOptions;
import io.percy.appium.lib.ScreenshotOptions;
import io.percy.appium.providers.GenericProvider;
import io.percy.appium.providers.ProviderResolver;

/**
 * Percy client for visual testing.
 */
public class AppPercy {
    /**
     * Appium Driver we'll use for accessing the apps to screenshot.
     */
    private AppiumDriver driver;

    private CliWrapper cliWrapper;

    private PercyOptions percyOptions;

    /**
     * Determine if we're debug logging
     */
    private static boolean PERCY_DEBUG = System.getenv().getOrDefault("PERCY_LOGLEVEL", "info").equals("debug");

    /**
     * for logging
     */
    private static String LABEL = "[\u001b[35m" + (PERCY_DEBUG ? "percy:java" : "percy") + "\u001b[39m]";

    /**
     * Is the Percy server running or not
     */
    private boolean isPercyEnabled;

    private String sessionId;

    private static Boolean ignoreErrors = true;

    /**
     * @param driver The Appium Driver object that will hold the app session to
     *               screenshot.
     */
    public AppPercy(AppiumDriver driver) {
        this.driver = driver;
        this.cliWrapper = new CliWrapper(driver);
        this.percyOptions = new PercyOptions(driver);
        this.isPercyEnabled = cliWrapper.healthcheck();
        this.sessionId = driver.getSessionId().toString();
    }

    /**
     * Take a screenshot and upload it to Percy.
     *
     * @param name The human-readable name of the screenshot. Should be unique.
     *
     */
    public void screenshot(String name) {
        screenshot(name, false, null);
    }

    /**
     * Take a screenshot and upload it to Percy.
     *
     * @param name       The human-readable name of the screenshot. Should be
     *                   unique.
     * @param fullScreen It indicates if the app is a full screen
     */
    public void screenshot(String name, Boolean fullScreen) {
        screenshot(name, fullScreen, null);
    }

    /**
     * Take a screenshot and upload it to Percy.
     *
     * @param name            The human-readable name of the screenshot. Should be
     *                        unique.
     * @param options         Optional screenshot params
        * @param deviceName      Device name on which screenshot is taken
        * @param statusBarHeight Height of status bar for the device
        * @param navBarHeight    Height of navigation bar for the device
        * @param orientation     Orientation of the application
     */
    public void screenshot(String name, ScreenshotOptions options) {
        screenshot(name, false, options);
    }

    /**
     * Take a screenshot and upload it to Percy.
     *
     * @param name            The human-readable name of the screenshot. Should be
     *                        unique.
     * @param fullScreen      It indicates if the app is a full screen
     * @param options         Optional screenshot params
        * @param deviceName      Device name on which screenshot is taken
        * @param statusBarHeight Height of status bar for the device
        * @param navBarHeight    Height of navigation bar for the device
        * @param orientation     Orientation of the application
     */
    public void screenshot(String name, Boolean fullScreen, ScreenshotOptions options) {
        if (!isPercyEnabled || !percyOptions.percyOptionEnabled()) {
            return;
        }
        percyOptions.setPercyIgnoreErrors();
        try {
            GenericProvider provider = ProviderResolver.resolveProvider(driver);
            if (options != null) {
                provider.screenshot(name, options.getDeviceName(), options.getStatusBarHeight(), options.getNavBarHeight(),
                        options.getOrientation(), fullScreen, provider.getDebugUrl());
            } else {
                provider.screenshot(name, null, null, null, null, fullScreen, provider.getDebugUrl());
            }
        } catch (Exception e) {
            log("Error taking screenshot " + name);
            log(e.toString(), "debug");
            if (!ignoreErrors) {
                throw new RuntimeException("Error taking screenshot " + name);
            }
        }
    }

    protected void finalize() throws Throwable {
        Set<String> set = new HashSet<>();
        set.add("getSessionDetails_" + sessionId);
        set.add("percyOptions_" + sessionId);
        set.add("systemBars_" + sessionId);
        set.add("viewportRect_" + sessionId);
        set.add("viewportRectFallback_" + sessionId);
        set.add("windowSize_" + sessionId);
        set.add("getDevicesJson");
        Cache.CACHE_MAP.keySet().removeAll(set);
    }

    public static void log(String message) {
        log(message, "info");
    }

    public static void log(String message, String logLevel) {
        if (logLevel == "debug" && PERCY_DEBUG) {
            System.out.println(LABEL + " " + message);
        } else if (logLevel == "info") {
            System.out.println(LABEL + " " + message);
        }
    }

}
