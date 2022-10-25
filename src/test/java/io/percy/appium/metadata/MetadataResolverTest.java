package io.percy.appium.metadata;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class MetadataResolverTest {
    @Mock
    IOSDriver iosDriver;

    @Mock
    AndroidDriver androidDriver;

    @Mock
    RemoteWebDriver webDriver;

    @Before
    public void setup() {
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        when(iosDriver.getSessionId()).thenReturn(new SessionId("abc"));
    }

    @Test
    public void testIOSDriver() {
        Assert.assertEquals(MetadataResolver.resolve(iosDriver).getClass(), IosMetadata.class);
    }

    @Test
    public void testAndroidDriver() {
        Assert.assertEquals(MetadataResolver.resolve(androidDriver).getClass(), AndroidMetadata.class);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testRemoteWebDriver() throws Exception {
        exception.expect(Exception.class);
        Assert.assertEquals(MetadataResolver.resolve((AppiumDriver) webDriver), null);
    }
    
}
