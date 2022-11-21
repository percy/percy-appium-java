package io.percy.appium.providers;

import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.SessionId;

import com.github.javafaker.Faker;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Cache;
import io.percy.appium.metadata.AndroidMetadata;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class AppAutomateTest {
    @Mock
    AndroidDriver androidDriver;

    @Mock
    Capabilities capabilities;

    AppAutomate appAutomate;

    @Mock
    AndroidMetadata metadata;

    Faker faker = new Faker();
    Integer deviceScreenHeight = (int) faker.number().randomNumber(3, false);
    Integer deviceScreenWidth = (int) faker.number().randomNumber(3, false);

    @Before
    public void setup() {
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        appAutomate = new AppAutomate(androidDriver, metadata);
    }

    @Test
    public void testGetDebugUrl() {
        Cache.CACHE_MAP.clear();
        String sessionDetails = "{\"browser_url\":\"http://example_session.browserstack.com/\"}";
        when(androidDriver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(appAutomate.getDebugUrl(), "http://example_session.browserstack.com/");
    }

    @Test
    public void testGetOsVersion() {
        Cache.CACHE_MAP.clear();
        String sessionDetails = "{\"os_version\":\"13\"}";
        when(androidDriver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(appAutomate.getOsVersion(), "13");
    }

    @Test
    public void testSupports() {
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://browserstack.com/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(appAutomate.supports(androidDriver), true);
    }

    @Test
    public void testDoesNotSupports() {
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://example.com/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(appAutomate.supports(androidDriver), false);
    }

    @Test
    public void testExecutePercyScreenshotBeginWhenNullExceptionDoesNotThrow() {
        appAutomate.executePercyScreenshotBegin("");
    }

    @Test
    public void testExecutePercyScreenshotEndWhenNullExceptionDoesNotThrow() {
        appAutomate.executePercyScreenshotEnd("", "", "");
    }

    @Test
    public void testGetTag(){
        Cache.CACHE_MAP.clear();
        when(metadata.deviceName()).thenReturn("Samsung Galaxy s22");
        when(metadata.osName()).thenReturn("Android");
        when(metadata.platformVersion()).thenReturn(null);
        when(metadata.orientation("AUTO")).thenReturn("LANDSCAPE");
        when(metadata.deviceScreenHeight()).thenReturn(deviceScreenHeight);
        when(metadata.deviceScreenWidth()).thenReturn(deviceScreenWidth);
        String sessionDetails = "{\"os_version\":\"13.1\"}";
        when(androidDriver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);


        AppAutomate appAutomate = new AppAutomate(androidDriver, metadata);
        
        JSONObject tile = appAutomate.getTag(null, "AUTO");
        Assert.assertEquals(tile.get("name"), "Samsung Galaxy s22");
        Assert.assertEquals(tile.get("osName"), "Android");
        Assert.assertEquals(tile.get("osVersion"), "13");
        Assert.assertEquals(tile.get("width"), deviceScreenWidth);
        Assert.assertEquals(tile.get("height"), deviceScreenHeight);
        Assert.assertEquals(tile.get("orientation"), "LANDSCAPE");
    }

    @Test
    public void testExecutePercyScreenshotBegin() {
        String response = "{\"success\":\"true\"}";
        String name = "First";
        JSONObject arguments = new JSONObject();
        arguments.put("state", "begin");
        arguments.put("percyBuildId", System.getenv("PERCY_BUILD_ID"));
        arguments.put("percyBuildUrl", System.getenv("PERCY_BUILD_URL"));
        arguments.put("name", name);
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(response);
        appAutomate.executePercyScreenshotBegin(name);
    }

    @Test
    public void testExecutePercyScreenshotEnd() {
        String response = "{\"success\":\"true\"}";
        String percyScreenshotUrl = "";
        String name = "First";
        JSONObject arguments = new JSONObject();
        arguments.put("state", "end");
        arguments.put("percyScreenshotUrl", percyScreenshotUrl);
        arguments.put("name", name);
        arguments.put("status", "success");
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(response);
        appAutomate.executePercyScreenshotEnd(name ,percyScreenshotUrl, null);
    }

}
