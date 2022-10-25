package io.percy.appium.lib;

import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.SessionId;

import com.github.javafaker.Faker;

import io.appium.java_client.ios.IOSDriver;
import io.percy.appium.metadata.IosMetadata;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class CacheTest {
    IosMetadata metadata;

    @Mock
    IOSDriver driver;

    @Mock
    Capabilities capabilities;

    HashMap<String, Long> viewportRect = new HashMap<String, Long>();

    Faker faker = new Faker();
    Long width = 100L;

    @Test
    public void testDeviceScreenWidth() {
        viewportRect.put("width", width);
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        when(driver.executeScript("mobile: viewportRect")).thenReturn(viewportRect);
        metadata = new IosMetadata(driver);
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), width.intValue());
    }

    // Does not require execute script to be called second time
    @Test
    public void testDeviceScreenWidthAgain() {
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        metadata = new IosMetadata(driver);
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), width.intValue());
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    // Post clearing cache throws NullPointerException
    @Test
    public void testDeviceScreenWidthThirdTime() {
        Cache.CACHE_MAP.clear();
        exception.expect(NullPointerException.class);
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        metadata = new IosMetadata(driver);
        metadata.deviceScreenWidth();
    }
    
}
