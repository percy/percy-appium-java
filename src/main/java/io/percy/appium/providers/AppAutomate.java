package io.percy.appium.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.lib.CliWrapper;
import io.percy.appium.lib.Tile;
import io.percy.appium.metadata.Metadata;
import io.percy.appium.metadata.MetadataHelper;

public class AppAutomate extends GenericProvider {
    private AppiumDriver driver;
    private CliWrapper cliWrapper;
    private Metadata metadata;
    private Boolean markedPercySession = true;

    public AppAutomate(AppiumDriver driver) {
        super(driver);
        this.driver = driver;
        this.cliWrapper = new CliWrapper(driver);
    }

    public String getDebugUrl(JSONObject result) {
        String buildHash = result.getString("buildHash");
        String sessionHash = result.getString("sessionHash");
        return "https://app-automate.browserstack.com/dashboard/v2/builds/" + buildHash + "/sessions/" + sessionHash;
    }

    public static Boolean supports(AppiumDriver driver) {
        String remoteAddress = driver.getRemoteAddress().getHost().toString();
        if (true) {
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

    public String executePercyScreenshot() {
        JSONObject arguments = new JSONObject();
        JSONObject args = new JSONObject();
        args.put("numOfTiles", 5);

        arguments.put("state", "screenshot");
        arguments.put("percyBuildId", System.getenv("PERCY_BUILD_ID"));
        arguments.put("screenshotType", "fullpage");
        arguments.put("scaleFactor", 1);
        arguments.put("options", args);
    
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        System.out.println("asdads");
        System.out.println(reqObject.toString());
        // Scanner sc= new Scanner(System.in);    //System.in is a standard input stream  
        // System.out.print("Enter first number- ");  
        System.out.print(driver.getSessionId().toString()); 
        // int a= sc.nextInt();  
        String resultString = driver
                .executeScript(String.format("browserstack_executor: %s", reqObject.toString())).toString();
        System.out.println("ABCD");
        System.out.println(resultString);
        JSONObject result = new JSONObject(resultString);
        return result.get("result").toString();
    }

    public List<Tile> captureTiles(Boolean fullScreen) throws IOException {
        // Integer statusBar = getMetadata().statBarHeight();
        // Integer navBar = getMetadata().navBarHeight();
        String reqObject = executePercyScreenshot();
        JSONArray jsonarray = new JSONArray(reqObject);
        System.out.println("##########");
        System.out.println(reqObject);
        System.out.println("##########");
        Integer headerHeight = 0;
        Integer footerHeight = 0;
        JSONObject object = new JSONObject(reqObject);
        List<Tile> tiles = new ArrayList<Tile>();
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            String sha = jsonobject.getString("sha");
            System.out.println("sha =" +sha);
            tiles.add(new Tile(null, 0, 0, headerHeight, footerHeight, fullScreen, sha));
        }
        //tiles.add(new Tile(null, statusBar, navBar, headerHeight, footerHeight, fullScreen, "sha"));
        return tiles;
    }

    public String screenshot(String name, String deviceName, Integer statusBarHeight, Integer navBarHeight,
            String orientation, Boolean fullScreen) throws IOException {
        JSONObject result = executePercyScreenshotBegin(name);
        String percyScreenshotUrl = "";
        String error = null;
        String device = deviceName(deviceName, result);
        String platformVersion =result.getString("osVersion").split("\\.")[0];
        this.metadata = MetadataHelper.resolve(driver, device, statusBarHeight, navBarHeight, orientation,
                platformVersion);
        super.setDebugUrl(getDebugUrl(result));
        System.out.println("$$$$$$$$");
        List<Tile> tiles = new ArrayList<>();
        try {
            tiles = captureTiles(fullScreen);
        } catch (Exception e) {
            error = e.getMessage();
            System.out.print("ERRRORRRO" + error);
        }
        JSONObject tag = getTag(this.metadata);
        percyScreenshotUrl = cliWrapper.postScreenshot(name, tag, tiles, "debugUrl");
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
