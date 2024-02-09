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
import org.openqa.selenium.remote.Response;
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
    HashMap<String, HashMap<String, Long>> sessionValue = new HashMap<String, HashMap<String, Long>>();
    Response session = new Response(new SessionId("abc"));

    @Before
    public void setup() {
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        sessionValue.put("viewportRect", viewportRect);
        session.setValue(sessionValue);
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(androidDriver.execute("getSession")).thenReturn(session);
        when(capabilities.getCapability("deviceScreenSize")).thenReturn("1080x2160");
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
}
