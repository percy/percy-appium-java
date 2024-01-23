package io.percy.appium.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;

import io.appium.java_client.MobileElement;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class ScreenshotOptionsTest {
  @Test
    public void testCastMobileElementsToWebElements() {
        ScreenshotOptions casting = new ScreenshotOptions();

        // Create a list of MobileElements
        List<Object> mobileElements = new ArrayList<>();
        mobileElements.add(new MobileElement() {});
        mobileElements.add(new Object());
        mobileElements.add(new Object());
        mobileElements.add(new MobileElement() {});
        mobileElements.add(new MobileElement() {});

        // Call the function to cast them to WebElements
        List<WebElement> webElements = casting.castMobileElementsToWebElements(mobileElements);

        // Check if the sizes of the original and converted lists are the same
        assertEquals(webElements.size(), 3);

        // Check that each element in the converted list is an instance of WebElement
        for (WebElement element : webElements) {
            assertTrue(element instanceof WebElement);
        }
    }
}