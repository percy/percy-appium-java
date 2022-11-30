package io.percy.appium.providers;

import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.lib.Cache;

public class AppAutomate extends GenericProvider {
    private AppiumDriver driver;
    private Boolean markedPercySession = true;
    private static String sessionId;

    public AppAutomate(AppiumDriver driver) {
        super(driver);
        this.driver = driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public String getDebugUrl() {
        String buildHash = getSessionDetails().get("buildHash").toString();
        String sessionHash = getSessionDetails().get("sessionHash").toString();
        return "https://app-automate.browserstack.com/dashboard/v2/builds/" + buildHash + "/sessions/" + sessionHash;
    }

    public static Boolean supports(AppiumDriver driver) {
        String remoteAddress = driver.getRemoteAddress().getHost().toString();
        if (remoteAddress.contains(System.getenv("AA_DOMAIN") != null ? System.getenv("AA_DOMAIN") : "browserstack")) {
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
                setSessionDetails(result);
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
        String device = deviceName(deviceName);
        try {
            percyScreenshotUrl = super.screenshot(name, device, statusBarHeight, navBarHeight, orientation,
                    fullScreen, debugUrl, platformVersion());
        } catch (Exception e) {
            error = e.getMessage();
        }
        executePercyScreenshotEnd(name, percyScreenshotUrl, error);
        return null;
    }

    public String deviceName(String deviceName) {
        if (deviceName != null) {
            return deviceName;
        }
        return getSessionDetails().get("deviceName").toString();
    }

    public String platformVersion() {
        return getSessionDetails().get("osVersion").toString().split("\\.")[0];
    }

    public static void setSessionDetails(JSONObject jsonObject) {
        if (Cache.CACHE_MAP.get("getSessionDetails_" + sessionId) == null) {
            Cache.CACHE_MAP.put("getSessionDetails_" + sessionId, jsonObject);
        }
    }

    public static JSONObject getSessionDetails() {
        return (JSONObject) Cache.CACHE_MAP.get("getSessionDetails_" + sessionId);
    }

}
