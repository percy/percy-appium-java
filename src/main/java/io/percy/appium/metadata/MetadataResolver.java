package io.percy.appium.metadata;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;

public class MetadataResolver {

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

}
