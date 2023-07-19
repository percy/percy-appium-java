package io.percy.appium;

import io.appium.java_client.MobileElement;
import io.percy.appium.lib.CliWrapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
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
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        when(cliMock.healthcheck()).thenReturn(true);
        percy.screenshot("Test");
        verify(cliMock).postScreenshotPOA(eq("Test"), eq("123"), eq("https://hub.browserstack.com/wd/hub"), eq(capabilities.asMap()), eq(null));
    }

    @Test
    public void takeScreenshotWithOptions() {
        when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        percy = spy(new PercyOnAutomate(androidDriver));
        percy.setCliWrapper(cliMock);
        when(cliMock.healthcheck()).thenReturn(true);
        Map<String, Object> options = new HashMap<>();
        MobileElement mockedElement = mock(MobileElement.class);
        when(mockedElement.getId()).thenReturn("1234");
        options.put("ignore_region_appium_elements", Arrays.asList(mockedElement));
        percy.screenshot("Test", options);

        options.remove("ignore_region_appium_elements");
        options.put("ignore_region_elements", "1234");
        verify(cliMock).postScreenshotPOA(eq("Test"), eq("123"), eq("https://hub.browserstack.com/wd/hub"), eq(capabilities.asMap()), eq(options));
    }
}
