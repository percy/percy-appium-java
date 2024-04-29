package io.percy.appium.providers;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Cache;
import io.percy.appium.lib.Region;
import io.percy.appium.lib.ScreenshotOptions;
import io.percy.appium.lib.Tile;
import io.percy.appium.metadata.AndroidMetadata;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import com.github.javafaker.Faker;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class GenericProviderTest {
    @Mock
    AndroidDriver androidDriver;

    @Mock
    Capabilities capabilities;

    @Mock
    WebElement mockElement;

    Faker faker = new Faker();
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);

    HashMap<String, Long> viewportRect = new HashMap<String, Long>();

    @Before
    public void setup() {
        Cache.CACHE_MAP.clear();
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        when(capabilities.getCapability("deviceScreenSize")).thenReturn("1080x2160");
        when(capabilities.getCapability("viewportRect")).thenReturn(viewportRect);
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
        options.setFullPage(false);
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        when(androidDriver.getScreenshotAs(OutputType.BASE64))
                .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPAAAADwCAYAAAA+VemSAAAgAEl...==");

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));

        Tile tile = genericProvider.captureTiles(options).get(0);
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

    @Test
    public void testGetRegionObject() {
        String selector = "test_selector";
        Point location = new Point(10, 20);
        Dimension size = new Dimension(30, 40);
        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);

        when(mockElement.getLocation()).thenReturn(location);
        when(mockElement.getSize()).thenReturn(size);
        JSONObject result = genericProvider.getRegionObject(selector, mockElement);

        Assert.assertEquals(selector, result.getString("selector"));

        JSONObject coOrdinates = result.getJSONObject("co_ordinates");
        Assert.assertEquals((int) (location.getY()), coOrdinates.getInt("top"));
        Assert.assertEquals((int) ((location.getY() + size.getHeight())), coOrdinates.getInt("bottom"));
        Assert.assertEquals((int) (location.getX()), coOrdinates.getInt("left"));
        Assert.assertEquals((int) ((location.getX() + size.getWidth())), coOrdinates.getInt("right"));
    }

    @Test
    public void testGetRegionsByXpath() {
        JSONArray elementsArray = new JSONArray();
        List<String> xpaths = new ArrayList<>();
        xpaths.add("//div[@class='example']");

        Point location = new Point(10, 20);
        Dimension size = new Dimension(100, 200);

        when(androidDriver.findElement(By.xpath("//div[@class='example']"))).thenReturn(mockElement);
        when(mockElement.getLocation()).thenReturn(location);
        when(mockElement.getSize()).thenReturn(size);

        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);

        genericProvider.getRegionsByXpath(elementsArray, xpaths);

        verify(androidDriver).findElement(By.xpath("//div[@class='example']"));
        JSONObject region = elementsArray.getJSONObject(0);
        Assert.assertEquals("xpath: //div[@class='example']", region.get("selector"));
        JSONObject coOrdinates = region.getJSONObject("co_ordinates");
        Assert.assertEquals(location.getY(), coOrdinates.getInt("top"));
        Assert.assertEquals((location.getY() + size.getHeight()), coOrdinates.getInt("bottom"));
        Assert.assertEquals(location.getX(), coOrdinates.getInt("left"));
        Assert.assertEquals((location.getX() + size.getWidth()), coOrdinates.getInt("right"));
    }

    @Test
    public void testGetRegionsByIds() {
        JSONArray elementsArray = new JSONArray();
        List<String> ids = new ArrayList<>();
        ids.add("some id");

        Point location = new Point(10, 20);
        Dimension size = new Dimension(100, 200);

        when(androidDriver.findElement(By.id("some id"))).thenReturn(mockElement);
        when(mockElement.getLocation()).thenReturn(location);
        when(mockElement.getSize()).thenReturn(size);

        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);

        genericProvider.getRegionsByIds(elementsArray, ids);

        verify(androidDriver).findElement(By.id("some id"));
        JSONObject region = elementsArray.getJSONObject(0);
        Assert.assertEquals("id: some id", region.get("selector"));
        JSONObject coOrdinates = region.getJSONObject("co_ordinates");
        Assert.assertEquals(location.getY(), coOrdinates.getInt("top"));
        Assert.assertEquals((location.getY() + size.getHeight()), coOrdinates.getInt("bottom"));
        Assert.assertEquals(location.getX(), coOrdinates.getInt("left"));
        Assert.assertEquals((location.getX() + size.getWidth()), coOrdinates.getInt("right"));
    }

    @Test
    public void testGetRegionsByElement() {
        JSONArray elementsArray = new JSONArray();
        Point location = new Point(10, 20);
        Dimension size = new Dimension(100, 200);
        List<WebElement> elements = new ArrayList<>();
        when(mockElement.getLocation()).thenReturn(location);
        when(mockElement.getSize()).thenReturn(size);
        when(mockElement.getAttribute("class")).thenReturn("Button");
        elements.add(mockElement);

        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);
        genericProvider.getRegionsByElements(elementsArray, elements);

        String expectedSelector1 = "element: 0 Button";
        Assert.assertEquals(1, elementsArray.length());
        Assert.assertEquals(expectedSelector1, elementsArray.getJSONObject(0).getString("selector"));
        JSONObject region = elementsArray.getJSONObject(0);
        JSONObject coOrdinates = region.getJSONObject("co_ordinates");
        Assert.assertEquals(location.getY(), coOrdinates.getInt("top"));
        Assert.assertEquals((location.getY() + size.getHeight()), coOrdinates.getInt("bottom"));
        Assert.assertEquals(location.getX(), coOrdinates.getInt("left"));
        Assert.assertEquals((location.getX() + size.getWidth()), coOrdinates.getInt("right"));
    }

    @Test
    public void testGetRegionsByLocation() {
        JSONArray elementsArray = new JSONArray();
        List<Region> customLocations = new ArrayList<>();

        Region customRegion = new Region(0, 0, 0, 0);
        customRegion.setTop(50);
        customRegion.setBottom(100);
        customRegion.setLeft(200);
        customRegion.setRight(250);
        // Dimensions are 1080x2160
        Region invaliCustomRegion1 = new Region(2299, 2230, 200, 250);
        Region invaliCustomRegion2 = new Region(50, 100, 1070, 1100);
        customLocations.add(customRegion);
        customLocations.add(invaliCustomRegion1);
        customLocations.add(invaliCustomRegion2);

        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);
        genericProvider.getRegionsByLocation(elementsArray, customLocations);

        Assert.assertEquals(1, elementsArray.length());
        JSONObject region = elementsArray.getJSONObject(0);
        JSONObject coordinates = region.getJSONObject("co_ordinates");
        Assert.assertEquals("custom region 0", region.getString("selector"));
        Assert.assertEquals(50, coordinates.getInt("top"));
        Assert.assertEquals(100, coordinates.getInt("bottom"));
        Assert.assertEquals(200, coordinates.getInt("left"));
        Assert.assertEquals(250, coordinates.getInt("right"));
    }
}
