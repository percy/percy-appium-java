package io.percy.appium.metadata;

import io.appium.java_client.ios.IOSDriver;
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
import org.openqa.selenium.Platform;
import org.openqa.selenium.ScreenOrientation;
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

    Faker faker = new Faker();
    Long width = faker.number().randomNumber(3, false);
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);

    @Before
    public void setup() {
        viewportRect.put("width", width);
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        when(driver.getCapabilities()).thenReturn(capabilities);
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        when(driver.executeScript("mobile: viewportRect")).thenReturn(viewportRect);
        metadata = new IosMetadata(driver);
    }

    @After
    public void clearCache() {
        Cache.CACHE_MAP.clear();
    }

    @Test
    public void testDeviceScreenWidth() {
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), width.intValue());
    }

    @Test
    public void testDeviceScreenHeight() {
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), top.intValue() + height.intValue());
    }

    @Test
    public void testStatBarHeight() {
        Assert.assertEquals(metadata.statBarHeight().intValue(), top.intValue());
    }

    @Test
    public void testNavBarHeight() {
        Assert.assertEquals(metadata.navBarHeight().intValue(), 0);
    }

    @Test
    public void testDeviceName(){
        when(capabilities.getCapability("device")).thenReturn("iPhone 12");
        Assert.assertEquals(metadata.deviceName(), "iPhone 12");
    }

    @Test
    public void testOsName(){
        when(capabilities.getPlatform()).thenReturn(Platform.IOS);
        Assert.assertEquals(metadata.osName(), "IOS");
    }

    @Test
    public void testOsVersion(){
        when(capabilities.getCapability("osVersion")).thenReturn("16");
        Assert.assertEquals(metadata.osVersion(), "16");
    }

    @Test
    public void testOrientation(){
        when(driver.getOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }
}
