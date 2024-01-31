package io.percy.appium.providers;

import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import com.github.javafaker.Faker;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.Environment;
import io.percy.appium.lib.Cache;
import io.percy.appium.lib.ScreenshotOptions;
import io.percy.appium.lib.Tile;
import io.percy.appium.metadata.AndroidMetadata;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class AppAutomateTest {
    @Mock
    AndroidDriver androidDriver;

    @Mock
    Capabilities capabilities;

    AppAutomate appAutomate;

    @Mock
    AndroidMetadata metadata;

    HashMap<String, String> bstackCaps = new HashMap<String, String>();
    HashMap<String, Long> viewportRect = new HashMap<String, Long>();
    HashMap<String, HashMap<String, Long>> sessionValue = new HashMap<String, HashMap<String, Long>>();
    Response session = new Response(new SessionId("abc"));

    Faker faker = new Faker();
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);
    Integer deviceScreenHeight = (int) faker.number().randomNumber(3, false);
    Integer deviceScreenWidth = (int) faker.number().randomNumber(3, false);

    @Before
    public void setup() {
        Cache.CACHE_MAP.clear();
        appAutomate = new AppAutomate(androidDriver);
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("deviceScreenSize")).thenReturn("1080x2160");
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        sessionValue.put("viewportRect", viewportRect);
        session.setValue(sessionValue);
        when(androidDriver.execute("getSession")).thenReturn(session);
    }

    @Test
    public void testGetDebugUrl() {
        JSONObject result = new JSONObject("{\"buildHash\":\"abc\", \"sessionHash\":\"def\"}");
        Assert.assertEquals(appAutomate.getDebugUrl(result),
                "https://app-automate.browserstack.com/dashboard/v2/builds/abc/sessions/def");
    }

    @Test
    public void testGetDebugUrlWhenResultIsNull() {
        Assert.assertEquals(appAutomate.getDebugUrl(null), null);
    }

    @Test
    public void testSupports() {
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://browserstack.com/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(AppAutomate.supports(androidDriver), true);
    }

    @Test
    public void testDoesNotSupports() {
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://example.com/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(AppAutomate.supports(androidDriver), false);
    }

    @Test
    public void testExecutePercyScreenshotBeginWhenNullExceptionDoesNotThrow() {
        appAutomate.executePercyScreenshotBegin("");
    }

    @Test
    public void testExecutePercyScreenshotEndWhenNullExceptionDoesNotThrow() {
        appAutomate.executePercyScreenshotEnd("", "", "", false);
    }

    @Test
    public void testcaptureTilesForSinglePage() throws Exception {
        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(false);
        String response = "{\"result\":\"[{'header_height': 200, 'footer_height': 100, 'sha': 'sha'}]\"}";
        JSONObject arguments = new JSONObject();
        JSONObject args = new JSONObject();
        args.put("numOfTiles", 4);
        args.put("deviceHeight", 2160);
        args.put("topScrollviewOffset", 0);
        args.put("bottomScrollviewOffset", 0);
        args.put("FORCE_FULL_PAGE", false);

        arguments.put("state", "screenshot");
        arguments.put("percyBuildId", Environment.getPercyBuildID());
        arguments.put("screenshotType", "singlepage");
        arguments.put("projectId", "percy-prod");
        arguments.put("scaleFactor", 1);
        arguments.put("options", args);
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);

        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(response);

        AppAutomate appAutomateProvider = new AppAutomate(androidDriver);
        appAutomateProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));

        Tile tile = appAutomateProvider.captureTiles(options).get(0);
        Assert.assertEquals(tile.getStatusBarHeight().intValue(), top.intValue());
        Assert.assertEquals(tile.getNavBarHeight().intValue(), 2160 - (height + top));
        Assert.assertEquals(tile.getHeaderHeight().intValue(), 200);
        Assert.assertEquals(tile.getFooterHeight().intValue(), 100);
        Assert.assertEquals(tile.getFullScreen(), false);
        Assert.assertEquals(tile.getSha(), "sha");
    }

    @Test
    public void testcaptureTilesForFullPage() throws Exception {
        String response = "{\"result\":\"[{'header_height': 200, 'footer_height': 100, 'sha': 'sha'}]\"}";
        JSONObject arguments = new JSONObject();
        JSONObject args = new JSONObject();
        args.put("numOfTiles", 4);
        args.put("deviceHeight", 2160);
        args.put("topScrollviewOffset", 0);
        args.put("bottomScrollviewOffset", 0);
        args.put("FORCE_FULL_PAGE", false);

        arguments.put("state", "screenshot");
        arguments.put("percyBuildId", Environment.getPercyBuildID());
        arguments.put("screenshotType", "fullpage");
        arguments.put("projectId", "percy-prod");
        arguments.put("scaleFactor", 1);
        arguments.put("options", args);
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(true);

        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(response);

        AppAutomate appAutomateProvider = new AppAutomate(androidDriver);
        appAutomateProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));

        Tile tile = appAutomateProvider.captureTiles(options).get(0);
        Assert.assertEquals(tile.getStatusBarHeight().intValue(), top.intValue());
        Assert.assertEquals(tile.getNavBarHeight().intValue(), 2160 - (height + top));
        Assert.assertEquals(tile.getHeaderHeight().intValue(), 200);
        Assert.assertEquals(tile.getFooterHeight().intValue(), 100);
        Assert.assertEquals(tile.getFullScreen(), false);
        Assert.assertEquals(tile.getSha(), "sha");
    }

    @Test
    public void testcaptureTilesForFullPage_for_dev_project() throws Exception {
        try (MockedStatic<Environment> mockedStatic = Mockito.mockStatic(Environment.class)) {
            mockedStatic.when(Environment::getEnablePercyDev).thenReturn(true);
            String response = "{\"result\":\"[{'header_height': 200, 'footer_height': 100, 'sha': 'sha'}]\"}";
            JSONObject arguments = new JSONObject();
            JSONObject args = new JSONObject();
            args.put("numOfTiles", 4);
            args.put("deviceHeight", 2160);
            args.put("topScrollviewOffset", 0);
            args.put("bottomScrollviewOffset", 0);
            args.put("FORCE_FULL_PAGE", false);

            arguments.put("state", "screenshot");
            arguments.put("percyBuildId", Environment.getPercyBuildID());
            arguments.put("screenshotType", "fullpage");
            arguments.put("projectId", "percy-dev");
            arguments.put("scaleFactor", 1);
            arguments.put("options", args);
            JSONObject reqObject = new JSONObject();
            reqObject.put("action", "percyScreenshot");
            reqObject.put("arguments", arguments);
            ScreenshotOptions options = new ScreenshotOptions();
            options.setFullPage(true);

            when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                    .thenReturn(response);
            AppAutomate appAutomateProvider = new AppAutomate(androidDriver);
            appAutomateProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));

            Tile tile = appAutomateProvider.captureTiles(options).get(0);
            Assert.assertEquals(tile.getStatusBarHeight().intValue(), top.intValue());
            Assert.assertEquals(tile.getNavBarHeight().intValue(), 2160 - (height + top));
            Assert.assertEquals(tile.getHeaderHeight().intValue(), 200);
            Assert.assertEquals(tile.getFooterHeight().intValue(), 100);
            Assert.assertEquals(tile.getFullScreen(), false);
            Assert.assertEquals(tile.getSha(), "sha");
        
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testExecutePercyScreenshotBegin() {
        String response = "{\"success\":\"true\"}";
        String name = "First";
        JSONObject arguments = new JSONObject();
        arguments.put("state", "begin");
        arguments.put("percyBuildId", Environment.getPercyBuildID());
        arguments.put("percyBuildUrl", Environment.getPercyBuildUrl());
        arguments.put("name", name);
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(response);
        appAutomate.executePercyScreenshotBegin(name);
    }

    @Test
    public void testExecutePercyScreenshotEnd() {
        String response = "{\"success\":\"true\"}";
        String percyScreenshotUrl = "";
        String name = "First";
        JSONObject arguments = new JSONObject();
        arguments.put("state", "end");
        arguments.put("percyScreenshotUrl", percyScreenshotUrl);
        arguments.put("name", name);
        arguments.put("status", "success");
        arguments.put("sync", true);
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(response);
        appAutomate.executePercyScreenshotEnd(name, percyScreenshotUrl, null, true);
    }

    @Test
    public void testExecutePercyScreenshot() throws Exception {
        String response = "{\"result\":\"result\"}";
        JSONObject arguments = new JSONObject();
        ScreenshotOptions options = new ScreenshotOptions();
        options.setScreenLengths(4);
        options.setFullPage(true);
        JSONObject args = new JSONObject();
        args.put("numOfTiles", 4);
        args.put("deviceHeight", 2160);
        args.put("topScrollviewOffset", 0);
        args.put("bottomScrollviewOffset", 0);
        args.put("FORCE_FULL_PAGE", false);
        arguments.put("state", "screenshot");
        arguments.put("percyBuildId", Environment.getPercyBuildID());
        arguments.put("screenshotType", "fullpage");
        arguments.put("projectId", "percy-prod");
        arguments.put("scaleFactor", 1);
        arguments.put("options", args);
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(response);
        appAutomate.executePercyScreenshot(options, 1, 2160);
    }

    @Test
    public void testDeviceName() {
        JSONObject result = new JSONObject("{\"deviceName\":\"Samsung Galaxy S22\"}");
        Assert.assertEquals(appAutomate.deviceName(null, result), "Samsung Galaxy S22");
    }

    @Test
    public void testDeviceNameWithPassedDeviceName() {
        JSONObject result = new JSONObject("{\"deviceName\":\"Samsung Galaxy S22\"}");
        Assert.assertEquals(appAutomate.deviceName("Custom name", result), "Custom name");
    }

    @Test
    public void testDeviceNameWithNullResult() {
        Assert.assertEquals(appAutomate.deviceName(null, null), null);
    }

    @Test
    public void testOsName() {
        JSONObject result = new JSONObject("{\"osVersion\":\"13.6\"}");
        Assert.assertEquals(appAutomate.osVersion(result), "13");
    }

    @Test
    public void testOsNameWithNullResult() {
        Assert.assertEquals(appAutomate.osVersion(null), null);
    }

    @Test
    public void testDeviceNameWhenProvidedInParams() {
        Assert.assertEquals(appAutomate.deviceName("Samsung Galaxy S22 Ultra", null), "Samsung Galaxy S22 Ultra");
    }

    @Test
    public void verifyCorrectAppiumVersionFalseJWP() {
        when(capabilities.getCapability("browserstack.appium_version")).thenReturn("1.19.1");
        Assert.assertEquals(appAutomate.verifyCorrectAppiumVersion(), true);
    }

    @Test
    public void verifyCorrectAppiumVersionTrueJWP() {
        when(capabilities.getCapability("browserstack.appium_version")).thenReturn("1.17.0");
        Assert.assertEquals(appAutomate.verifyCorrectAppiumVersion(), false);
    }

    @Test
    public void verifyCorrectAppiumVersionFalseW3C() {
        bstackCaps.put("appiumVersion", "1.19.1");
        when(capabilities.getCapability("bstack:options")).thenReturn(bstackCaps);
        Assert.assertEquals(appAutomate.verifyCorrectAppiumVersion(), true);
    }

    @Test
    public void verifyCorrectAppiumVersionTrueW3C() {
        bstackCaps.put("appiumVersion", "1.17.0");
        when(capabilities.getCapability("bstack:options")).thenReturn(bstackCaps);
        Assert.assertEquals(appAutomate.verifyCorrectAppiumVersion(), false);
    }

}
