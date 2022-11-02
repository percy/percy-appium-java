package io.percy.appium.providers;

import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.metadata.Metadata;
import io.percy.appium.metadata.MetadataHelper;

public class AppAutomate extends GenericProvider {
    private AppiumDriver driver;
    private Boolean markedPercySession = true;

    public AppAutomate(AppiumDriver driver, Metadata metadata) {
        super(driver, metadata);
        this.driver = driver;
    }

    public String getDebugUrl() {
        return MetadataHelper.getSessionDetails(driver).get("browser_url").toString();
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

}
