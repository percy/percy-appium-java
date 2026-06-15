package io.percy.appium.lib;

import java.util.Map;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;

public class PercyOptions {
    private AppiumDriver driver;
    private String sessionId;

    public PercyOptions(AppiumDriver driver) {
        this.driver = driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public Boolean percyOptionEnabled() {
        Map percyOptionsW3CProtocol = getPercyOptions();
        Object percyEnabledJsonProtocol = driver.getCapabilities().getCapability("percy.enabled");
        if (percyOptionsW3CProtocol == null && percyEnabledJsonProtocol == null) {
            AppPercy.log("Percy options not provided in capabilitiies, considering enabled", "debug");
            return true;
        } else if ((percyEnabledJsonProtocol != null && "false".equals(percyEnabledJsonProtocol.toString()))
                || (percyOptionsW3CProtocol != null
                        && "false".equals(percyOptionsW3CProtocol.get("enabled").toString()))) {
            AppPercy.log("App Percy is disabled in capabilities");
            return false;
        }
        return true;
    }

    public Boolean setPercyIgnoreErrors() {
        Map percyOptionsW3CProtocol = getPercyOptions();
        Object percyIgnoreErrorsJsonProtocol = driver.getCapabilities().getCapability("percy.ignoreErrors");
        if (percyOptionsW3CProtocol == null && percyIgnoreErrorsJsonProtocol == null) {
            AppPercy.log("Percy options not provided in capabilitiies, ignoring errors by default", "debug");
            return true;
        } else if ((percyIgnoreErrorsJsonProtocol != null && "false".equals(percyIgnoreErrorsJsonProtocol.toString()))
                || (percyOptionsW3CProtocol != null
                        && "false".equals(percyOptionsW3CProtocol.get("ignoreErrors").toString()))) {
            return false;
        }
        return true;
    }

    private Map getPercyOptions() {
        if (Cache.CACHE_MAP.get("percyOptions_" + sessionId) == null) {
            Cache.CACHE_MAP.put("percyOptions_" + sessionId, driver.getCapabilities().getCapability("percyOptions"));
        }
        return (Map) Cache.CACHE_MAP.get("percyOptions_" + sessionId);
    }

}
