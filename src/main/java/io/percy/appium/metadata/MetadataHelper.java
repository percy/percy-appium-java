package io.percy.appium.metadata;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;

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

    public static Integer parsedJsonValue(String key, String deviceName) {
        try {
            InputStream inputStream = new FileInputStream(
                    "src/main/java/io/percy/appium/metadata/ios_devices.json");
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject object = new JSONObject(tokener).getJSONObject(deviceName);
            return Integer.parseInt(object.getString(key));
        } catch (FileNotFoundException e) {
            AppPercy.log("Json file missing");
        } catch (JSONException e) {
            return 0;
        }
        return 0;
    }

}
