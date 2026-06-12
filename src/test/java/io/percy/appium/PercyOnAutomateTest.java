package io.percy.appium;

import io.percy.appium.lib.CliWrapper;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.Silent.class)
public class PercyOnAutomateTest {
    @Mock
    AndroidDriver androidDriver;
    private static PercyOnAutomate percy;
    private static CliWrapper cliMock;
    private DesiredCapabilities capabilities;

    @Before
    public void setup() {
        AndroidDriver androidDriver = mock(AndroidDriver.class);
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "Chrome");

        cliMock = mock(CliWrapper.class);
    }

    @Test
    public void takeScreenshot() {
        when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        when(androidDriver.getCapabilities()).thenReturn(capabilities);

        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        when(cliMock.healthcheck()).thenReturn(true);
        percy.screenshot("Test");
        verify(cliMock).postScreenshotPOA(eq("Test"), eq("123"), eq("https://hub.browserstack.com/wd/hub"), eq(capabilities.asMap()), eq(null));
    }

    @Test
    public void takeScreenshotWithOptions() {
        lenient().when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        lenient().when(androidDriver.getCapabilities()).thenReturn(capabilities);
        try {
            lenient(). when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        when(cliMock.healthcheck()).thenReturn(true);
        Map<String, Object> options = new HashMap<>();
        RemoteWebElement mockedElement = mock(RemoteWebElement.class);
        when(mockedElement.getId()).thenReturn("1234");
        options.put("ignore_region_appium_elements", Arrays.asList(mockedElement));
        percy.screenshot("Test", options);

        options.remove("ignore_region_appium_elements");
        options.put("ignore_region_elements", "1234");
        verify(cliMock).postScreenshotPOA(eq("Test"), eq("123"), eq("https://hub.browserstack.com/wd/hub"), eq(capabilities.asMap()), eq(options));
    }

    @Test
    public void takeScreenshotWithSYNCOption(){
        lenient().when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        lenient().when(androidDriver.getCapabilities()).thenReturn(capabilities);
        try {
            lenient(). when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        
        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        when(cliMock.healthcheck()).thenReturn(true);
        Map<String, Object> options = new HashMap<>();
        
        // Mock the cliWrapper response, when sync is true
        JSONObject innerData = new JSONObject();
        innerData.put("snapshot-name", "Test");
        
        JSONObject jsonData = new JSONObject();
        jsonData.put("data", innerData);
        when(cliMock.postScreenshotPOA(any(), any(), any(), any(), any())).thenReturn(jsonData);

        options.put("sync", true);
        JSONObject data = percy.screenshot("Test", options);

        options.remove("sync");

        if(data != null){
            assertEquals(data.getString("snapshot-name"), "Test");
        }
        verify(cliMock).postScreenshotPOA(eq("Test"), eq("123"), eq("https://hub.browserstack.com/wd/hub"), eq(capabilities.asMap()), eq(options));
    }

    @Test
    public void takeScreenshotWhenPercyDisabled() {
        lenient().when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        lenient().when(androidDriver.getCapabilities()).thenReturn(capabilities);

        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        // healthcheck returns false -> isPercyEnabled is false -> early return null (line 64)
        when(cliMock.healthcheck()).thenReturn(false);

        JSONObject data = percy.screenshot("Test");

        assertNull(data);
        verify(cliMock, never()).postScreenshotPOA(any(), any(), any(), any(), any());
    }

    @Test
    public void takeScreenshotWithConsiderAltKeyOptions() throws MalformedURLException {
        lenient().when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        lenient().when(androidDriver.getCapabilities()).thenReturn(capabilities);
        lenient().when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));

        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        when(cliMock.healthcheck()).thenReturn(true);

        Map<String, Object> options = new HashMap<>();
        RemoteWebElement mockedElement = mock(RemoteWebElement.class);
        when(mockedElement.getId()).thenReturn("9999");
        // Alt camelCase key -> normalized to snake_case key, then to consider_region_elements
        // (covers lines 83-84 and 94-98)
        options.put("considerRegionAppiumElements", Arrays.asList(mockedElement));

        percy.screenshot("Test", options);

        Map<String, Object> expected = new HashMap<>();
        expected.put("consider_region_elements", Arrays.asList("9999"));
        verify(cliMock).postScreenshotPOA(eq("Test"), eq("123"), eq("https://hub.browserstack.com/wd/hub"), eq(capabilities.asMap()), eq(expected));
    }

    @Test
    public void takeScreenshotWithIgnoreAltKeyOptions() throws MalformedURLException {
        lenient().when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        lenient().when(androidDriver.getCapabilities()).thenReturn(capabilities);
        lenient().when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));

        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        when(cliMock.healthcheck()).thenReturn(true);

        Map<String, Object> options = new HashMap<>();
        RemoteWebElement mockedElement = mock(RemoteWebElement.class);
        when(mockedElement.getId()).thenReturn("8888");
        // Alt camelCase ignore key -> normalized to snake_case key (covers lines 78-79)
        options.put("ignoreRegionAppiumElements", Arrays.asList(mockedElement));

        percy.screenshot("Test", options);

        Map<String, Object> expected = new HashMap<>();
        expected.put("ignore_region_elements", Arrays.asList("8888"));
        verify(cliMock).postScreenshotPOA(eq("Test"), eq("123"), eq("https://hub.browserstack.com/wd/hub"), eq(capabilities.asMap()), eq(expected));
    }

    @Test
    public void takeScreenshotSwallowsErrorWhenIgnoreErrorsTrue() throws MalformedURLException {
        // Default capabilities do not set percy.ignoreErrors -> ignoreErrors defaults to true
        lenient().when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        lenient().when(androidDriver.getCapabilities()).thenReturn(capabilities);
        lenient().when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));

        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        when(cliMock.healthcheck()).thenReturn(true);
        // Force an exception inside the try block (covers catch -> log -> return null, lines 114-116, 120)
        when(cliMock.postScreenshotPOA(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("boom"));

        JSONObject data = percy.screenshot("Test");

        assertNull(data);
    }

    @Test
    public void takeScreenshotRethrowsWhenIgnoreErrorsFalse() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "Chrome");
        // String literal "false" is interned; PercyOptions uses reference equality -> ignoreErrors becomes false
        caps.setCapability("percy.ignoreErrors", "false");

        lenient().when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        lenient().when(androidDriver.getCapabilities()).thenReturn(caps);
        lenient().when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));

        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        when(cliMock.healthcheck()).thenReturn(true);
        when(cliMock.postScreenshotPOA(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("boom"));

        try {
            // covers lines 114-118 (catch -> log -> !ignoreErrors -> throw)
            percy.screenshot("Test");
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Error taking screenshot Test", e.getMessage());
        }
    }
}
