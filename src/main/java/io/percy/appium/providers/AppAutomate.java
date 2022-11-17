package io.percy.appium.providers;

import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.lib.Cache;
import io.percy.appium.metadata.Metadata;

public class AppAutomate extends GenericProvider {
    private AppiumDriver driver;
    private Boolean markedPercySession = true;
    private static String sessionId;

    public AppAutomate(AppiumDriver driver, Metadata metadata) {
        super(driver, metadata);
        this.driver = driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public String getDebugUrl() {
        return getSessionDetails(driver).get("browser_url").toString();
    }

    public static Boolean supports(AppiumDriver driver) {
        if (driver.getRemoteAddress().getHost().toString().contains("browserstack")) {
            return true;
        }
        return false;
    }

    public void executePercyScreenshotBegin(String name) {
        try {
            if (markedPercySession) {
                JSONObject arguments = new JSONObject();
                arguments.put("state", "begin");
                arguments.put("percyBuildId", System.getenv("PERCY_BUILD_ID"));
                arguments.put("percyBuildUrl", System.getenv("PERCY_BUILD_URL"));
                arguments.put("name", name);
                JSONObject reqObject = new JSONObject();
                reqObject.put("action", "percyScreenshot");
                reqObject.put("arguments", arguments);
                String resultString = driver
                        .executeScript(String.format("browserstack_executor: %s", reqObject.toString())).toString();
                JSONObject result = new JSONObject(resultString);
                markedPercySession = result.get("success").toString() == "true";
            }
        } catch (Exception e) {
            AppPercy.log("BrowserStack executer failed");
        }
    }

    public void executePercyScreenshotEnd(String name, String percyScreenshotUrl, String error) {
        try {
            if (markedPercySession) {
                String status = "success";
                if (error != null) {
                    status = "Failed: " + error;
                }
                JSONObject arguments = new JSONObject();
                arguments.put("state", "end");
                arguments.put("percyScreenshotUrl", percyScreenshotUrl);
                arguments.put("name", name);
                arguments.put("status", status);
                JSONObject reqObject = new JSONObject();
                reqObject.put("action", "percyScreenshot");
                reqObject.put("arguments", arguments);
                String resultString = driver
                        .executeScript(String.format("browserstack_executor: %s", reqObject.toString())).toString();
                JSONObject result = new JSONObject(resultString);
                markedPercySession = result.get("success").toString() == "true";
            }
        } catch (Exception e) {
            AppPercy.log("BrowserStack executer failed");
        }
    }

    public String screenshot(String name, String deviceName, Integer statusBarHeight, Integer navBarHeight,
            String orientation, Boolean fullScreen, String debugUrl) {
        executePercyScreenshotBegin(name);
        String percyScreenshotUrl = "";
        String error = null;
        try {
            percyScreenshotUrl = super.screenshot(name, deviceName, statusBarHeight, navBarHeight, orientation,
            fullScreen, debugUrl);
        } catch (Exception e) {
            error = e.getMessage();
        }
        executePercyScreenshotEnd(name, percyScreenshotUrl, error);
        return null;
    }

    public static JSONObject getSessionDetails(AppiumDriver driver) {
        if (Cache.CACHE_MAP.get("getSessionDetails_" + sessionId) == null) {
            String sessionDetails = (String) driver
                    .executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}");
            JSONObject sessionDetailsJsonObject = new JSONObject(sessionDetails);
            Cache.CACHE_MAP.put("getSessionDetails_" + sessionId, sessionDetailsJsonObject);
        }
        return (JSONObject) Cache.CACHE_MAP.get("getSessionDetails_" + sessionId);
    }

}
