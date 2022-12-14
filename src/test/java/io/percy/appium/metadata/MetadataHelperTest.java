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
public class MetadataHelperTest {
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
    public void testResolveIOSDriver() {
        Assert.assertEquals(MetadataHelper.resolve(iosDriver, null, null, null, null, null).getClass(), IosMetadata.class);
    }

    @Test
    public void testResolveAndroidDriver() {
        Assert.assertEquals(MetadataHelper.resolve(androidDriver, null, null, null, null, null).getClass(),
                AndroidMetadata.class);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testResolveRemoteWebDriver() throws Exception {
        exception.expect(Exception.class);
        Assert.assertEquals(MetadataHelper.resolve((AppiumDriver) webDriver, null, null, null, null, null), null);
    }

    @Test
    public void testValueFromStaticDevicesInfoWhenExists() {
        Assert.assertEquals(MetadataHelper.valueFromStaticDevicesInfo("pixelRatio", "iphone 8 plus").intValue(), 3);
    }

    @Test
    public void testValueFromStaticDevicesInfoWhenNotExists() {
        Assert.assertEquals(MetadataHelper.valueFromStaticDevicesInfo("pixelRatio", "iphone 1").intValue(), 0);
    }

}
