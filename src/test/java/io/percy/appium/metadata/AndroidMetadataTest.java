package io.percy.appium.metadata;

import io.appium.java_client.android.AndroidDriver;
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
import org.openqa.selenium.Platform;
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
    Integer statusBarHeight = (int) faker.number().randomNumber(3, false);
    Integer navigationBarHeight = (int) faker.number().randomNumber(3, false);

    HashMap<String, Object> statusBar = new HashMap<String, Object>();
    HashMap<String, Object> navigationBar = new HashMap<String, Object>();
    Map<String, Map<String, Object>> systemBars = new HashMap<String, Map<String, Object>>();

    @Before
    public void setup() {
        statusBar.put("height", statusBarHeight);
        navigationBar.put("height", navigationBarHeight);
        systemBars.put("statusBar", statusBar);
        systemBars.put("navigationBar", navigationBar);
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(androidDriver.getSystemBars()).thenReturn(systemBars);
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
        Assert.assertEquals(metadata.statBarHeight(), statusBarHeight);
    }

    @Test
    public void testNavBarHeight() {
        Assert.assertEquals(metadata.navBarHeight(), navigationBarHeight);
    }

    @Test
    public void testStatBarHeightException() {
        systemBars.clear();
        Assert.assertEquals(metadata.statBarHeight().intValue(), 0);
    }

    @Test
    public void testNavBarHeightException() {
        systemBars.clear();
        Assert.assertEquals(metadata.navBarHeight().intValue(), 0);
    }

    @Test
    public void testDeviceName(){
        when(capabilities.getCapability("device")).thenReturn("Samsung s22");
        Assert.assertEquals(metadata.deviceName(), "Samsung s22");
    }

    @Test
    public void testOsName(){
        when(capabilities.getPlatform()).thenReturn(Platform.ANDROID);
        Assert.assertEquals(metadata.osName(), "ANDROID");
    }

    @Test
    public void testOsVersion(){
        when(capabilities.getCapability("osVersion")).thenReturn("12");
        Assert.assertEquals(metadata.osVersion(), "12");
    }

    @Test
    public void testOrientation(){
        when(androidDriver.getOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation(), "landscape");
    }
}
