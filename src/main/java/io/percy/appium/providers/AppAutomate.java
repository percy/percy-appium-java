package io.percy.appium.providers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.lib.ScreenshotOptions;
import io.percy.appium.lib.Tile;
public class AppAutomate extends GenericProvider {
    private AppiumDriver driver;
    private Boolean markedPercySession = true;

    public AppAutomate(AppiumDriver driver) {
        super(driver);
        this.driver = driver;
    }

    public String getDebugUrl(JSONObject result) {
        String buildHash = result.getString("buildHash");
        String sessionHash = result.getString("sessionHash");
        return "https://app-automate.browserstack.com/dashboard/v2/builds/" + buildHash + "/sessions/" + sessionHash;
    }

    public static Boolean supports(AppiumDriver driver) {
        String remoteAddress = driver.getRemoteAddress().getHost().toString();
        if (remoteAddress.contains(System.getenv("AA_DOMAIN") != null ? System.getenv("AA_DOMAIN") : "browserstack")) {
            return true;
        }
        return false;
    }

    public JSONObject executePercyScreenshotBegin(String name) {
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
                return result;
            }
        } catch (Exception e) {
            AppPercy.log("BrowserStack executer failed");
        }
        return null;
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

    public String executePercyScreenshot(ScreenshotOptions options, Integer scaleFactor,
        Integer deviceHeight) throws Exception {
        try {
            JSONObject arguments = new JSONObject();
            JSONObject args = new JSONObject();
            args.put("numOfTiles", options.getScreenLengths());
            args.put("deviceHeight", deviceHeight);
            arguments.put("state", "screenshot");
            arguments.put("percyBuildId", System.getenv("PERCY_BUILD_ID"));
            arguments.put("screenshotType", "fullpage");
            arguments.put("scaleFactor", scaleFactor);
            arguments.put("options", args);
            JSONObject reqObject = new JSONObject();
            reqObject.put("action", "percyScreenshot");
            reqObject.put("arguments", arguments);
            String resultString = driver
                    .executeScript(String.format("browserstack_executor: %s", reqObject.toString())).toString();
            JSONObject result = new JSONObject(resultString);
            return result.get("result").toString();
        } catch (Exception e) {
            throw new Exception("Screenshot command failed");
        }
    }

    public List<Tile> captureTiles(Boolean fullScreen, ScreenshotOptions options) throws Exception {
        if (!options.getFullpageScreenshot()) {
            return super.captureTiles(fullScreen, options);
        }

        Integer statusBar = getMetadata().statBarHeight();
        Integer navBar = getMetadata().navBarHeight();
        String response = executePercyScreenshot(options, getMetadata().scaleFactor(),
            getMetadata().deviceScreenHeight());
        JSONArray jsonarray = new JSONArray(response);
        List<Tile> tiles = new ArrayList<Tile>();
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            String sha = jsonobject.getString("sha");
            Integer headerHeight = jsonobject.getInt("header_height");
            Integer footerHeight = jsonobject.getInt("footer_height");
            tiles.add(new Tile(null, statusBar, navBar, headerHeight, footerHeight, fullScreen, sha));
        }
        return tiles;
    }

    public String screenshot(String name, ScreenshotOptions options, Boolean fullScreen) {
        JSONObject result = executePercyScreenshotBegin(name);
        String percyScreenshotUrl = "";
        String error = null;
        String device = deviceName(options.getDeviceName(), result);
        super.setDebugUrl(getDebugUrl(result));
        try {
            percyScreenshotUrl = super.screenshot(name, options, fullScreen,
                result.getString("osVersion").split("\\.")[0], device);
        } catch (Exception e) {
            error = e.getMessage();
        }
        executePercyScreenshotEnd(name, percyScreenshotUrl, error);
        return null;
    }


    public String deviceName(String deviceName, JSONObject result) {
        if (deviceName != null) {
            return deviceName;
        }
        return result.getString("deviceName");
    }

}
