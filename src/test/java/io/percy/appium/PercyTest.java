package io.percy.appium;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionId;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.ScreenshotOptions;

@RunWith(org.mockito.junit.MockitoJUnitRunner.Silent.class)
public class PercyTest {
    @Mock
    AndroidDriver androidDriver;

    @Before
    public void setup() throws Exception {
        // healthcheck() runs in the constructor and fails (no CLI server),
        // leaving the session type as whatever we set here.
        Environment.setSessionType("web");
        when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
        lenient().when(androidDriver.getCapabilities()).thenReturn(new DesiredCapabilities());
        lenient().when(androidDriver.getRemoteAddress())
                .thenReturn(new URL("https://hub.browserstack.com/wd/hub"));
    }

    private void injectInnerPercy(Percy percy, IPercy inner) throws Exception {
        Field field = Percy.class.getDeclaredField("percy");
        field.setAccessible(true);
        field.set(percy, inner);
    }

    @Test
    public void constructorUsesAppPercyForNonAutomateSessions() throws Exception {
        Percy percy = new Percy(androidDriver);
        Field field = Percy.class.getDeclaredField("percy");
        field.setAccessible(true);
        Assert.assertTrue(field.get(percy) instanceof AppPercy);
    }

    @Test
    public void constructorUsesPercyOnAutomateForAutomateSessions() {
        try (MockedStatic<Environment> env = Mockito.mockStatic(Environment.class, Mockito.CALLS_REAL_METHODS)) {
            env.when(Environment::getSessionType).thenReturn("automate");
            Percy percy = new Percy(androidDriver);
            try {
                Field field = Percy.class.getDeclaredField("percy");
                field.setAccessible(true);
                Assert.assertTrue(field.get(percy) instanceof PercyOnAutomate);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    @Test
    public void screenshotByNameDelegates() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        JSONObject expected = new JSONObject().put("ok", true);
        when(inner.screenshot("name")).thenReturn(expected);
        injectInnerPercy(percy, inner);
        Assert.assertEquals(expected, percy.screenshot("name"));
    }

    @Test
    public void screenshotWithMapOptionsDelegates() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        Map<String, Object> options = new HashMap<>();
        JSONObject expected = new JSONObject();
        when(inner.screenshot("name", options)).thenReturn(expected);
        injectInnerPercy(percy, inner);
        Assert.assertEquals(expected, percy.screenshot("name", options));
    }

    @Test
    public void screenshotWithFullScreenDelegates() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        JSONObject expected = new JSONObject();
        when(inner.screenshot("name", true)).thenReturn(expected);
        injectInnerPercy(percy, inner);
        Assert.assertEquals(expected, percy.screenshot("name", true));
    }

    @Test
    public void screenshotWithScreenshotOptionsDelegates() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        ScreenshotOptions options = new ScreenshotOptions();
        JSONObject expected = new JSONObject();
        when(inner.screenshot("name", options)).thenReturn(expected);
        injectInnerPercy(percy, inner);
        Assert.assertEquals(expected, percy.screenshot("name", options));
    }

    @Test
    public void screenshotWithFullScreenAndOptionsDelegates() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        ScreenshotOptions options = new ScreenshotOptions();
        JSONObject expected = new JSONObject();
        when(inner.screenshot("name", false, options)).thenReturn(expected);
        injectInnerPercy(percy, inner);
        Assert.assertEquals(expected, percy.screenshot("name", false, options));
    }

    @Test
    public void screenshotSwallowsExceptionsAndReturnsNull() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        when(inner.screenshot("boom")).thenThrow(new RuntimeException("fail"));
        injectInnerPercy(percy, inner);
        Assert.assertNull(percy.screenshot("boom"));
    }

    @Test
    public void screenshotWithMapOptionsSwallowsExceptions() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        Map<String, Object> options = new HashMap<>();
        when(inner.screenshot("boom", options)).thenThrow(new RuntimeException("fail"));
        injectInnerPercy(percy, inner);
        Assert.assertNull(percy.screenshot("boom", options));
    }

    @Test
    public void screenshotWithFullScreenSwallowsExceptions() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        when(inner.screenshot("boom", true)).thenThrow(new RuntimeException("fail"));
        injectInnerPercy(percy, inner);
        Assert.assertNull(percy.screenshot("boom", true));
    }

    @Test
    public void screenshotWithScreenshotOptionsSwallowsExceptions() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        ScreenshotOptions options = new ScreenshotOptions();
        when(inner.screenshot("boom", options)).thenThrow(new RuntimeException("fail"));
        injectInnerPercy(percy, inner);
        Assert.assertNull(percy.screenshot("boom", options));
    }

    @Test
    public void screenshotWithFullScreenAndOptionsSwallowsExceptions() throws Exception {
        Percy percy = new Percy(androidDriver);
        IPercy inner = mock(IPercy.class);
        ScreenshotOptions options = new ScreenshotOptions();
        when(inner.screenshot("boom", false, options)).thenThrow(new RuntimeException("fail"));
        injectInnerPercy(percy, inner);
        Assert.assertNull(percy.screenshot("boom", false, options));
    }

    @Test
    public void setClientInfoOverridesEnvironment() {
        Percy percy = new Percy(androidDriver);
        // exercises the client/environment-info override path without throwing
        percy.setClientInfo("custom-client/1.0", "custom-env/2.0");
    }

    @Test
    public void getSdkVersionReturnsEnvironmentVersion() {
        Assert.assertEquals(Environment.getSdkVersion(), Percy.getSdkVersion());
    }
}
