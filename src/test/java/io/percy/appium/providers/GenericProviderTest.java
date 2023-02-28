package io.percy.appium.providers;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.ScreenshotOptions;
import io.percy.appium.lib.Tile;
import io.percy.appium.metadata.AndroidMetadata;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import com.github.javafaker.Faker;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class GenericProviderTest {
    @Mock
    AndroidDriver androidDriver;

    @Mock
    Capabilities capabilities;

    Faker faker = new Faker();
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);

    HashMap<String, Long> viewportRect = new HashMap<String, Long>();
    HashMap<String, HashMap<String, Long>> sessionValue = new HashMap<String, HashMap<String, Long>>();
    Response session = new Response(new SessionId("abc"));

    @Before
    public void setup() {
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        when(capabilities.getCapability("deviceScreenSize")).thenReturn("1080x2160");
    }

    @Test
    public void testGetTag(){
        when(capabilities.getCapability("device")).thenReturn("Samsung Galaxy s22");
        when(capabilities.getCapability("platformName")).thenReturn("Android");
        when(capabilities.getCapability("platformVersion")).thenReturn("9");

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));
        
        JSONObject tile = genericProvider.getTag();
        Assert.assertEquals(tile.get("name"), "Samsung Galaxy s22");
        Assert.assertEquals(tile.get("osName"), "Android");
        Assert.assertEquals(tile.get("osVersion"), "9");
        Assert.assertEquals(tile.get("width"), 1080);
        Assert.assertEquals(tile.get("height"), 2160);
        Assert.assertEquals(tile.get("orientation"), "portrait");
    }

    @Test
    public void testcaptureTiles() throws IOException, Exception {
        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPageScreenshot(false);
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        sessionValue.put("viewportRect", viewportRect);
        session.setValue(sessionValue);
        when(androidDriver.getScreenshotAs(OutputType.BASE64))
                .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPAAAADwCAYAAAA+VemSAAAgAEl...==");
        when(androidDriver.execute("getSession")).thenReturn(session);

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));

        Tile tile = genericProvider.captureTiles(false, options).get(0);
        Assert.assertTrue(tile.getLocalFilePath().endsWith(".png"));
        Assert.assertEquals(tile.getStatusBarHeight().intValue(), top.intValue());
        Assert.assertEquals(tile.getNavBarHeight().intValue(), 2160 - (height + top));
        Assert.assertEquals(tile.getHeaderHeight().intValue(), 0);
        Assert.assertEquals(tile.getFooterHeight().intValue(), 0);
        Assert.assertEquals(tile.getFullScreen(), false);
    }

    @Test
    public void testSupports() {
        Assert.assertEquals(GenericProvider.supports(androidDriver), true);
    }

    @Test
    public void testGetSetMetadata() {
        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);
        Assert.assertEquals(genericProvider.getMetadata(), metadata);
    }

}
