package io.percy.appium.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.Environment;
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
        if (result == null) {
            return null;
        }

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
                arguments.put("percyBuildId", Environment.getPercyBuildID());
                arguments.put("percyBuildUrl", Environment.getPercyBuildUrl());
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

    public void executePercyScreenshotEnd(String name, String percyScreenshotUrl, String error, Boolean sync) {
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
                arguments.put("sync", sync);
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
            String screenshotType = "fullpage";
            if (!options.getFullPage() || !verifyCorrectAppiumVersion()) {
                screenshotType = "singlepage";
            }
            String projectId = "percy-prod";
            if (Environment.getEnablePercyDev()) {
                projectId = "percy-dev";
            }
            JSONObject arguments = new JSONObject();
            JSONObject args = new JSONObject();
            args.put("numOfTiles", options.getScreenLengths());
            args.put("deviceHeight", deviceHeight);
            args.put("scollableXpath", options.getScrollableXpath());
            args.put("scrollableId", options.getScrollableId());
            args.put("topScrollviewOffset", options.getTopScrollviewOffset());
            args.put("bottomScrollviewOffset", options.getBottomScrollviewOffset());
            args.put("FORCE_FULL_PAGE", Environment.getForceFullPage());
            arguments.put("state", "screenshot");
            arguments.put("percyBuildId", Environment.getPercyBuildID());
            arguments.put("screenshotType", screenshotType);
            arguments.put("scaleFactor", scaleFactor);
            arguments.put("projectId", projectId);
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

    public List<Tile> captureTiles(ScreenshotOptions options) throws Exception {
        if (Environment.getDisableRemoteUploads()) {
            if (options.getFullPage()) {
                AppPercy.log(
                "Full page screenshots are only supported when \"PERCY_DISABLE_REMOTE_UPLOADS\" is not set",
                "warn"
                );
            }
            return super.captureTiles(options);
        }

        Integer statusBar = getMetadata().statBarHeight();
        Integer navBar = getMetadata().navBarHeight();
        String response = executePercyScreenshot(options, getMetadata().scaleFactor(),
                getMetadata().deviceScreenHeight());
        JSONArray jsonarray = new JSONArray(response);
        List<Tile> tiles = new ArrayList<Tile>();
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            String sha = jsonobject.getString("sha").split("-")[0];
            Integer headerHeight = jsonobject.getInt("header_height");
            Integer footerHeight = jsonobject.getInt("footer_height");
            tiles.add(new Tile(null, statusBar, navBar, headerHeight, footerHeight, options.getFullScreen(), sha));
        }
        return tiles;
    }

    public JSONObject screenshot(String name, ScreenshotOptions options) {
        JSONObject result = executePercyScreenshotBegin(name);
        String percyScreenshotUrl = "";
        String error = null;
        String device = deviceName(options.getDeviceName(), result);
        String osVersion = osVersion(result);

        super.setDebugUrl(getDebugUrl(result));
        try {
            JSONObject response = super.screenshot(name, options, osVersion, device);
            percyScreenshotUrl = response.getString("link");
            return response;
        } catch (Exception e) {
            error = e.getMessage();
        }
        executePercyScreenshotEnd(name, percyScreenshotUrl, error, options.getSync());
        return null;
    }

    public String osVersion(JSONObject result) {
        return result != null ? result.getString("osVersion").split("\\.")[0] : null;
    }

    public String deviceName(String deviceName, JSONObject result) {
        if (deviceName != null) {
            return deviceName;
        }
        return result != null ? result.getString("deviceName") : null;
    }

    public Boolean verifyCorrectAppiumVersion() {
        Map bstackOptions = (Map) driver.getCapabilities().getCapability("bstack:options");
        Object appiumVersionJsonProtocol = driver.getCapabilities().getCapability("browserstack.appium_version");
        if (bstackOptions == null && appiumVersionJsonProtocol == null) {
            AppPercy.log("Unable to fetch Appium version, "
                    + "Appium version should be >= 1.19 for Fullpage Screenshot", "warn");
        } else if ((appiumVersionJsonProtocol != null && !appiumVersionCheck(appiumVersionJsonProtocol.toString()))
                || (bstackOptions != null && !appiumVersionCheck(bstackOptions.get("appiumVersion").toString()))) {
            AppPercy.log("Appium version should be >= 1.19 for Fullpage Screenshot, "
                    + "Falling back to single page screenshot.", "warn");
            return false;
        }
        return true;
    }

    private Boolean appiumVersionCheck(String appiumVersion) {
        Integer majorVersion = Integer.parseInt(appiumVersion.split("\\.")[0]);
        Integer minorVersion = Integer.parseInt(appiumVersion.split("\\.")[1]);
        if (majorVersion == 2 || (majorVersion == 1 && minorVersion > 18)) {
            return true;
        }
        return false;
    }

}
