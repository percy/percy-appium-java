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
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.metadata.AndroidMetadata;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class CacheTest {
    AndroidMetadata metadata;

    @Mock
    AndroidDriver driver;

    @Mock
    Capabilities capabilities;

    HashMap<String, Long> viewportRect = new HashMap<String, Long>();
    HashMap<String, HashMap<String, Long>> sessionValue = new HashMap<String, HashMap<String, Long>>();
    Response session = new Response(new SessionId("abc"));

    @Test
    public void testStatBarHeight() {
        Cache.CACHE_MAP.clear();
        viewportRect.put("top", 100L);
        sessionValue.put("viewportRect", viewportRect);
        session.setValue(sessionValue);
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        when(driver.execute("getSession")).thenReturn(session);
        metadata = new AndroidMetadata(driver, null, null, null, null, null);
        Assert.assertEquals(metadata.statBarHeight().intValue(), 100);
    }

    // Does not require execute script to be called second time
    @Test
    public void teststatBarHeightAgain() {
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        metadata = new AndroidMetadata(driver, null, null, null, null, null);
        Assert.assertEquals(metadata.statBarHeight().intValue(), 100);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    // Post clearing cache throws NullPointerException
    @Test
    public void testStatBarHeightThirdTime() {
        Cache.CACHE_MAP.clear();
        exception.expect(NullPointerException.class);
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        metadata = new AndroidMetadata(driver, null, null, null, null, null);
        metadata.statBarHeight();
    }
}
