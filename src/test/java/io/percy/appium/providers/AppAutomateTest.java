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

    @Before
    public void setup() {
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        appAutomate = new AppAutomate(androidDriver, metadata);
    }

    @Test
    public void testGetDebugUrl() {
        String sessionDetails = "{\"browser_url\":\"http://example_session.browserstack.com/\"}";
        when(androidDriver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(appAutomate.getDebugUrl(), "http://example_session.browserstack.com/");
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
        when(androidDriver.executeScript(String.format("browserstack_executor: {%s}", reqObject.toString())))
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
        when(androidDriver.executeScript(String.format("browserstack_executor: {%s}", reqObject.toString())))
                .thenReturn(response);
        appAutomate.executePercyScreenshotEnd(name ,percyScreenshotUrl, null);
    }

}
