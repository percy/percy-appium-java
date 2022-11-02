package io.percy.appium.metadata;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.lib.Cache;

public class MetadataHelper {

    public static Metadata resolve(AppiumDriver driver) {
        String driverClass = "";
        try {
            driverClass = driver.getClass().toString();
            if (driverClass.contains("AndroidDriver")) {
                return new AndroidMetadata(driver);
            } else if (driverClass.contains("IOSDriver")) {
                return new IosMetadata(driver);
            } else {
                throw new Exception("Driver class not found");
            }
        } catch (Exception e) {
            AppPercy.log("Unsupported driver class, " + driverClass);
        }
        return null;
    }

    public static Integer valueFromStaticDevicesInfo(String key, String deviceName, String sessionId) {
        try {
            JSONObject object = getDevicesJson(sessionId).getJSONObject(deviceName);
            return Integer.parseInt(object.getString(key));
        } catch (JSONException e) {
            return 0;
        }
    }

    public static JSONObject getDevicesJson(String sessionId) {
        if (Cache.CACHE_MAP.get("getDevicesJson_" + sessionId) == null) {
            InputStream inputStream = MetadataHelper.class.getResourceAsStream("devices.json");
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject devicesJsonObject = new JSONObject(tokener);
            Cache.CACHE_MAP.put("getDevicesJson_" + sessionId, devicesJsonObject);
        }
        return (JSONObject) Cache.CACHE_MAP.get("getDevicesJson_" + sessionId);
    }

    public static JSONObject getSessionDetails(AppiumDriver driver) {
        String sessionId = driver.getSessionId().toString();
        if (Cache.CACHE_MAP.get("getSessionDetails_" + sessionId) == null) {
            String sessionDetails = (String) driver
                    .executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}");
            JSONObject sessionDetailsJsonObject = new JSONObject(sessionDetails);
            Cache.CACHE_MAP.put("getSessionDetails_" + sessionId, sessionDetailsJsonObject);
        }
        return (JSONObject) Cache.CACHE_MAP.get("getSessionDetails_" + sessionId);
    }

}
