package io.percy.appium.cucumber;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.Percy;
import io.percy.appium.lib.ScreenshotOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionId;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class PercyStepsTest {

    @Mock
    AndroidDriver mockDriver;

    private Percy mockPercy;
    private PercySteps steps;

    @Before
    public void setUp() {
        mockDriver = mock(AndroidDriver.class);
        lenient().when(mockDriver.getSessionId()).thenReturn(new SessionId("test-session-id"));
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        lenient().when(mockDriver.getCapabilities()).thenReturn(capabilities);
        try {
            lenient().when(mockDriver.getRemoteAddress())
                .thenReturn(new URL("https://hub.example.com/wd/hub"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        mockPercy = mock(Percy.class);
        steps = new PercySteps();
    }

    @After
    public void tearDown() {
        PercySteps.reset();
    }

    private void initWithMockPercy() {
        PercySteps.setDriver(mockDriver);
        setPercyField(mockPercy);
    }

    private void setPercyField(Percy percy) {
        setStaticField("percy", percy);
    }

    private void setStaticField(String name, Object value) {
        try {
            Field field = PercySteps.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------------------------------------------------------
    // Lifecycle tests
    // ------------------------------------------------------------------

    @Test
    public void testSetDriverAndGetPercy() {
        PercySteps.setDriver(mockDriver);
        assertNotNull(PercySteps.getPercy());
    }

    @Test
    public void testResetClearsState() {
        PercySteps.setDriver(mockDriver);
        assertNotNull(PercySteps.getPercy());

        PercySteps.reset();
        assertNull(PercySteps.getPercy());
    }

    @Test(expected = IllegalStateException.class)
    public void testIHaveAPercyInstanceThrowsWithoutDriver() {
        steps.iHaveAPercyInstance();
    }

    @Test
    public void testIHaveAPercyInstanceSucceedsWithDriver() {
        PercySteps.setDriver(mockDriver);
        steps.iHaveAPercyInstance();
        assertNotNull(PercySteps.getPercy());
    }

    // ------------------------------------------------------------------
    // Option setter tests
    // ------------------------------------------------------------------

    @Test
    public void testSetDeviceName() {
        PercySteps.setDriver(mockDriver);
        steps.iSetDeviceName("iPhone 14");
    }

    @Test
    public void testSetOrientation() {
        PercySteps.setDriver(mockDriver);
        steps.iSetOrientation("landscape");
    }

    @Test
    public void testSetStatusBarHeight() {
        PercySteps.setDriver(mockDriver);
        steps.iSetStatusBarHeight(44);
    }

    @Test
    public void testSetNavBarHeight() {
        PercySteps.setDriver(mockDriver);
        steps.iSetNavBarHeight(48);
    }

    @Test
    public void testSetFullPage() {
        PercySteps.setDriver(mockDriver);
        steps.iSetFullPage("true");
    }

    @Test
    public void testSetScreenLengths() {
        PercySteps.setDriver(mockDriver);
        steps.iSetScreenLengths(3);
    }

    @Test
    public void testSetTestCase() {
        PercySteps.setDriver(mockDriver);
        steps.iSetTestCase("TC-001");
    }

    @Test
    public void testSetLabels() {
        PercySteps.setDriver(mockDriver);
        steps.iSetLabels("regression,smoke");
    }

    @Test
    public void testSetSync() {
        PercySteps.setDriver(mockDriver);
        steps.iSetSync("true");
    }

    @Test
    public void testSetScrollableXpath() {
        PercySteps.setDriver(mockDriver);
        steps.iSetScrollableXpath("//android.widget.ScrollView");
    }

    @Test
    public void testSetScrollableId() {
        PercySteps.setDriver(mockDriver);
        steps.iSetScrollableId("scrollview_1");
    }

    @Test
    public void testSetTopScrollviewOffset() {
        PercySteps.setDriver(mockDriver);
        steps.iSetTopScrollviewOffset(100);
    }

    @Test
    public void testSetBottomScrollviewOffset() {
        PercySteps.setDriver(mockDriver);
        steps.iSetBottomScrollviewOffset(50);
    }

    @Test
    public void testSetScrollSpeed() {
        PercySteps.setDriver(mockDriver);
        steps.iSetScrollSpeed(200);
    }

    @Test
    public void testSetAndroidScrollAreaPercentage() {
        PercySteps.setDriver(mockDriver);
        steps.iSetAndroidScrollAreaPercentage(80);
    }

    @Test
    public void testSetThTestCaseExecutionId() {
        PercySteps.setDriver(mockDriver);
        steps.iSetThTestCaseExecutionId("exec-123");
    }

    // ------------------------------------------------------------------
    // Region tests
    // ------------------------------------------------------------------

    @Test
    public void testAddIgnoreRegionXPath() {
        PercySteps.setDriver(mockDriver);
        steps.iAddIgnoreRegionXPath("//android.widget.Button[@text='AD']");
    }

    @Test
    public void testAddIgnoreRegionAccessibilityId() {
        PercySteps.setDriver(mockDriver);
        steps.iAddIgnoreRegionAccessibilityId("ad_banner");
    }

    @Test
    public void testAddConsiderRegionXPath() {
        PercySteps.setDriver(mockDriver);
        steps.iAddConsiderRegionXPath("//android.widget.TextView");
    }

    @Test
    public void testAddConsiderRegionAccessibilityId() {
        PercySteps.setDriver(mockDriver);
        steps.iAddConsiderRegionAccessibilityId("main_content");
    }

    @Test
    public void testAddCustomIgnoreRegion() {
        PercySteps.setDriver(mockDriver);
        steps.iAddCustomIgnoreRegion(0, 100, 0, 200);
    }

    @Test
    public void testAddCustomConsiderRegion() {
        PercySteps.setDriver(mockDriver);
        steps.iAddCustomConsiderRegion(10, 50, 10, 150);
    }

    @Test
    public void testClearPercyOptions() {
        PercySteps.setDriver(mockDriver);
        steps.iAddIgnoreRegionXPath("//button");
        steps.iClearPercyOptions();
    }

    // ------------------------------------------------------------------
    // Screenshot tests
    // ------------------------------------------------------------------

    @Test
    public void testTakeScreenshot() {
        initWithMockPercy();
        steps.iTakeScreenshot("Homepage");
        verify(mockPercy).screenshot("Homepage");
    }

    @Test
    public void testTakeScreenshotFullPage() {
        initWithMockPercy();
        steps.iTakeScreenshotFullPage("Full Page");
        verify(mockPercy).screenshot(eq("Full Page"), eq(true));
    }

    @Test
    public void testTakeScreenshotFullScreen() {
        initWithMockPercy();
        steps.iTakeScreenshotFullScreen("Full Screen");
        ArgumentCaptor<ScreenshotOptions> captor = ArgumentCaptor.forClass(ScreenshotOptions.class);
        verify(mockPercy).screenshot(eq("Full Screen"), captor.capture());
        assertTrue(captor.getValue().getFullScreen());
    }

    @Test
    public void testTakeScreenshotWithOptions() {
        initWithMockPercy();
        steps.iSetDeviceName("Pixel 6");
        steps.iSetOrientation("portrait");
        steps.iAddIgnoreRegionXPath("//ad");
        steps.iTakeScreenshotWithOptions("With Options");
        ArgumentCaptor<ScreenshotOptions> captor = ArgumentCaptor.forClass(ScreenshotOptions.class);
        verify(mockPercy).screenshot(eq("With Options"), captor.capture());
        ScreenshotOptions opts = captor.getValue();
        assertEquals("Pixel 6", opts.getDeviceName());
        assertEquals("portrait", opts.getOrientation());
        assertEquals(1, opts.getIgnoreRegionXpaths().size());
        assertEquals("//ad", opts.getIgnoreRegionXpaths().get(0));
    }

    @Test
    public void testTakeScreenshotWithOptionsResetsAfterCall() {
        initWithMockPercy();
        steps.iSetDeviceName("Pixel 6");
        steps.iTakeScreenshotWithOptions("First");

        steps.iTakeScreenshotWithOptions("Second");
        ArgumentCaptor<ScreenshotOptions> captor = ArgumentCaptor.forClass(ScreenshotOptions.class);
        verify(mockPercy).screenshot(eq("Second"), captor.capture());
        assertNull(captor.getValue().getDeviceName());
    }

    @Test
    public void testTakeScreenshotFullScreenResetsAfterCall() {
        initWithMockPercy();
        steps.iSetDeviceName("iPhone 14");
        steps.iTakeScreenshotFullScreen("FS");

        steps.iTakeScreenshotWithOptions("After FS");
        ArgumentCaptor<ScreenshotOptions> captor = ArgumentCaptor.forClass(ScreenshotOptions.class);
        verify(mockPercy).screenshot(eq("After FS"), captor.capture());
        assertNull(captor.getValue().getDeviceName());
        assertFalse(captor.getValue().getFullScreen());
    }

    @Test
    public void testTakeScreenshotWithIgnoreAndConsiderRegions() {
        initWithMockPercy();
        steps.iAddIgnoreRegionXPath("//ad");
        steps.iAddIgnoreRegionAccessibilityId("banner");
        steps.iAddConsiderRegionXPath("//main");
        steps.iAddConsiderRegionAccessibilityId("content");
        steps.iAddCustomIgnoreRegion(0, 100, 0, 200);
        steps.iAddCustomConsiderRegion(10, 50, 10, 150);
        steps.iTakeScreenshotWithOptions("Regions");
        ArgumentCaptor<ScreenshotOptions> captor = ArgumentCaptor.forClass(ScreenshotOptions.class);
        verify(mockPercy).screenshot(eq("Regions"), captor.capture());
        ScreenshotOptions opts = captor.getValue();
        assertEquals(1, opts.getIgnoreRegionXpaths().size());
        assertEquals(1, opts.getIgnoreRegionAccessibilityIds().size());
        assertEquals(1, opts.getConsiderRegionXpaths().size());
        assertEquals(1, opts.getConsiderRegionAccessibilityIds().size());
        assertEquals(1, opts.getCustomIgnoreRegions().size());
        assertEquals(1, opts.getCustomConsiderRegions().size());
    }

    // ------------------------------------------------------------------
    // Then step tests
    // ------------------------------------------------------------------

    @Test(expected = IllegalStateException.class)
    public void testPercyShouldBeEnabledThrowsWithoutInit() {
        steps.percyShouldBeEnabled();
    }

    @Test
    public void testPercyShouldBeEnabledSucceeds() {
        PercySteps.setDriver(mockDriver);
        steps.percyShouldBeEnabled();
    }

    // ------------------------------------------------------------------
    // iHaveAPercyInstance: re-init branch (driver set, percy null) -> line 121
    // ------------------------------------------------------------------

    @Test
    public void testIHaveAPercyInstanceReinitializesPercyWhenNull() {
        // Driver present but percy cleared: forces percy = new Percy(driver).
        setStaticField("driver", mockDriver);
        setStaticField("percy", null);

        steps.iHaveAPercyInstance();

        assertNotNull(PercySteps.getPercy());
    }

    // ------------------------------------------------------------------
    // Appium element regions -> lines 275-280 and 284-289
    // ------------------------------------------------------------------

    @Test
    public void testAddIgnoreRegionAppiumElement() {
        PercySteps.setDriver(mockDriver);
        org.openqa.selenium.WebElement mockElement = mock(org.openqa.selenium.WebElement.class);
        when(mockDriver.findElement(org.openqa.selenium.By.xpath("//ignore-el")))
            .thenReturn(mockElement);

        steps.iAddIgnoreRegionAppiumElement("//ignore-el");

        verify(mockDriver).findElement(org.openqa.selenium.By.xpath("//ignore-el"));

        ScreenshotOptions opts = currentOptions();
        assertEquals(1, opts.getIgnoreRegionAppiumElements().size());
        assertSame(mockElement, opts.getIgnoreRegionAppiumElements().get(0));
    }

    @Test
    public void testAddConsiderRegionAppiumElement() {
        PercySteps.setDriver(mockDriver);
        org.openqa.selenium.WebElement mockElement = mock(org.openqa.selenium.WebElement.class);
        when(mockDriver.findElement(org.openqa.selenium.By.xpath("//consider-el")))
            .thenReturn(mockElement);

        steps.iAddConsiderRegionAppiumElement("//consider-el");

        verify(mockDriver).findElement(org.openqa.selenium.By.xpath("//consider-el"));

        ScreenshotOptions opts = currentOptions();
        assertEquals(1, opts.getConsiderRegionAppiumElements().size());
        assertSame(mockElement, opts.getConsiderRegionAppiumElements().get(0));
    }

    // ------------------------------------------------------------------
    // ensureOptions: re-creates options when null -> line 342
    // ------------------------------------------------------------------

    @Test
    public void testEnsureOptionsRecreatesWhenNull() {
        PercySteps.setDriver(mockDriver);
        // Clear the stored options so ensureOptions() must allocate a fresh instance.
        setStaticField("screenshotOptions", null);

        steps.iSetDeviceName("Pixel 7");

        ScreenshotOptions opts = currentOptions();
        assertNotNull(opts);
        assertEquals("Pixel 7", opts.getDeviceName());
    }

    // ------------------------------------------------------------------
    // resolveCucumberVersion: value, null-fallback, and exception-fallback
    // ------------------------------------------------------------------

    @Test
    public void testResolveCucumberVersionReturnsSuppliedValue() {
        assertEquals("7.18.0", PercySteps.resolveCucumberVersion(() -> "7.18.0"));
    }

    @Test
    public void testResolveCucumberVersionFallsBackWhenNull() {
        assertEquals("unknown", PercySteps.resolveCucumberVersion(() -> null));
    }

    @Test
    public void testResolveCucumberVersionFallsBackWhenSupplierThrows() {
        assertEquals("unknown", PercySteps.resolveCucumberVersion(() -> {
            throw new RuntimeException("boom");
        }));
    }

    private ScreenshotOptions currentOptions() {
        try {
            Field field = PercySteps.class.getDeclaredField("screenshotOptions");
            field.setAccessible(true);
            return (ScreenshotOptions) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
