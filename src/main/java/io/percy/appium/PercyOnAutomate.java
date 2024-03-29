package io.percy.appium;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.lib.CliWrapper;
import io.percy.appium.lib.PercyOptions;
import io.percy.appium.metadata.DriverMetadata;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.Map;

import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class PercyOnAutomate extends IPercy {
    private final AppiumDriver driver;
    private  final DriverMetadata driverMetadata;
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
        this.driverMetadata = new DriverMetadata(driver);
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
    public JSONObject screenshot(String name) {
        return this.screenshot(name, (Map<String, Object>) null);
    }

    /**
     * Take a screenshot and upload it to Percy.
     *
     * @param name       The human-readable name of the screenshot. Should be
     *                   unique.
     * @param options    Optional screenshot params
     */
    @Override
    public JSONObject screenshot(String name, Map<String, Object> options) {
        try {
            if (isPercyEnabled == null) {
                this.isPercyEnabled = this.cliWrapper.healthcheck();
            }
            if (!isPercyEnabled || !percyOptions.percyOptionEnabled()) {
                return null;
            }

            String sessionId = this.driverMetadata.getSessionId();
            String remoteWebAddress = this.driverMetadata.getCommandExecutorUrl();
            Map<String, Object> capabilities = this.driverMetadata.getCapabilities();

            String ignoreElementKey = "ignore_region_appium_elements";
            String ignoreElementAltKey = "ignoreRegionAppiumElements";
            String considerElementKey = "consider_region_appium_elements";
            String considerElementAltKey = "considerRegionAppiumElements";

            if (options != null) {
                if (options.containsKey(ignoreElementAltKey)) {
                    options.put(ignoreElementKey, options.get(ignoreElementAltKey));
                    options.remove(ignoreElementAltKey);
                }

                if (options.containsKey(considerElementAltKey)) {
                    options.put(considerElementKey, options.get(considerElementAltKey));
                    options.remove(considerElementAltKey);
                }

                if (options.containsKey(ignoreElementKey)) {
                    List<String> ignoreElementIds =
                            getElementIdFromElement((List<RemoteWebElement>) options.get(ignoreElementKey));
                    options.remove(ignoreElementKey);
                    options.put("ignore_region_elements", ignoreElementIds);
                }

                if (options.containsKey(considerElementKey)) {
                    List<String> considerElementIds =
                            getElementIdFromElement((List<RemoteWebElement>) options.get(considerElementKey));
                    options.remove(considerElementKey);
                    options.put("consider_region_elements", considerElementIds);
                }
            }

            JSONObject response = cliWrapper.postScreenshotPOA(
                name,
                sessionId,
                remoteWebAddress,
                capabilities,
                options
            );

            if (response != null && response.has("data")) {
                return response.getJSONObject("data");
            }
            return null;
        } catch (Exception e) {
            AppPercy.log("Error taking screenshot " + name);
            AppPercy.log(e.toString());
            if (!ignoreErrors) {
                throw new RuntimeException("Error taking screenshot " + name, e);
            }
            return null;
        }
    }

    private List<String> getElementIdFromElement(List<RemoteWebElement> elements) {
        List<String> ignoredElementsArray = new ArrayList<>();
        for (RemoteWebElement element : elements) {
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
