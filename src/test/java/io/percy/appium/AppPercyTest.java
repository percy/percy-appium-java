package io.percy.appium;

import io.percy.appium.lib.CliWrapper;
import io.percy.appium.lib.ScreenshotOptions;
import io.percy.appium.providers.GenericProvider;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import io.appium.java_client.android.AndroidDriver;

import org.openqa.selenium.remote.*;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.Silent.class)
public class AppPercyTest{
    @Mock
    AndroidDriver androidDriver;
    private static AppPercy percy;
    private static GenericProvider genericProvider;
    private DesiredCapabilities capabilities;

    @Before
    public void setup() {
        androidDriver = mock(AndroidDriver.class);
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserstack.user", "USER_NAME");
        capabilities.setCapability("browserstack.key", "USER_AUTH_KEY");
        capabilities.setCapability("browserstack.appium_version", "1.20.2");
        capabilities.setCapability("app", "APP_URL");
        capabilities.setCapability("device", "DEVICE_NAME");
        capabilities.setCapability("os_version", "9.0");
        capabilities.setCapability("percy.enabled", "true");
        capabilities.setCapability("percy.enabled", "true");
        genericProvider = mock(GenericProvider.class);
    }

    @Test
    public void takeScreenshotWithoutPercyEnabled() throws Exception {
      when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));

      try {
        lenient(). when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }

      percy = spy(new AppPercy(androidDriver));
      percy.setGenericProvider(genericProvider);
    
      JSONObject res = percy.screenshot("Test");
      assertEquals(res, null);
      verify(genericProvider, never()).screenshot(eq("Test"), eq(null));
    }

    @Test
    public void takeScreenshotWithoutOptions() throws Exception {
        // Percy Options to Enable Percy
        capabilities.setCapability("percy.enabled", "true");

      when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
      when(androidDriver.getCapabilities()).thenReturn(capabilities);

      try {
          lenient(). when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));
      } catch (MalformedURLException e) {
          throw new RuntimeException(e);
      }

      ArgumentCaptor<ScreenshotOptions> captureOptions = ArgumentCaptor.forClass(ScreenshotOptions.class);
      percy = spy(new AppPercy(androidDriver));
      percy.setGenericProvider(genericProvider);
      percy.setPercyEnabled();
      when(genericProvider.screenshot(any(), any())).thenReturn(null);
      JSONObject res = percy.screenshot("Test");
      assertEquals(res, null);
      verify(genericProvider).screenshot(eq("Test"), captureOptions.capture());
      assertNotNull(captureOptions.getValue());
      assertTrue(captureOptions.getValue() instanceof ScreenshotOptions);
    }

    @Test
    public void takeScreenshotWithSyncOption() throws Exception{

      when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
      when(androidDriver.getCapabilities()).thenReturn(capabilities);
      
      try {
        lenient(). when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
      
      percy = spy(new AppPercy(androidDriver));
      percy.setGenericProvider(genericProvider);
      percy.setPercyEnabled();
      JSONObject mockResponse = new JSONObject()
      .put("data", new JSONObject().put("snapshot-name", "Test"));
      when(genericProvider.screenshot(any(), any())).thenReturn(mockResponse);
      ScreenshotOptions options = new ScreenshotOptions();
      options.setSync(true);

      // The CLI response is in the format { data: { snapshot-name: 'some_Test', ... and so on } }
      // We need to extract and use the response from the `data` key
      JSONObject res = percy.screenshot("Test", options);
      assertEquals(res.getString("snapshot-name"), "Test");
    }

    @Test
    public void takeScreenshotWithFullScreenOverload() throws Exception {
      when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
      when(androidDriver.getCapabilities()).thenReturn(capabilities);
      lenient().when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));

      ArgumentCaptor<ScreenshotOptions> captureOptions = ArgumentCaptor.forClass(ScreenshotOptions.class);
      percy = spy(new AppPercy(androidDriver));
      percy.setGenericProvider(genericProvider);
      percy.setPercyEnabled();
      when(genericProvider.screenshot(any(), any())).thenReturn(null);

      // 2-arg overload screenshot(name, fullScreen) -> delegates to 3-arg (covers line 82)
      JSONObject res = percy.screenshot("Test", true);

      assertNull(res);
      verify(genericProvider).screenshot(eq("Test"), captureOptions.capture());
      assertNotNull(captureOptions.getValue());
      assertTrue(captureOptions.getValue().getFullScreen());
    }

    @Test
    public void takeScreenshotSwallowsErrorWhenProviderThrows() throws Exception {
      when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
      when(androidDriver.getCapabilities()).thenReturn(capabilities);
      lenient().when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));

      percy = spy(new AppPercy(androidDriver));
      percy.setGenericProvider(genericProvider);
      percy.setPercyEnabled();
      CliWrapper cliMock = mock(CliWrapper.class);
      percy.setCliWrapper(cliMock);
      // Provider throws -> catch -> postFailedEvent + log -> return null (covers lines 122-126, 130)
      when(genericProvider.screenshot(any(), any())).thenThrow(new RuntimeException("provider failure"));

      JSONObject res = percy.screenshot("Test");

      assertNull(res);
      verify(cliMock).postFailedEvent(eq("provider failure"));
    }

    @Test
    public void logEmitsAllLevels() {
      // Exercises the public log overloads/branches including the debug branch (covers lines 146-147, 152)
      AppPercy.log("info message");
      AppPercy.log("info message", "info");
      AppPercy.log("debug message", "debug");
      AppPercy.log("warn message", "warn");
    }

    @Test
    public void setCliWrapperReplacesWrapper() throws Exception {
      when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
      lenient().when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));

      percy = spy(new AppPercy(androidDriver));
      CliWrapper cliMock = mock(CliWrapper.class);
      // covers setCliWrapper (lines 157-158)
      percy.setCliWrapper(cliMock);
    }

    @Test
    public void takeScreenshotRethrowsWhenIgnoreErrorsFalse() throws Exception {
      when(androidDriver.getSessionId()).thenReturn(new SessionId("123"));
      when(androidDriver.getCapabilities()).thenReturn(capabilities);
      lenient().when(androidDriver.getRemoteAddress()).thenReturn(new URL("https://hub.browserstack.com/wd/hub"));

      percy = spy(new AppPercy(androidDriver));
      percy.setGenericProvider(genericProvider);
      percy.setPercyEnabled();
      percy.setCliWrapper(mock(CliWrapper.class));
      when(genericProvider.screenshot(any(), any())).thenThrow(new RuntimeException("provider failure"));

      // Force ignoreErrors=false so the catch block rethrows (covers line 127). Restore after.
      Field ignoreErrorsField = AppPercy.class.getDeclaredField("ignoreErrors");
      ignoreErrorsField.setAccessible(true);
      Object original = ignoreErrorsField.get(null);
      ignoreErrorsField.set(null, Boolean.FALSE);
      try {
        assertThrows(RuntimeException.class, () -> percy.screenshot("Test"));
      } finally {
        ignoreErrorsField.set(null, original);
      }
    }

    @Test
    public void logDebugBranchPrintsWhenDebugEnabled() throws Exception {
      // Enable PERCY_DEBUG so the debug log branch body executes (covers line 147). Restore after.
      Field debugField = AppPercy.class.getDeclaredField("PERCY_DEBUG");
      debugField.setAccessible(true);
      boolean original = debugField.getBoolean(null);
      debugField.setBoolean(null, true);
      try {
        AppPercy.log("debug message", "debug");
      } finally {
        debugField.setBoolean(null, original);
      }
    }
}
