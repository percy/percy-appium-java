package io.percy.appium.metadata;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Cache;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.SessionId;

import com.github.javafaker.Faker;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class AndroidMetadataTest {
    @Mock
    AndroidDriver androidDriver;

    @Mock
    Capabilities capabilities;

    AndroidMetadata metadata;

    Faker faker = new Faker();
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);

    HashMap<String, Long> viewportRect = new HashMap<String, Long>();

    @Before
    public void setup() {
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("deviceScreenSize")).thenReturn("1080x2160");
        when(capabilities.getCapability("viewportRect")).thenReturn(viewportRect);
        metadata = new AndroidMetadata(androidDriver, null, null, null, null, null);
    }

    @After
    public void clearCache() {
        Cache.CACHE_MAP.clear();
    }

    @Test
    public void testDeviceScreenWidth() {
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 1080);
    }

    @Test
    public void testDeviceScreenHeight() {
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), 2160);
    }

    @Test
    public void testStatBarHeight() {
        Assert.assertEquals(metadata.statBarHeight().intValue(), top.intValue());
    }

    @Test
    public void testStatBarHeightWhenValueGiven() {
        metadata = new AndroidMetadata(androidDriver, "Samsung Galaxy s22", 100, 200, "auto", null);
        Assert.assertEquals(metadata.statBarHeight().intValue(), 100);
    }

    @Test
    public void testStatBarHeightForAuto() {
        metadata = new AndroidMetadata(androidDriver, "Samsung Galaxy s22", null, null, "auto", null);
        JSONObject arguments = new JSONObject();
        arguments.put("action", "adbShell");
        JSONObject command = new JSONObject();
        command.put("command", "dumpsys window displays");
        arguments.put("arguments", command);

        String response = "InsetsSource type=ITYPE_STATUS_BAR frame=[0,0][2400,74] visible=true\n" +
                "InsetsSource type=ITYPE_NAVIGATION_BAR frame=[0,2358][1080,2400] visible=true";

        when(androidDriver.executeScript(String.format("browserstack_executor: %s", arguments.toString())))
                .thenReturn(response);

        Integer expectedStatBarHeight = 74;
        Integer actualStatBarHeight = metadata.statBarHeight();
        Assert.assertEquals(expectedStatBarHeight, actualStatBarHeight);
    }

    @Test
    public void testNavBarHeight() {
        Assert.assertEquals(metadata.navBarHeight().intValue(), 2160 - (height + top));
    }

    @Test
    public void testNavBarHeightWhenValueGiven() {
        metadata = new AndroidMetadata(androidDriver, "Samsung Galaxy s22", 100, 200, "auto", null);
        Assert.assertEquals(metadata.navBarHeight().intValue(), 200);
    }

    @Test
    public void testNavBarHeightForAuto() {
        metadata = new AndroidMetadata(androidDriver, "Samsung Galaxy s22", null, null, "auto", null);
        JSONObject arguments = new JSONObject();
        arguments.put("action", "adbShell");
        JSONObject command = new JSONObject();
        command.put("command", "dumpsys window displays");
        arguments.put("arguments", command);

        String response = "InsetsSource type=ITYPE_STATUS_BAR frame=[0,0][2400,74] visible=true\n" +
                "InsetsSource type=ITYPE_NAVIGATION_BAR frame=[0,2358][1080,2400] visible=true";

        when(androidDriver.executeScript(String.format("browserstack_executor: %s", arguments.toString())))
                .thenReturn(response);

        Integer expectedStatBarHeight = 42;
        Integer actualStatBarHeight = metadata.navBarHeight();
        Assert.assertEquals(expectedStatBarHeight, actualStatBarHeight);
    }

    @Test
    public void testDeviceName() {
        when(capabilities.getCapability("device")).thenReturn("Samsung Galaxy s22");
        Assert.assertEquals(metadata.deviceName(), "Samsung Galaxy s22");
    }

    @Test
    public void testDeviceNameFromDesired() {
        Map desired = new HashMap<>();
        desired.put("deviceName", "Samsung Galaxy s22");
        when(capabilities.getCapability("desired")).thenReturn(desired);
        Assert.assertEquals(metadata.deviceName(), "Samsung Galaxy s22");
    }

    @Test
    public void testOsName() {
        when(capabilities.getCapability("platformName")).thenReturn("Android");
        Assert.assertEquals(metadata.osName(), "Android");
    }

    @Test
    public void testPlatformVersion() {
        when(capabilities.getCapability("platformVersion")).thenReturn("12");
        Assert.assertEquals(metadata.platformVersion(), "12");
    }

    @Test
    public void testExplicitlyProvidedParams() {
        metadata = new AndroidMetadata(androidDriver, "Samsung Galaxy s22", 100, 200, "landscape", null);
        Assert.assertEquals(metadata.deviceName(), "Samsung Galaxy s22");
        Assert.assertEquals(metadata.statBarHeight().intValue(), 100);
        Assert.assertEquals(metadata.navBarHeight().intValue(), 200);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }

    @Test
    public void testOrientatioWithPortrait() {
        metadata = new AndroidMetadata(androidDriver, null, null, null, "portrait", null);
        Assert.assertEquals(metadata.orientation(), "portrait");
    }

    @Test
    public void testOrientatioWithLandscape() {
        metadata = new AndroidMetadata(androidDriver, null, null, null, "landscape", null);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }

    @Test
    public void testOrientatioWithWrongParam() {
        metadata = new AndroidMetadata(androidDriver, null, null, null, "temp", null);
        Assert.assertEquals(metadata.orientation(), "portrait");
    }

    @Test
    public void testOrientatioWithNullParam() {
        Assert.assertEquals(metadata.orientation(), "portrait");
    }

    @Test
    public void testScaleFactor() {
        Assert.assertEquals(metadata.scaleFactor().intValue(), 1);
    }

    @Test
    public void testOrientatioWithNullParamAndCaps() {
        when(androidDriver.getCapabilities().getCapability("orientation")).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }

    @Test
    public void testOrientationAuto() {
        metadata = new AndroidMetadata(androidDriver, null, null, null, "auto", null);
        when(androidDriver.getOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }

    @Test
    public void testDeviceNameFromEspressoDriver() {
        when(capabilities.getCapability("device")).thenReturn(null);
        when(capabilities.getCapability("desired")).thenReturn(null);
        when(capabilities.getCapability("appium:deviceName")).thenReturn("Pixel 6");
        Assert.assertEquals(metadata.deviceName(), "Pixel 6");
    }

    @Test
    public void testDeviceScreenWidthWithViewportRect() {
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.put("width", 720L);
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 720);
    }

    @Test
    public void testDeviceScreenWidthWithRealDisplaySize() {
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear(); // Remove width from viewportRect
        
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("realDisplaySize", "1440x2960");
        when(androidDriver.executeScript("mobile: deviceInfo")).thenReturn(deviceInfo);
        
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 1440);
    }

    @Test
    public void testDeviceScreenWidthFallbackToZero() {
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear(); // Remove width from viewportRect
        when(androidDriver.executeScript("mobile: deviceInfo")).thenReturn(null);
        
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 0);
    }

    @Test
    public void testDeviceScreenHeightWithRealDisplaySize() {
        // Clear deviceScreenSize and viewportRect to force realDisplaySize path
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear();
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("realDisplaySize", "1440x2960");
        when(androidDriver.executeScript("mobile: deviceInfo")).thenReturn(deviceInfo);
        
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), 2960);
    }

    @Test
    public void testDeviceScreenHeightWithViewportRect() {
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.put("height", 1920L);
        
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), 1920);
    }

    @Test
    public void testDeviceScreenHeightFallbackToZero() {
        when(androidDriver.executeScript("mobile: deviceInfo")).thenReturn(null);
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear(); // Remove height from viewportRect
        
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), 0);
    }

    @Test
    public void testGetRealDisplaySizeCaching() {
        // Clear deviceScreenSize and viewportRect to force realDisplaySize path
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear();
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("realDisplaySize", "1440x2960");
        when(androidDriver.executeScript("mobile: deviceInfo")).thenReturn(deviceInfo);
        
        // First call should execute the script
        metadata.deviceScreenWidth();
        
        // Second call should use cached value
        metadata.deviceScreenHeight();
        
        // Verify executeScript was called only once for deviceInfo
        org.mockito.Mockito.verify(androidDriver, org.mockito.Mockito.times(1))
            .executeScript("mobile: deviceInfo");
    }

    @Test
    public void testGetRealDisplaySizeWithException() {
        // Clear deviceScreenSize to force fallback to realDisplaySize path
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear();
        when(androidDriver.executeScript("mobile: deviceInfo"))
            .thenThrow(new RuntimeException("Device info not supported"));
        
        // Should fall back to 0 when all methods fail
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 0);
    }

    @Test
    public void testGetRealDisplaySizeWithEmptyDeviceInfo() {
        // Clear deviceScreenSize to force fallback to realDisplaySize path
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear();
        Map<String, Object> deviceInfo = new HashMap<>();
        // deviceInfo doesn't contain realDisplaySize key
        when(androidDriver.executeScript("mobile: deviceInfo")).thenReturn(deviceInfo);
        
        // Should fall back to 0
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 0);
    }

    @Test
    public void testGetRealDisplaySizeWithNullDeviceInfo() {
        // Clear deviceScreenSize to force fallback to realDisplaySize path
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear();
        when(androidDriver.executeScript("mobile: deviceInfo")).thenReturn(null);
        
        // Should fall back to 0
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 0);
    }

    @Test
    public void testDeviceScreenWidthRealDisplaySizeWithException() {
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear();
        when(androidDriver.executeScript("mobile: deviceInfo"))
            .thenThrow(new RuntimeException("Device info not supported"));
        
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 0);
    }

    @Test
    public void testDeviceScreenHeightRealDisplaySizeReturnsFirst() {
        // Test deviceScreenHeight with deviceScreenSize disabled to check realDisplaySize
        when(capabilities.getCapability("deviceScreenSize")).thenReturn(null);
        viewportRect.clear();
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("realDisplaySize", "1440x2960");
        when(androidDriver.executeScript("mobile: deviceInfo")).thenReturn(deviceInfo);
        
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), 2960);
    }

    @Test 
    public void testDeviceScreenWidthPriority() {
        // Test that deviceScreenWidth checks deviceScreenSize first, then viewportRect, then realDisplaySize
        when(capabilities.getCapability("deviceScreenSize")).thenReturn("1080x2160");
        
        // Should return deviceScreenSize value (1080)
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 1080);
    }
}
