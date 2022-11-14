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

    public static Integer valueFromStaticDevicesInfo(String key, String deviceName) {
        try {
            JSONObject object = getDevicesJson().getJSONObject(deviceName);
            return object.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    public static JSONObject getDevicesJson() {
        if (Cache.CACHE_MAP.get("getDevicesJson") == null) {
            InputStream inputStream = MetadataHelper.class.getResourceAsStream("/devices.json");
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject devicesJsonObject = new JSONObject(tokener);
            Cache.CACHE_MAP.put("getDevicesJson", devicesJsonObject);
        }
        return (JSONObject) Cache.CACHE_MAP.get("getDevicesJson");
    }

}
