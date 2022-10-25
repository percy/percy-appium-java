package io.percy.appium.providers;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.metadata.Metadata;
import io.percy.appium.metadata.MetadataResolver;

public class ProviderResolver {

    public static GenericProvider resolveProvider(AppiumDriver driver) {
        Metadata metadata = MetadataResolver.resolve(driver);
        if (AppAutomate.supports(driver)) {
            return new AppAutomate(driver, metadata);
        } else {
            return new GenericProvider(driver, metadata);
        }
    }

}
