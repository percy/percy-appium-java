package io.percy.appium;

import io.percy.appium.lib.ScreenshotOptions;
import io.percy.appium.providers.GenericProvider;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

import org.openqa.selenium.remote.*;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class AppPercyTest{
    @Mock
    AndroidDriver<AndroidElement> androidDriver;
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
}
