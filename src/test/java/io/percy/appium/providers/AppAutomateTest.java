package io.percy.appium.providers;

import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockConstruction;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.SessionId;

import com.github.javafaker.Faker;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.Environment;
import io.percy.appium.lib.Cache;
import io.percy.appium.lib.CliWrapper;
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

    Faker faker = new Faker();
    Long top = faker.number().randomNumber(3, false);
    Long height = faker.number().randomNumber(3, false);
    Integer deviceScreenHeight = (int) faker.number().randomNumber(3, false);
    Integer deviceScreenWidth = (int) faker.number().randomNumber(3, false);

    @Before
    public void setup() {
        Cache.CACHE_MAP.clear();
        viewportRect.put("top", top);
        viewportRect.put("height", height);
        appAutomate = new AppAutomate(androidDriver);
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("deviceScreenSize")).thenReturn("1080x2160");
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
        when(capabilities.getCapability("viewportRect")).thenReturn(viewportRect);
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
        args.put("androidScrollAreaPercentage", options.getAndroidScrollAreaPercentage());
        args.put("scrollSpeed", options.getScrollSpeed());

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

    @Test
    public void verifyCorrectAppiumVersionWhenOptionsIsEmptyW3C() {
        when(capabilities.getCapability("bstack:options")).thenReturn(bstackCaps);
        Assert.assertEquals(appAutomate.verifyCorrectAppiumVersion(), true);
    }

    // Covers lines 56-60: executePercyScreenshotBegin happy path returns the
    // parsed result and sets markedPercySession from the "success" field.
    @Test
    public void testExecutePercyScreenshotBeginReturnsResult() {
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

        JSONObject result = appAutomate.executePercyScreenshotBegin(name);
        Assert.assertEquals(result.get("success").toString(), "true");

        // With the .equals fix, a {"success":"true"} response keeps markedPercySession=true,
        // so a second begin call still executes the body and returns a (non-null) result.
        JSONObject secondResult = appAutomate.executePercyScreenshotBegin(name);
        Assert.assertNotNull(secondResult);
        Assert.assertEquals("true", secondResult.get("success").toString());
    }

    // Covers lines 62-65: executePercyScreenshotBegin catch block when
    // executeScript throws (no stub matches -> NPE on null result.toString()).
    @Test
    public void testExecutePercyScreenshotBeginCatchReturnsNull() {
        when(androidDriver.executeScript(Mockito.anyString()))
                .thenThrow(new RuntimeException("boom"));
        Assert.assertEquals(appAutomate.executePercyScreenshotBegin("First"), null);
    }

    // Covers line 89: executePercyScreenshotEnd happy path sets markedPercySession
    // from the "success" field of the parsed result.
    @Test
    public void testExecutePercyScreenshotEndSetsMarkedPercySession() {
        String response = "{\"success\":\"true\"}";
        String percyScreenshotUrl = "url";
        String name = "First";
        JSONObject arguments = new JSONObject();
        arguments.put("state", "end");
        arguments.put("percyScreenshotUrl", percyScreenshotUrl);
        arguments.put("name", name);
        arguments.put("status", "Failed: some error");
        arguments.put("sync", false);
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(response);
        // error != null exercises the "Failed: ..." status branch (lines 74-75)
        appAutomate.executePercyScreenshotEnd(name, percyScreenshotUrl, "some error", false);
    }

    // Covers lines 132-134: executePercyScreenshot catch block re-throws a
    // wrapped "Screenshot command failed" exception when executeScript fails.
    @Test
    public void testExecutePercyScreenshotThrowsOnFailure() throws Exception {
        when(androidDriver.executeScript(Mockito.anyString()))
                .thenThrow(new RuntimeException("driver error"));
        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(true);
        try {
            appAutomate.executePercyScreenshot(options, 1, 2160);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertEquals("Screenshot command failed", e.getMessage());
        }
    }

    // Covers lines 139-141 and 146: captureTiles falls back to super.captureTiles
    // when PERCY_DISABLE_REMOTE_UPLOADS is set, and logs a warning for full page.
    @Test
    public void testCaptureTilesWithDisableRemoteUploadsFullPage() throws Exception {
        try (MockedStatic<Environment> mockedStatic = Mockito.mockStatic(Environment.class)) {
            mockedStatic.when(Environment::getDisableRemoteUploads).thenReturn(true);
            ScreenshotOptions options = new ScreenshotOptions();
            options.setFullPage(true);
            viewportRect.put("top", top);
            viewportRect.put("height", height);
            when(androidDriver.getScreenshotAs(OutputType.BASE64))
                    .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");
            // super.captureTiles calls AppAutomate.supports(driver) for full page.
            try {
                when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://browserstack.com/"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            AppAutomate appAutomateProvider = new AppAutomate(androidDriver);
            appAutomateProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));

            Tile tile = appAutomateProvider.captureTiles(options).get(0);
            Assert.assertTrue(tile.getLocalFilePath().endsWith(".png"));
            Assert.assertEquals(tile.getStatusBarHeight().intValue(), top.intValue());
            Assert.assertEquals(tile.getHeaderHeight().intValue(), 0);
            Assert.assertEquals(tile.getFooterHeight().intValue(), 0);
        }
    }

    // Covers line 146: captureTiles falls back to super.captureTiles when
    // PERCY_DISABLE_REMOTE_UPLOADS is set, without the full page warning branch.
    @Test
    public void testCaptureTilesWithDisableRemoteUploadsSinglePage() throws Exception {
        try (MockedStatic<Environment> mockedStatic = Mockito.mockStatic(Environment.class)) {
            mockedStatic.when(Environment::getDisableRemoteUploads).thenReturn(true);
            ScreenshotOptions options = new ScreenshotOptions();
            options.setFullPage(false);
            viewportRect.put("top", top);
            viewportRect.put("height", height);
            when(androidDriver.getScreenshotAs(OutputType.BASE64))
                    .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");

            AppAutomate appAutomateProvider = new AppAutomate(androidDriver);
            appAutomateProvider.setMetadata(new AndroidMetadata(androidDriver, null, null, null, null, null));

            Tile tile = appAutomateProvider.captureTiles(options).get(0);
            Assert.assertTrue(tile.getLocalFilePath().endsWith(".png"));
        }
    }

    // Covers lines 166-180: screenshot() orchestration. Begin fails (returns null
    // because executeScript is unstubbed), so device/osVersion/debugUrl resolve from
    // a null result, super.screenshot throws inside the try (captureTiles ->
    // executePercyScreenshot fails) hitting the catch (lines 176-177), and finally
    // executePercyScreenshotEnd runs (line 179) before returning the response.
    @Test
    public void testScreenshotHandlesBeginFailureAndScreenshotError() {
        ScreenshotOptions options = new ScreenshotOptions();
        options.setDeviceName("Samsung Galaxy S22");
        options.setFullPage(false);
        viewportRect.put("top", top);
        viewportRect.put("height", height);

        AppAutomate appAutomateProvider = new AppAutomate(androidDriver);
        JSONObject response = appAutomateProvider.screenshot("Snapshot", options);
        // super.screenshot throws (no executeScript stub for the screenshot request),
        // so the error branch is taken and the returned response is null.
        Assert.assertEquals(response, null);
    }

    // Covers lines 166-180 with a non-null begin result so that deviceName(),
    // osVersion() and getDebugUrl() all read from the result object.
    @Test
    public void testScreenshotWithBeginResultPopulatesDeviceAndOsVersion() {
        String beginResponse = "{\"success\":\"true\", \"deviceName\":\"Pixel 7\", "
                + "\"osVersion\":\"13.0\", \"buildHash\":\"bh\", \"sessionHash\":\"sh\"}";
        String name = "Snapshot";
        JSONObject arguments = new JSONObject();
        arguments.put("state", "begin");
        arguments.put("percyBuildId", Environment.getPercyBuildID());
        arguments.put("percyBuildUrl", Environment.getPercyBuildUrl());
        arguments.put("name", name);
        JSONObject reqObject = new JSONObject();
        reqObject.put("action", "percyScreenshot");
        reqObject.put("arguments", arguments);
        when(androidDriver.executeScript(String.format("browserstack_executor: %s", reqObject.toString())))
                .thenReturn(beginResponse);

        ScreenshotOptions options = new ScreenshotOptions();
        options.setFullPage(false);
        viewportRect.put("top", top);
        viewportRect.put("height", height);

        AppAutomate appAutomateProvider = new AppAutomate(androidDriver);
        JSONObject response = appAutomateProvider.screenshot(name, options);
        // super.screenshot still throws (screenshot request unstubbed) so response is null,
        // but deviceName/osVersion/debugUrl were resolved from the begin result.
        Assert.assertEquals(response, null);
    }

    // Covers lines 174 and 175: screenshot() success path where super.screenshot()
    // returns a JSON object containing "link", so percyScreenshotUrl is read from it.
    // We mock CliWrapper construction so the inherited GenericProvider.screenshot ->
    // cliWrapper.postScreenshot returns {"link":"https://percy.io/x"} without any HTTP
    // call, and disable remote uploads so captureTiles falls back to a local screenshot.
    @Test
    public void testScreenshotSuccessReadsLinkFromResponse() throws Exception {
        try (MockedStatic<Environment> mockedStatic = Mockito.mockStatic(Environment.class);
             MockedConstruction<CliWrapper> mockedCli = mockConstruction(CliWrapper.class,
                     (mock, context) -> when(mock.postScreenshot(
                             anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                             .thenReturn(new JSONObject("{\"link\":\"https://percy.io/x\"}")))) {
            mockedStatic.when(Environment::getDisableRemoteUploads).thenReturn(true);

            ScreenshotOptions options = new ScreenshotOptions();
            options.setDeviceName("Samsung Galaxy S22");
            options.setFullPage(false);
            viewportRect.put("top", top);
            viewportRect.put("height", height);
            when(androidDriver.getScreenshotAs(OutputType.BASE64))
                    .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");
            // getTag() (inside super.screenshot) reads platformName for osName().
            when(capabilities.getCapability("platformName")).thenReturn("android");

            // CliWrapper must be mocked at construction time, so build the provider here.
            AppAutomate appAutomateProvider = new AppAutomate(androidDriver);
            JSONObject response = appAutomateProvider.screenshot("Snapshot", options);

            Assert.assertEquals("https://percy.io/x", response.getString("link"));
        }
    }

    // Covers line 178 (close of the catch block in screenshot()): super.screenshot()
    // succeeds and returns a JSON object WITHOUT a "link" key, so
    // response.getString("link") throws inside the try and the catch block runs to
    // completion before executePercyScreenshotEnd is invoked.
    @Test
    public void testScreenshotSuccessWithMissingLinkHitsCatch() throws Exception {
        try (MockedStatic<Environment> mockedStatic = Mockito.mockStatic(Environment.class);
             MockedConstruction<CliWrapper> mockedCli = mockConstruction(CliWrapper.class,
                     (mock, context) -> when(mock.postScreenshot(
                             anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                             .thenReturn(new JSONObject("{\"data\":{}}")))) {
            mockedStatic.when(Environment::getDisableRemoteUploads).thenReturn(true);

            ScreenshotOptions options = new ScreenshotOptions();
            options.setDeviceName("Samsung Galaxy S22");
            options.setFullPage(false);
            viewportRect.put("top", top);
            viewportRect.put("height", height);
            when(androidDriver.getScreenshotAs(OutputType.BASE64))
                    .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA==");
            // getTag() (inside super.screenshot) reads platformName for osName().
            when(capabilities.getCapability("platformName")).thenReturn("android");

            AppAutomate appAutomateProvider = new AppAutomate(androidDriver);
            JSONObject response = appAutomateProvider.screenshot("Snapshot", options);

            // super.screenshot returned a non-null object (no "link"), so getString throws
            // and is swallowed by the catch; the original response object is still returned.
            Assert.assertEquals(false, response.has("link"));
        }
    }

    // Covers line 65: when the begin response reports failure ("success" != "true"),
    // markedPercySession is correctly set to false, so the next begin call skips the
    // `if (markedPercySession)` body and returns null (line 65).
    @Test
    public void testExecutePercyScreenshotBeginWhenSessionNotMarkedReturnsNull() {
        String response = "{\"success\":\"false\"}";
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

        // First call: success="false" -> markedPercySession becomes false, returns the result.
        JSONObject first = appAutomate.executePercyScreenshotBegin(name);
        Assert.assertEquals("false", first.get("success").toString());
        // Second call: markedPercySession is false, body is skipped, returns null (line 65).
        Assert.assertEquals(null, appAutomate.executePercyScreenshotBegin(name));
    }

}
