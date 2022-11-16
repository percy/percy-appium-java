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
    HashMap<String, HashMap<String, Long>> sessionValue = new HashMap<String, HashMap<String, Long>>();
    Response session = new Response(new SessionId("abc"));

    Faker faker = new Faker();
    Long width = faker.number().randomNumber(3, false);
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);

    @Before
    public void setup() {
        viewportRect.put("width", width);
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        sessionValue.put("viewportRect", viewportRect);
        session.setValue(sessionValue);
        when(driver.execute("getSession")).thenReturn(session);
        when(driver.getCapabilities()).thenReturn(capabilities);
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        metadata = new IosMetadata(driver);
    }

    @After
    public void clearCache() {
        Cache.CACHE_MAP.clear();
    }

    @Test
    public void testDeviceScreenWidth() {
        String sessionDetails = "{\"device\":\"iphone 12 pro\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), width.intValue());
    }

    @Test
    public void testDeviceScreenWidthFromJson() {
        viewportRect.clear();
        String sessionDetails = "{\"device\":\"iphone 8 plus\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 1080);
    }

    @Test
    public void testDeviceScreenHeight() {
        String sessionDetails = "{\"device\":\"iphone 12 pro\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), height.intValue());
    }

    @Test
    public void testDeviceScreenHeightFromJson() {
        viewportRect.clear();
        String sessionDetails = "{\"device\":\"iphone 8 plus\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), 1920);
    }

    @Test
    public void testStatBarHeight() {
        String sessionDetails = "{\"device\":\"iphone 12 pro\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.statBarHeight().intValue(), top.intValue());
    }

    @Test
    public void testStatBarHeightFromJson() {
        viewportRect.clear();
        String sessionDetails = "{\"device\":\"iphone 8 plus\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.statBarHeight().intValue(), 60);
    }

    @Test
    public void testNavBarHeight() {
        Assert.assertEquals(metadata.navBarHeight().intValue(), 0);
    }

    @Test
    public void testDeviceName() {
        String sessionDetails = "{\"device\":\"iPhone 12\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.deviceName(), "iPhone 12");
    }

    @Test
    public void testOsName(){
        when(capabilities.getCapability("platformName")).thenReturn("IOS");
        Assert.assertEquals(metadata.osName(), "IOS");
    }

    @Test
    public void testOsVersion(){
        when(capabilities.getCapability("platformVersion")).thenReturn(null);
        when(capabilities.getCapability("os_version")).thenReturn("16");
        Assert.assertEquals(metadata.platformVersion(), "16");
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
        when(driver.getCapabilities().getCapability("orientation")).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation(null), "landscape");
    }

    @Test
    public void testOrientatioAuto(){
        when(driver.getOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation("AUTO"), "landscape");
    }
}
