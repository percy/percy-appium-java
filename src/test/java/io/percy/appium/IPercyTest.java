package io.percy.appium;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.percy.appium.lib.ScreenshotOptions;

public class IPercyTest {
    private final IPercy percy = new IPercy();

    @Test
    public void screenshotByNameIsNotImplemented() {
        Exception ex = Assert.assertThrows(Exception.class, () -> percy.screenshot("name"));
        Assert.assertEquals("Method not implemented", ex.getMessage());
    }

    @Test
    public void screenshotWithFullScreenIsNotImplemented() {
        Exception ex = Assert.assertThrows(Exception.class, () -> percy.screenshot("name", (Boolean) true));
        Assert.assertEquals("Method not implemented", ex.getMessage());
    }

    @Test
    public void screenshotWithOptionsIsNotImplemented() {
        Exception ex = Assert.assertThrows(Exception.class,
                () -> percy.screenshot("name", (ScreenshotOptions) null));
        Assert.assertEquals("Method not implemented", ex.getMessage());
    }

    @Test
    public void screenshotWithFullScreenAndOptionsIsNotImplemented() {
        Exception ex = Assert.assertThrows(Exception.class,
                () -> percy.screenshot("name", true, (ScreenshotOptions) null));
        Assert.assertEquals("Method not implemented", ex.getMessage());
    }

    @Test
    public void screenshotWithMapOptionsIsNotImplemented() {
        Map<String, Object> options = new HashMap<>();
        Exception ex = Assert.assertThrows(Exception.class, () -> percy.screenshot("name", options));
        Assert.assertEquals("Method not implemented", ex.getMessage());
    }
}
