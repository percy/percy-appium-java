package io.percy.appium.metadata;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Cache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class DriverMetadataTest {
    @Mock
    AndroidDriver driver;

    private DesiredCapabilities capabilities;

    DriverMetadata metadata;

    Response session = new Response(new SessionId("abc"));


    @Before
    public void setup() {
        Map<String, Object> caps = new HashMap<>();
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "Samsung Galaxy S22");
        capabilities.setCapability("platform", "chrome_android");
        capabilities.setCapability("osName", "android");
        when(driver.getCapabilities()).thenReturn(capabilities);
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        metadata = new DriverMetadata(driver);
    }


    @After
    public void clearCache() {
        Cache.CACHE_MAP.clear();
    }

    @Test
    public void testSessionId() {
        Assert.assertEquals(metadata.getSessionId(), "abc");
    }

    @Test
    public void testCommandExecutorUrl() throws MalformedURLException {
        when(driver.getRemoteAddress()).thenReturn(new URL("https://localhost:4444/wd/hub"));
        Assert.assertEquals(metadata.getCommandExecutorUrl(), "https://localhost:4444/wd/hub");
    }

    @Test
    public void testGetCapabilities(){
        Map<String, Object> caps = new HashMap<>();
        caps.put("deviceName", "Samsung Galaxy S22");
        caps.put("platform", "chrome_android");
        caps.put("osName", "android");
        Assert.assertEquals(metadata.getCapabilities(), caps);
    }
}
