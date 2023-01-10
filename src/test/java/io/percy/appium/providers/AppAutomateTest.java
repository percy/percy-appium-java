package io.percy.appium.providers;

import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import com.github.javafaker.Faker;

import io.appium.java_client.android.AndroidDriver;
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

    HashMap<String, Long> viewportRect = new HashMap<String, Long>();
    HashMap<String, HashMap<String, Long>> sessionValue = new HashMap<String, HashMap<String, Long>>();
    Response session = new Response(new SessionId("abc"));

    Faker faker = new Faker();
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);
    Integer deviceScreenHeight = (int) faker.number().randomNumber(3, false);
    Integer deviceScreenWidth = (int) faker.number().randomNumber(3, false);

    @Before
    public void setup() {
        appAutomate = new AppAutomate(androidDriver);
    }

    @Test
    public void testGetDebugUrl() {
        JSONObject result = new JSONObject("{\"buildHash\":\"abc\", \"sessionHash\":\"def\"}");
        Assert.assertEquals(appAutomate.getDebugUrl(result), "https://app-automate.browserstack.com/dashboard/v2/builds/abc/sessions/def");
    }

    @Test
    public void testSupports() {
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://browserstack.com/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(AppAutomate.supports(androidDriver), true);
    }

    @Test
    public void testDoesNotSupports() {
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://example.com/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(AppAutomate.supports(androidDriver), false);
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
    public void testExecutePercyScreenshot() {
        String response = "{\"result\":\"sha\"}";
        JSONObject arguments = new JSONObject();
        arguments.put("state", "screenshot");
        arguments.put("percyBuildId", System.getenv("PERCY_BUILD_ID"));
        arguments.put("screenshotType", "SinglePage");
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(response);
        appAutomate.executePercyScreenshot();
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
        appAutomate.executePercyScreenshotEnd(name, percyScreenshotUrl, null);
    }

    @Test
    public void testDeviceName() {
        JSONObject result = new JSONObject("{\"deviceName\":\"Samsung Galaxy S22\"}");
        Assert.assertEquals(appAutomate.deviceName(null, result), "Samsung Galaxy S22");
    }

    @Test
    public void testDeviceNameWhenProvidedInParams() {
        Assert.assertEquals(appAutomate.deviceName("Samsung Galaxy S22 Ultra", null), "Samsung Galaxy S22 Ultra");
    }

}
