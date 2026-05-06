package io.percy.appium.cucumber;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class PercyStepsTest {

    @Mock
    AndroidDriver mockDriver;

    private PercySteps steps;

    @Before
    public void setUp() {
        mockDriver = mock(AndroidDriver.class);
        steps = new PercySteps();
    }

    @After
    public void tearDown() {
        PercySteps.reset();
    }

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

    @Test(expected = IllegalStateException.class)
    public void testPercyShouldBeEnabledThrowsWithoutInit() {
        steps.percyShouldBeEnabled();
    }

    @Test
    public void testPercyShouldBeEnabledSucceeds() {
        PercySteps.setDriver(mockDriver);
        steps.percyShouldBeEnabled();
    }

    @Test
    public void testSetThTestCaseExecutionId() {
        PercySteps.setDriver(mockDriver);
        steps.iSetThTestCaseExecutionId("exec-123");
    }
}
