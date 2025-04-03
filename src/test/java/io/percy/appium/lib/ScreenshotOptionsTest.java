package io.percy.appium.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class ScreenshotOptionsTest {
  @Test
    public void testCastMobileElementsToWebElements() {
        ScreenshotOptions casting = new ScreenshotOptions();

        // Create a list of MobileElements
        List<Object> mobileElements = new ArrayList<>();
        mobileElements.add(new RemoteWebElement() {});
        mobileElements.add(new Object());
        mobileElements.add(new Object());
        mobileElements.add(new RemoteWebElement() {});
        mobileElements.add(new RemoteWebElement() {});

        // Call the function to cast them to WebElements
        List<WebElement> webElements = casting.castMobileElementsToWebElements(mobileElements);

        // Check if the sizes of the original and converted lists are the same
        assertEquals(webElements.size(), 3);

        // Check that each element in the converted list is an instance of WebElement
        for (WebElement element : webElements) {
            assertTrue(element instanceof WebElement);
        }
    }

    @Test
    public void testSyncOption() {
        ScreenshotOptions options = new ScreenshotOptions();

        assertEquals(options.getSync(), null);
    }

    @Test
    public void testTestCase() {
        ScreenshotOptions options = new ScreenshotOptions();

        assertEquals(options.getTestCase(), null);
    }

    @Test
    public void testScrollSpeed() {
        // when valid is set
        ScreenshotOptions options = new ScreenshotOptions();
        options.setScrollSpeed(200);
        assertEquals(options.getScrollSpeed(), new Integer(200));

        // when invalid is set
        ScreenshotOptions invalidOptions = new ScreenshotOptions();
        invalidOptions.setScrollSpeed(5001);
        assertEquals(invalidOptions.getScrollSpeed(), null);
    }

    @Test
    public void testAndroidScrollAreaPercentage() {
        // when valid is set
        ScreenshotOptions options = new ScreenshotOptions();
        options.setAndroidScrollAreaPercentage(50);
        assertEquals(options.getAndroidScrollAreaPercentage(), new Integer(50));

        // when invalid is set
        ScreenshotOptions invalidOptions = new ScreenshotOptions();
        invalidOptions.setAndroidScrollAreaPercentage(101);
        assertEquals(invalidOptions.getAndroidScrollAreaPercentage(), null);
    }

    @Test
    public void testThTestCaseExecutionId() {
        ScreenshotOptions options = new ScreenshotOptions();

        assertEquals(options.getThTestCaseExecutionId(), null);
    }

    @Test
    public void testLabelsExecutionId() {
        ScreenshotOptions options = new ScreenshotOptions();

        assertEquals(options.getLabels(), null);
        options.setLabels("app;testing");
        assertEquals(options.getLabels(), "app;testing");
    }

    @Test
    public void testRegions() {
        ScreenshotOptions options = new ScreenshotOptions();
        Map<String, Object> params = new HashMap<>();
        params.put("algorithm", "intelliignore");
        assertEquals(options.getRegions(), null);
        options.setRegions(Arrays.asList(params));
        List<Map<String, Object>> expectedRegions = Arrays.asList(params);
        assertEquals(options.getRegions(), expectedRegions);
    }
}
