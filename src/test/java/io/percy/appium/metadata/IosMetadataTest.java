package io.percy.appium.metadata;

import io.appium.java_client.ios.IOSDriver;
import io.percy.appium.lib.Cache;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

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
public class IosMetadataTest {

    @Mock
    IOSDriver driver;

    IosMetadata metadata;

    @Mock
    Capabilities capabilities;

    HashMap<String, Long> viewportRect = new HashMap<String, Long>();
    HashMap<String, Object> sessionValue = new HashMap<>();
    Response session = new Response(new SessionId("abc"));

    Faker faker = new Faker();
    Long width = faker.number().randomNumber(3, false);
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);
    Long pixelRatio = faker.number().randomNumber(1, false);

    @Before
    public void setup() {
        viewportRect.put("width", width);
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        sessionValue.put("viewportRect", viewportRect);
        sessionValue.put("pixelRatio", pixelRatio);
        session.setValue(sessionValue);
        when(driver.execute("getSession")).thenReturn(session);
        when(driver.getCapabilities()).thenReturn(capabilities);
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        metadata = new IosMetadata(driver, null, null, null, null, null);
    }

    @After
    public void clearCache() {
        Cache.CACHE_MAP.clear();
    }

    @Test
    public void testDeviceScreenWidth() {
        when(capabilities.getCapability("deviceName")).thenReturn("iphone 12 pro");
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), width.intValue());
    }

    @Test
    public void testDeviceScreenWidthFromJson() {
        viewportRect.clear();
        when(capabilities.getCapability("deviceName")).thenReturn("iphone 8 plus");
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 1080);
    }

    @Test
    public void testDeviceScreenHeight() {
        when(capabilities.getCapability("deviceName")).thenReturn("iphone 12 pro");
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), height.intValue() + top.intValue());
    }

    @Test
    public void testDeviceScreenHeightFromJson() {
        viewportRect.clear();
        when(capabilities.getCapability("deviceName")).thenReturn("iphone 8 plus");
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), 1920);
    }

    @Test
    public void testStatBarHeight() {
        when(capabilities.getCapability("deviceName")).thenReturn("iphone 12 pro");
        Assert.assertEquals(metadata.statBarHeight().intValue(), top.intValue());
    }

    @Test
    public void testStatBarHeightFromJson() {
        viewportRect.clear();
        when(capabilities.getCapability("deviceName")).thenReturn("iphone 8 plus");
        Assert.assertEquals(metadata.statBarHeight().intValue(), 60);
    }

    @Test
    public void testNavBarHeight() {
        Assert.assertEquals(metadata.navBarHeight().intValue(), 0);
    }

    @Test
    public void testDeviceName() {
        when(capabilities.getCapability("deviceName")).thenReturn("iPhone 12");
        Assert.assertEquals(metadata.deviceName(), "iPhone 12");
    }

    @Test
    public void testExplicitlyProvidedParams() {
        metadata = new IosMetadata(driver, "iPhone 13", 100, 200, "landscape", null);
        Assert.assertEquals(metadata.deviceName(), "iPhone 13");
        Assert.assertEquals(metadata.statBarHeight().intValue(), 100);
        Assert.assertEquals(metadata.navBarHeight().intValue(), 200);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }

    @Test
    public void testOsName(){
        when(capabilities.getCapability("platformName")).thenReturn("iOS");
        Assert.assertEquals(metadata.osName(), "iOS");
    }

    @Test
    public void testOsVersion(){
        when(capabilities.getCapability("platformVersion")).thenReturn(null);
        when(capabilities.getCapability("os_version")).thenReturn("16");
        Assert.assertEquals(metadata.platformVersion(), "16");
    }

    @Test
    public void testOrientatioWithPortrait() {
        metadata = new IosMetadata(driver, null, null, null, "portrait", null);
        Assert.assertEquals(metadata.orientation(), "portrait");
    }

    @Test
    public void testOrientatioWithLandscape() {
        metadata = new IosMetadata(driver, null, null, null, "landscape", null);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }

    @Test
    public void testOrientatioWithWrongParam() {
        metadata = new IosMetadata(driver, null, null, null, "temp", null);
        Assert.assertEquals(metadata.orientation(), "portrait");
    }

    @Test
    public void testOrientatioWithNullParam() {
        Assert.assertEquals(metadata.orientation(), "portrait");
    }

    @Test
    public void testOrientatioWithNullParamAndCaps(){
        when(driver.getCapabilities().getCapability("orientation")).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }

    @Test
    public void testOrientatioAuto() {
        metadata = new IosMetadata(driver, null, null, null, "auto", null);
        when(driver.getOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }

    @Test
    public void testScaleFactor() {
        Cache.CACHE_MAP.clear();
        Map details = new HashMap<>();
        details.put("pixelRatio", 2);
        Assert.assertEquals(metadata.scaleFactor().intValue(), pixelRatio.intValue());
    }
}
