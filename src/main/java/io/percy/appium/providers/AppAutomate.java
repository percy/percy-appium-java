package io.percy.appium.providers;

import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.lib.Cache;
import io.percy.appium.metadata.Metadata;

public class AppAutomate extends GenericProvider {
    private AppiumDriver driver;
    private Boolean markedPercySession = true;
    private String sessionId;

    public AppAutomate(AppiumDriver driver, Metadata metadata) {
        super(driver, metadata);
        this.driver = driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public String getDebugUrl() {
        JSONObject sessionDetails = new JSONObject(getSessionDetails());
        return sessionDetails.get("browser_url").toString();
    }

    public static Boolean supports(AppiumDriver driver) {
        if (driver.getRemoteAddress().getHost().toString().contains("browserstack")) {
            return true;
        }
        return false;
    }

    public void executePercyScreenshotBegin() {
        try {
            if (markedPercySession) {
                String resultString = driver.executeScript(String.format(
                        "browserstack_executor: {\"action\": \"percyScreenshot\", \"arguments\": {\"state\": \"begin\", \"percyBuildId\": \"{%s}\", \"percyBuildUrl\": \"{%s}\"}}",
                        System.getenv("PERCY_BUILD_ID"), System.getenv("PERCY_BUILD_URL"))).toString();
                JSONObject result = new JSONObject(resultString);
                markedPercySession = result.get("success").toString() == "true";
            }
        } catch (Exception e) {
            AppPercy.log("BrowserStack executer failed");
        }
    }

    public void executePercyScreenshotEnd(String percyScreenshotUrl) {
        try {
            if (markedPercySession) {
                String resultString = driver.executeScript(String.format(
                        "browserstack_executor: {\"action\": \"percyScreenshot\", \"arguments\": {\"state\": \"end\", \"percyScreenshotUrl\": \"{%s}\"}}",
                        percyScreenshotUrl)).toString();
                JSONObject result = new JSONObject(resultString);
                markedPercySession = result.get("success").toString() == "true";
            }
        } catch (Exception e) {
            AppPercy.log("BrowserStack executer failed");
        }
    }

    public String screenshot(String name, Boolean fullScreen, String debugUrl) {
        executePercyScreenshotBegin();
        String percyScreenshotUrl = super.screenshot(name, fullScreen, debugUrl);
        executePercyScreenshotEnd(percyScreenshotUrl);
        return null;
    }

    private String getSessionDetails() {
        if (Cache.CACHE_MAP.get("getSessionDetails_" + sessionId) == null) {
            Cache.CACHE_MAP.put("getSessionDetails_" + sessionId,
                    driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"));
        }
        return Cache.CACHE_MAP.get("getSessionDetails_" + sessionId).toString();
    }

}
