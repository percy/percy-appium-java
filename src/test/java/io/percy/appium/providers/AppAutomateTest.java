package io.percy.appium.providers;

import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

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
        appAutomate.executePercyScreenshotBegin();
    }

    @Test
    public void testExecutePercyScreenshotEndWhenNullExceptionDoesNotThrow() {
        appAutomate.executePercyScreenshotEnd("");
    }

    @Test
    public void testExecutePercyScreenshotBegin() {
        String response = "{\"success\":\"true\"}";
        when(androidDriver.executeScript(String.format(
                "browserstack_executor: {\"action\": \"percyScreenshot\", \"arguments\": {\"state\": \"begin\", \"percyBuildId\": \"{%s}\", \"percyBuildUrl\": \"{%s}\"}}",
                System.getenv("PERCY_BUILD_ID"), System.getenv("PERCY_BUILD_URL")))).thenReturn(response);
        appAutomate.executePercyScreenshotBegin();
    }

    @Test
    public void testExecutePercyScreenshotEnd() {
        String response = "{\"success\":\"true\"}";
        String percyScreenshotUrl = "";
        when(androidDriver.executeScript(String.format(
            "browserstack_executor: {\"action\": \"percyScreenshot\", \"arguments\": {\"state\": \"end\", \"percyScreenshotUrl\": \"{%s}\"}}",
            percyScreenshotUrl))).thenReturn(response);
        appAutomate.executePercyScreenshotEnd(percyScreenshotUrl);
    }

}
