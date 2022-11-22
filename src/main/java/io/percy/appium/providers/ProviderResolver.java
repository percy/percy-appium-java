package io.percy.appium.providers;

import io.appium.java_client.AppiumDriver;

public class ProviderResolver {

    public static GenericProvider resolveProvider(AppiumDriver driver) {
        if (AppAutomate.supports(driver)) {
            return new AppAutomate(driver);
        } else {
            return new GenericProvider(driver);
        }
    }

}
