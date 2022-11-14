package io.percy.appium.metadata;

import io.appium.java_client.ios.IOSDriver;
import io.percy.appium.lib.Cache;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver.ImeHandler;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.SessionId;

import com.github.javafaker.Faker;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class IosMetadataTest {

    @Mock
    IOSDriver driver;

    IosMetadata metadata;

    @Mock
    Capabilities capabilities;

    HashMap<String, Long> viewportRect = new HashMap<String, Long>();

    Faker faker = new Faker();
    Long width = faker.number().randomNumber(3, false);
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);

    Options Options = new Options() {

        @Override
        public void addCookie(Cookie cookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void deleteCookieNamed(String name) {
            // TODO Auto-generated method stub

        }

        @Override
        public void deleteCookie(Cookie cookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public void deleteAllCookies() {
            // TODO Auto-generated method stub

        }

        @Override
        public Set<Cookie> getCookies() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Cookie getCookieNamed(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Timeouts timeouts() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ImeHandler ime() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Window window() {
            return new Window() {

                @Override
                public void setSize(Dimension targetSize) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void setPosition(Point targetPosition) {
                    // TODO Auto-generated method stub

                }

                @Override
                public Dimension getSize() {
                    // TODO Auto-generated method stub
                    return new Dimension(100, 200);
                }

                @Override
                public Point getPosition() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public void maximize() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void fullscreen() {
                    // TODO Auto-generated method stub

                }

            };
        }

        @Override
        public Logs logs() {
            // TODO Auto-generated method stub
            return null;
        }

    };

    @Before
    public void setup() {
        viewportRect.put("width", width);
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        when(driver.getCapabilities()).thenReturn(capabilities);
        when(driver.getSessionId()).thenReturn(new SessionId("abc"));
        when(driver.executeScript("mobile: viewportRect")).thenReturn(viewportRect);
        metadata = new IosMetadata(driver);
    }

    @After
    public void clearCache() {
        Cache.CACHE_MAP.clear();
    }

    @Test
    public void testDeviceScreenWidth() {
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), width.intValue());
    }

    @Test
    public void testDeviceScreenWidthFromJson() {
        viewportRect.clear();
        String sessionDetails = "{\"device\":\"iphone 8 plus\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        when(driver.manage()).thenReturn(Options);
        Assert.assertEquals(metadata.deviceScreenWidth().intValue(), 300);
    }

    @Test
    public void testDeviceScreenHeight() {
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), top.intValue() + height.intValue());
    }

    @Test
    public void testDeviceScreenHeightFromJson() {
        viewportRect.clear();
        String sessionDetails = "{\"device\":\"iphone 8 plus\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        when(driver.manage()).thenReturn(Options);
        Assert.assertEquals(metadata.deviceScreenHeight().intValue(), 600);
    }

    @Test
    public void testStatBarHeight() {
        Assert.assertEquals(metadata.statBarHeight().intValue(), top.intValue());
    }

    @Test
    public void testStatBarHeightFromJson() {
        viewportRect.clear();
        String sessionDetails = "{\"device\":\"iPhone 12\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.statBarHeight().intValue(), 141);
    }

    @Test
    public void testNavBarHeight() {
        Assert.assertEquals(metadata.navBarHeight().intValue(), 0);
    }

    @Test
    public void testDeviceName() {
        String sessionDetails = "{\"device\":\"iPhone 12\"}";
        when(driver.executeScript("browserstack_executor: {\"action\": \"getSessionDetails\"}"))
                .thenReturn(sessionDetails);
        Assert.assertEquals(metadata.deviceName(), "iPhone 12");
    }

    @Test
    public void testOsName(){
        when(capabilities.getCapability("platformName")).thenReturn("IOS");
        Assert.assertEquals(metadata.osName(), "IOS");
    }

    @Test
    public void testOsVersion(){
        when(capabilities.getCapability("platformVersion")).thenReturn(null);
        when(capabilities.getCapability("os_version")).thenReturn("16");
        Assert.assertEquals(metadata.platformVersion(), "16");
    }

    @Test
    public void testOrientatioWithPortrait(){
        Assert.assertEquals(metadata.orientation("PORTRAIT"), "PORTRAIT");
    }

    @Test
    public void testOrientatioWithLandscape(){
        Assert.assertEquals(metadata.orientation("LANDSCAPE"), "LANDSCAPE");
    }

    @Test
    public void testOrientatioWithWrongParam(){
        Assert.assertEquals(metadata.orientation("PARAM"), "PORTRAIT");
    }

    @Test
    public void testOrientatioWithWrongNullParam(){
        Assert.assertEquals(metadata.orientation(null), "PORTRAIT");
    }

    @Test
    public void testOrientatioWithWrongNullParamAndCaps(){
        when(driver.getCapabilities().getCapability("orientation")).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation(null), "LANDSCAPE");
    }

    @Test
    public void testOrientatioAuto(){
        when(driver.getOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        Assert.assertEquals(metadata.orientation("AUTO"), "LANDSCAPE");
    }
}
