package io.percy.appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.percy.appium.lib.CliWrapper;
import io.percy.appium.lib.PercyOptions;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class PercyOnAutomate extends IPercy {
    private final AppiumDriver driver;
    private CliWrapper cliWrapper;
    private final PercyOptions percyOptions;
    /**
     * Is the Percy server running or not
     */
    private Boolean isPercyEnabled = null;
    private static Boolean ignoreErrors = true;

    /**
     * @param driver The Appium Driver object that will hold the session to
     *               screenshot.
     */
    public PercyOnAutomate(AppiumDriver driver) {
        this.driver = driver;
        this.cliWrapper = new CliWrapper(driver);
        this.percyOptions = new PercyOptions(driver);
        ignoreErrors = percyOptions.setPercyIgnoreErrors();
    }

    /**
     * Take a screenshot and upload it to Percy.
     *
     * @param name The human-readable name of the screenshot. Should be unique.
     *
     */
    @Override
    public void screenshot(String name) {
        this.screenshot(name, (Map<String, Object>) null);
    }

    /**
     * Take a screenshot and upload it to Percy.
     *
     * @param name       The human-readable name of the screenshot. Should be
     *                   unique.
     * @param options    Optional screenshot params
     */
    @Override
    public void screenshot(String name, Map<String, Object> options) {
        try {
            if (isPercyEnabled == null) {
                this.isPercyEnabled = this.cliWrapper.healthcheck();
            }
            if (!isPercyEnabled || !percyOptions.percyOptionEnabled()) {
                return;
            }

            String sessionId = driver.getSessionId().toString();
            String remoteWebAddress = driver.getRemoteAddress().toString();
            Map<String, Object> capabilities = driver.getCapabilities().asMap();

            String ignoreElementKey = "ignore_region_appium_elements";
            String considerElementKey = "consider_region_appium_elements";
            if (options != null) {
                if (options.containsKey(ignoreElementKey)) {
                    List<String> ignoreElementIds =
                            getElementIdFromElement((List<MobileElement>) options.get(ignoreElementKey));
                    options.remove(ignoreElementKey);
                    options.put("ignore_region_elements", ignoreElementIds);
                }

                if (options.containsKey(considerElementKey)) {
                    List<String> considerElementIds =
                            getElementIdFromElement((List<MobileElement>) options.get(considerElementKey));
                    options.remove(considerElementKey);
                    options.put("consider_region_elements", considerElementIds);
                }
            }

            cliWrapper.postScreenshotPOA(name, sessionId, remoteWebAddress, capabilities, options);
        } catch (Exception e) {
            AppPercy.log("Error taking screenshot " + name);
            AppPercy.log(e.toString());
            if (!ignoreErrors) {
                throw new RuntimeException("Error taking screenshot " + name, e);
            }
        }
    }

    private List<String> getElementIdFromElement(List<MobileElement> elements) {
        List<String> ignoredElementsArray = new ArrayList<>();
        for (MobileElement element : elements) {
            String elementId = element.getId();
            ignoredElementsArray.add(elementId);
        }
        return ignoredElementsArray;
    }
    // Following method added for test cases.
    protected void setCliWrapper(CliWrapper cli) {
        this.cliWrapper = cli;
    }
}
