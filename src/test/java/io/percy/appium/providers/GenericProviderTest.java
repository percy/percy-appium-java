package io.percy.appium.providers;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Tile;
import io.percy.appium.metadata.AndroidMetadata;

import static org.mockito.Mockito.when;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;

import com.github.javafaker.Faker;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class GenericProviderTest {
    @Mock
    AndroidDriver androidDriver;

    @Mock
    Capabilities capabilities;

    @Mock
    AndroidMetadata metadata;

    Faker faker = new Faker();
    Integer deviceScreenHeight = (int) faker.number().randomNumber(3, false);
    Integer deviceScreenWidth = (int) faker.number().randomNumber(3, false);
    Integer statusBarHeight = (int) faker.number().randomNumber(3, false);
    Integer navigationBarHeight = (int) faker.number().randomNumber(3, false);

    @Test
    public void testGetTag(){

        when(metadata.deviceName()).thenReturn("Samsung Galaxy s22");
        when(metadata.osName()).thenReturn("ANDROID");
        when(metadata.platformVersion()).thenReturn("9");
        when(metadata.orientation()).thenReturn("landscape");
        when(metadata.deviceScreenHeight()).thenReturn(deviceScreenHeight);
        when(metadata.deviceScreenWidth()).thenReturn(deviceScreenWidth);

        GenericProvider genericProvider = new GenericProvider(androidDriver, metadata);
        
        JSONObject tile = genericProvider.getTag();
        Assert.assertEquals(tile.get("name"), "Samsung Galaxy s22");
        Assert.assertEquals(tile.get("osName"), "ANDROID");
        Assert.assertEquals(tile.get("osVersion"), "9");
        Assert.assertEquals(tile.get("width"), deviceScreenWidth);
        Assert.assertEquals(tile.get("height"), deviceScreenHeight);
        Assert.assertEquals(tile.get("orientation"), "landscape");
    }

    @Test
    public void testcaptureTiles() {
        when(androidDriver.getScreenshotAs(OutputType.BASE64)).thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPAAAADwCAYAAAA+VemSAAAgAEl...==");
        when(metadata.statBarHeight()).thenReturn(statusBarHeight);
        when(metadata.navBarHeight()).thenReturn(navigationBarHeight);

        GenericProvider genericProvider = new GenericProvider(androidDriver, metadata);

        Tile tile = genericProvider.captureTiles(false).get(0);
        Assert.assertTrue(tile.localFilePath.contains("/tmp"));
        Assert.assertEquals(tile.statusBarHeight, statusBarHeight);
        Assert.assertEquals(tile.navBarHeight, navigationBarHeight);
        Assert.assertEquals(tile.headerHeight.intValue(), 0);
        Assert.assertEquals(tile.footerHeight.intValue(), 0);
        Assert.assertEquals(tile.fullScreen, false);
    }

    @Test
    public void testSupports() {
        GenericProvider genericProvider = new GenericProvider(androidDriver, metadata);
        Assert.assertEquals(genericProvider.supports(androidDriver), true);
    }

    @Test
    public void testGetDebugUrl() {
        GenericProvider genericProvider = new GenericProvider(androidDriver, metadata);
        Assert.assertEquals(genericProvider.getDebugUrl(), null);
    }

}
