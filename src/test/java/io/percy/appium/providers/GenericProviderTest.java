package io.percy.appium.providers;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.lib.Cache;
import io.percy.appium.lib.Region;
import io.percy.appium.lib.ScreenshotOptions;
import io.percy.appium.lib.Tile;
import io.percy.appium.metadata.AndroidMetadata;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
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
        viewportRect.put("top", top);
        viewportRect.put("height", height);
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

    // Targets lines 53-54: fullPage requested but provider is not App Automate,
    // so it logs the fallback message and still returns a single-page tile.
    @Test
    public void testCaptureTilesFullPageFallsBackWhenNotAppAutomate()
            throws MalformedURLException, IOException, Exception {
        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(true);
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        // Non-browserstack remote address => AppAutomate.supports(driver) == false
        when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://example.com/"));
        when(androidDriver.getScreenshotAs(OutputType.BASE64))
                .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));

        List<Tile> tiles = genericProvider.captureTiles(options);
        Assert.assertEquals(1, tiles.size());
        Tile tile = tiles.get(0);
        Assert.assertTrue(tile.getLocalFilePath().endsWith(".png"));
        Assert.assertEquals(tile.getStatusBarHeight().intValue(), top.intValue());
        Assert.assertEquals(tile.getNavBarHeight().intValue(), 2160 - (height + top));
    }

    // Targets lines 82-84: getAbsolutePath catch block. We point java.io.tmpdir at a
    // read-only directory so FileOutputStream fails with an IOException, which the
    // provider logs and rethrows.
    @Test
    public void testCaptureTilesThrowsWhenFileCannotBeWritten() throws Exception {
        File readOnlyDir = Files.createTempDirectory("percy-readonly").toFile();
        boolean madeReadOnly = readOnlyDir.setWritable(false, false);
        // If the environment ignores read-only dirs (e.g. running as root), this scenario
        // is not reproducible; skip without registering any unnecessary stubs.
        org.junit.Assume.assumeTrue(madeReadOnly && !readOnlyDir.canWrite());

        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(false);
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        when(androidDriver.getScreenshotAs(OutputType.BASE64))
                .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));

        String originalTmpDir = System.getProperty("java.io.tmpdir");
        try {
            System.setProperty("java.io.tmpdir", readOnlyDir.getAbsolutePath());
            try {
                genericProvider.captureTiles(options);
                Assert.fail("Expected IOException when target directory is not writable");
            } catch (IOException expected) {
                Assert.assertNotNull(expected);
            }
        } finally {
            if (originalTmpDir != null) {
                System.setProperty("java.io.tmpdir", originalTmpDir);
            }
            readOnlyDir.setWritable(true, false);
            readOnlyDir.delete();
        }
    }

    // Targets line 104: the two-arg screenshot overload delegates to the four-arg one.
    // postScreenshot has no local Percy server to reach, so it returns null after logging.
    @Test
    public void testScreenshotTwoArgOverloadDelegates() throws Exception {
        when(capabilities.getCapability("device")).thenReturn("Samsung Galaxy s22");
        when(capabilities.getCapability("platformName")).thenReturn("Android");
        when(capabilities.getCapability("platformVersion")).thenReturn("9");
        when(androidDriver.getScreenshotAs(OutputType.BASE64))
                .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");

        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(false);

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        // No Percy server is running in the test env, so postScreenshot returns null.
        JSONObject result = genericProvider.screenshot("two-arg-screenshot", options);
        Assert.assertNull(result);
    }

    // Targets lines 108-111: getObjectForArray wraps a JSONArray under the given key.
    // Exercised indirectly through the full screenshot() path below as well, but we
    // also drive the full four-arg screenshot here to cover 116-132 and 137-142.
    @Test
    public void testScreenshotFourArgBuildsRequestAndReturnsNull() throws Exception {
        when(capabilities.getCapability("device")).thenReturn("Samsung Galaxy s22");
        when(capabilities.getCapability("platformName")).thenReturn("Android");
        when(capabilities.getCapability("platformVersion")).thenReturn("9");
        when(androidDriver.getScreenshotAs(OutputType.BASE64))
                .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");

        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(false);
        // Populate ignore/consider regions so findRegions + getObjectForArray run with data.
        when(androidDriver.findElement(By.xpath("//ignore"))).thenReturn(mockElement);
        when(androidDriver.findElement(By.id("considerId"))).thenReturn(mockElement);
        when(mockElement.getLocation()).thenReturn(new Point(5, 5));
        when(mockElement.getSize()).thenReturn(new Dimension(10, 10));
        options.setIgnoreRegionXpaths(new ArrayList<>(Arrays.asList("//ignore")));
        options.setConsiderRegionAccessibilityIds(new ArrayList<>(Arrays.asList("considerId")));

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        // metadata is resolved internally via MetadataHelper.resolve. Passing null
        // platformVersion/deviceName forces metadata to read them from capabilities.
        JSONObject result = genericProvider.screenshot("four-arg-screenshot", options, null, null);
        // No Percy server => postScreenshot returns null after logging.
        Assert.assertNull(result);
    }

    // Targets lines 108-111 directly via the full screenshot path: ensures findRegions
    // wrapping works when regions are empty (default ScreenshotOptions).
    @Test
    public void testScreenshotFourArgWithEmptyRegions() throws Exception {
        when(capabilities.getCapability("device")).thenReturn("Samsung Galaxy s22");
        when(capabilities.getCapability("platformName")).thenReturn("Android");
        when(capabilities.getCapability("platformVersion")).thenReturn("9");
        when(androidDriver.getScreenshotAs(OutputType.BASE64))
                .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");

        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(false);

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        JSONObject result = genericProvider.screenshot("empty-regions", options, null, null);
        Assert.assertNull(result);
    }

    // Targets lines 154-155: setDebugUrl stores the value (verified indirectly via the
    // screenshot path, which is the only public reader). Here we just assert the call
    // is accepted and does not interfere with a subsequent screenshot.
    @Test
    public void testSetDebugUrl() throws Exception {
        when(capabilities.getCapability("device")).thenReturn("Samsung Galaxy s22");
        when(capabilities.getCapability("platformName")).thenReturn("Android");
        when(capabilities.getCapability("platformVersion")).thenReturn("9");
        when(androidDriver.getScreenshotAs(OutputType.BASE64))
                .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setDebugUrl("https://example.com/debug");

        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(false);
        JSONObject result = genericProvider.screenshot("with-debug-url", options, null, null);
        Assert.assertNull(result);
    }

    // Targets lines 162-168: findRegions aggregates xpath, id, element and location
    // regions into a single JSONArray.
    @Test
    public void testFindRegionsAggregatesAllSources() {
        Point location = new Point(10, 20);
        Dimension size = new Dimension(100, 200);
        when(androidDriver.findElement(By.xpath("//x"))).thenReturn(mockElement);
        when(androidDriver.findElement(By.id("anId"))).thenReturn(mockElement);
        when(mockElement.getLocation()).thenReturn(location);
        when(mockElement.getSize()).thenReturn(size);
        when(mockElement.getAttribute("class")).thenReturn("Button");

        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);

        List<String> xpaths = new ArrayList<>(Arrays.asList("//x"));
        List<String> ids = new ArrayList<>(Arrays.asList("anId"));
        List<WebElement> elements = new ArrayList<>(Arrays.asList((WebElement) mockElement));
        Region customRegion = new Region(50, 100, 200, 250);
        List<Region> locations = new ArrayList<>(Arrays.asList(customRegion));

        JSONArray regions = genericProvider.findRegions(xpaths, ids, elements, locations);

        // one per source: xpath, id, element, custom location
        Assert.assertEquals(4, regions.length());
        Assert.assertEquals("xpath: //x", regions.getJSONObject(0).getString("selector"));
        Assert.assertEquals("id: anId", regions.getJSONObject(1).getString("selector"));
        Assert.assertEquals("element: 0 Button", regions.getJSONObject(2).getString("selector"));
        Assert.assertEquals("custom region 0", regions.getJSONObject(3).getString("selector"));
    }

    // Targets lines 195-197: getRegionsByXpath catch block when findElement throws.
    @Test
    public void testGetRegionsByXpathSwallowsExceptionForMissingElement() {
        JSONArray elementsArray = new JSONArray();
        List<String> xpaths = new ArrayList<>(Arrays.asList("//missing"));
        when(androidDriver.findElement(By.xpath("//missing")))
                .thenThrow(new RuntimeException("no such element"));

        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);

        genericProvider.getRegionsByXpath(elementsArray, xpaths);

        // Missing element is ignored, leaving the array empty.
        Assert.assertEquals(0, elementsArray.length());
    }

    // Targets lines 210-212: getRegionsByIds catch block when findElement throws.
    @Test
    public void testGetRegionsByIdsSwallowsExceptionForMissingElement() {
        JSONArray elementsArray = new JSONArray();
        List<String> ids = new ArrayList<>(Arrays.asList("missingId"));
        when(androidDriver.findElement(By.id("missingId")))
                .thenThrow(new RuntimeException("no such element"));

        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);

        genericProvider.getRegionsByIds(elementsArray, ids);

        Assert.assertEquals(0, elementsArray.length());
    }

    // Targets lines 225-227: getRegionsByElements catch block when the element throws.
    @Test
    public void testGetRegionsByElementsSwallowsExceptionForBadElement() {
        JSONArray elementsArray = new JSONArray();
        when(mockElement.getAttribute("class")).thenThrow(new RuntimeException("bad element"));
        List<WebElement> elements = new ArrayList<>(Arrays.asList((WebElement) mockElement));

        AndroidMetadata metadata = new AndroidMetadata(androidDriver, "dummy", null, null, null, null);
        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);

        genericProvider.getRegionsByElements(elementsArray, elements);

        Assert.assertEquals(0, elementsArray.length());
    }

    // Targets lines 253-254: getRegionsByLocation catch block. A mocked Region whose
    // isValid() throws makes the for-loop body raise, which is caught and logged.
    @Test
    public void testGetRegionsByLocationSwallowsExceptionFromRegion() {
        JSONArray elementsArray = new JSONArray();
        AndroidMetadata metadata = Mockito.mock(AndroidMetadata.class);
        when(metadata.deviceScreenWidth()).thenReturn(1080);
        when(metadata.deviceScreenHeight()).thenReturn(2160);

        GenericProvider genericProvider = new GenericProvider(androidDriver);
        genericProvider.setMetadata(metadata);

        // A Region whose isValid() throws by referencing a mocked region that explodes.
        Region throwingRegion = Mockito.mock(Region.class);
        when(throwingRegion.isValid(2160, 1080)).thenThrow(new RuntimeException("boom"));
        List<Region> locations = new ArrayList<>(Arrays.asList(throwingRegion));

        genericProvider.getRegionsByLocation(elementsArray, locations);

        // Exception in the loop body is caught and logged; nothing is added.
        Assert.assertEquals(0, elementsArray.length());
    }
}
