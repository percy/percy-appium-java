package io.percy.appium.metadata;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Cache;

import static org.mockito.Mockito.when;

import java.util.HashMap;

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
        metadata = new AndroidMetadata(androidDriver);
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
    public void testNavBarHeight() {
        Assert.assertEquals(metadata.navBarHeight().intValue(), 2160 - (height + top));
    }

    @Test
    public void testDeviceName(){
        String sessionDetails = "{\"device\":\"Samsung Galaxy s22\"}";
        when(androidDriver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.deviceName(), "Samsung Galaxy s22");
    }

    @Test
    public void testOsName(){
        when(capabilities.getCapability("platformName")).thenReturn("Android");
        Assert.assertEquals(metadata.osName(), "Android");
    }

    @Test
    public void testPlatformVersion(){
        when(capabilities.getCapability("platformVersion")).thenReturn("12");
        Assert.assertEquals(metadata.platformVersion(), "12");
    }


    @Test
    public void testOrientatioWithPortrait(){
        Assert.assertEquals(metadata.orientation("PORTRAIT"), "portrait");
    }

    @Test
    public void testOrientatioWithLandscape(){
        Assert.assertEquals(metadata.orientation("LANDSCAPE"), "landscape");
    }

    @Test
    public void testOrientatioWithWrongParam(){
        Assert.assertEquals(metadata.orientation("PARAM"), "portrait");
    }

    @Test
    public void testOrientatioWithNullParam(){
        Assert.assertEquals(metadata.orientation(null), "portrait");
    }

    @Test
    public void testOrientatioWithNullParamAndCaps(){
        when(androidDriver.getCapabilities().getCapability("orientation")).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation(null), "landscape");
    }

    @Test
    public void testOrientationAuto(){
        when(androidDriver.getOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation("AUTO"), "landscape");
    }
}
