package io.percy.appium.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.percy.appium.Percy;
import io.percy.appium.lib.Region;
import io.percy.appium.lib.ScreenshotOptions;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Cucumber step definitions for Percy visual testing with Appium.
 *
 * <p>Provides Gherkin steps to capture Percy screenshots from Cucumber
 * feature files using Appium drivers (Android/iOS).</p>
 *
 * <p>Usage in feature files:</p>
 * <pre>
 * Feature: Mobile Visual Testing
 *   Scenario: Homepage screenshot
 *     Given I have a Percy instance
 *     When I take a Percy screenshot named "Homepage"
 *
 *   Scenario: Full page screenshot
 *     Given I have a Percy instance
 *     When I take a Percy screenshot named "Full Page" with full page
 *
 *   Scenario: Ignore region
 *     Given I have a Percy instance
 *     And I add ignore region XPath "//android.widget.Button[@text='AD']"
 *     When I take a Percy screenshot named "No Ads" with options
 * </pre>
 *
 * <p>Setup in step definition glue:</p>
 * <pre>
 * public class Hooks {
 *     {@literal @}Before
 *     public void setUp() {
 *         AppiumDriver driver = new AndroidDriver(url, caps);
 *         PercySteps.setDriver(driver);
 *     }
 *
 *     {@literal @}After
 *     public void tearDown() {
 *         PercySteps.reset();
 *     }
 * }
 * </pre>
 */
public class PercySteps {

    private static AppiumDriver driver;
    private static Percy percy;
    private static ScreenshotOptions screenshotOptions;

    private static final String CUCUMBER_CLIENT_NAME = "percy-cucumber-java-appium";

    /**
     * Set the AppiumDriver instance for Percy to use.
     * Call this from your Cucumber hooks before using any Percy steps.
     *
     * @param appiumDriver the Appium driver instance
     */
    public static void setDriver(AppiumDriver appiumDriver) {
        driver = appiumDriver;
        percy = new Percy(driver);
        screenshotOptions = new ScreenshotOptions();

        // Identify as Cucumber wrapper in Percy build info
        String sdkVersion = Percy.getSdkVersion();
        String cucumberVersion = getCucumberVersion();
        percy.setClientInfo(
            CUCUMBER_CLIENT_NAME + "/" + sdkVersion,
            "cucumber-java/" + cucumberVersion + "; appium-java"
        );
    }

    /**
     * Get the current Percy instance.
     */
    public static Percy getPercy() {
        return percy;
    }

    /**
     * Reset the Percy instance and clear stored options.
     * Call this from your Cucumber hooks in teardown.
     */
    public static void reset() {
        percy = null;
        driver = null;
        screenshotOptions = null;
    }

    private static String getCucumberVersion() {
        return resolveCucumberVersion(() -> {
            Package pkg = io.cucumber.java.en.Given.class.getPackage();
            return pkg != null ? pkg.getImplementationVersion() : null;
        });
    }

    /**
     * Resolve the Cucumber version from the given supplier, falling back to
     * {@code "unknown"} when the supplier yields {@code null} or throws.
     *
     * <p>Package-private to allow tests to exercise the null and failure
     * fallbacks without depending on the runtime manifest.</p>
     *
     * @param versionSupplier supplies the raw implementation version (may be null)
     * @return the resolved version, or {@code "unknown"} on null/failure
     */
    static String resolveCucumberVersion(java.util.function.Supplier<String> versionSupplier) {
        try {
            String version = versionSupplier.get();
            return version != null ? version : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    // ------------------------------------------------------------------
    // Given steps
    // ------------------------------------------------------------------

    @Given("I have a Percy instance")
    public void iHaveAPercyInstance() {
        if (driver == null) {
            throw new IllegalStateException(
                "AppiumDriver not set. Call PercySteps.setDriver(driver) in your @Before hook.");
        }
        if (percy == null) {
            percy = new Percy(driver);
        }
    }

    @Given("I set device name {string}")
    public void iSetDeviceName(String deviceName) {
        ensureOptions();
        screenshotOptions.setDeviceName(deviceName);
    }

    @Given("I set orientation {string}")
    public void iSetOrientation(String orientation) {
        ensureOptions();
        screenshotOptions.setOrientation(orientation);
    }

    @Given("I set status bar height {int}")
    public void iSetStatusBarHeight(int height) {
        ensureOptions();
        screenshotOptions.setStatusBarHeight(height);
    }

    @Given("I set nav bar height {int}")
    public void iSetNavBarHeight(int height) {
        ensureOptions();
        screenshotOptions.setNavBarHeight(height);
    }

    @Given("I set full page to {string}")
    public void iSetFullPage(String fullPage) {
        ensureOptions();
        screenshotOptions.setFullPage(Boolean.parseBoolean(fullPage));
    }

    @Given("I set screen lengths {int}")
    public void iSetScreenLengths(int lengths) {
        ensureOptions();
        screenshotOptions.setScreenLengths(lengths);
    }

    @Given("I set test case {string}")
    public void iSetTestCase(String testCase) {
        ensureOptions();
        screenshotOptions.setTestCase(testCase);
    }

    @Given("I set labels {string}")
    public void iSetLabels(String labels) {
        ensureOptions();
        screenshotOptions.setLabels(labels);
    }

    @Given("I set th test case execution ID {string}")
    public void iSetThTestCaseExecutionId(String id) {
        ensureOptions();
        screenshotOptions.setThTestCaseExecutionId(id);
    }

    @Given("I set sync to {string}")
    public void iSetSync(String sync) {
        ensureOptions();
        screenshotOptions.setSync(Boolean.parseBoolean(sync));
    }

    @Given("I set scrollable XPath {string}")
    public void iSetScrollableXpath(String xpath) {
        ensureOptions();
        screenshotOptions.setScrollableXpath(xpath);
    }

    @Given("I set scrollable ID {string}")
    public void iSetScrollableId(String id) {
        ensureOptions();
        screenshotOptions.setScrollableId(id);
    }

    @Given("I set top scrollview offset {int}")
    public void iSetTopScrollviewOffset(int offset) {
        ensureOptions();
        screenshotOptions.setTopScrollviewOffset(offset);
    }

    @Given("I set bottom scrollview offset {int}")
    public void iSetBottomScrollviewOffset(int offset) {
        ensureOptions();
        screenshotOptions.setBottomScrollviewOffset(offset);
    }

    @Given("I set scroll speed {int}")
    public void iSetScrollSpeed(int speed) {
        ensureOptions();
        screenshotOptions.setScrollSpeed(speed);
    }

    @Given("I set android scroll area percentage {int}")
    public void iSetAndroidScrollAreaPercentage(int percentage) {
        ensureOptions();
        screenshotOptions.setAndroidScrollAreaPercentage(percentage);
    }

    // ------------------------------------------------------------------
    // Ignore/Consider regions
    // ------------------------------------------------------------------

    @Given("I add ignore region XPath {string}")
    public void iAddIgnoreRegionXPath(String xpath) {
        ensureOptions();
        List<String> xpaths = new ArrayList<>(screenshotOptions.getIgnoreRegionXpaths());
        xpaths.add(xpath);
        screenshotOptions.setIgnoreRegionXpaths(xpaths);
    }

    @Given("I add ignore region accessibility ID {string}")
    public void iAddIgnoreRegionAccessibilityId(String accessibilityId) {
        ensureOptions();
        List<String> ids = new ArrayList<>(screenshotOptions.getIgnoreRegionAccessibilityIds());
        ids.add(accessibilityId);
        screenshotOptions.setIgnoreRegionAccessibilityIds(ids);
    }

    @Given("I add consider region XPath {string}")
    public void iAddConsiderRegionXPath(String xpath) {
        ensureOptions();
        List<String> xpaths = new ArrayList<>(screenshotOptions.getConsiderRegionXpaths());
        xpaths.add(xpath);
        screenshotOptions.setConsiderRegionXpaths(xpaths);
    }

    @Given("I add consider region accessibility ID {string}")
    public void iAddConsiderRegionAccessibilityId(String accessibilityId) {
        ensureOptions();
        List<String> ids = new ArrayList<>(screenshotOptions.getConsiderRegionAccessibilityIds());
        ids.add(accessibilityId);
        screenshotOptions.setConsiderRegionAccessibilityIds(ids);
    }

    @Given("I add custom ignore region {int}, {int}, {int}, {int}")
    public void iAddCustomIgnoreRegion(int top, int bottom, int left, int right) {
        ensureOptions();
        List<Region> regions = new ArrayList<>(screenshotOptions.getCustomIgnoreRegions());
        regions.add(new Region(top, bottom, left, right));
        screenshotOptions.setCustomIgnoreRegions(regions);
    }

    @Given("I add custom consider region {int}, {int}, {int}, {int}")
    public void iAddCustomConsiderRegion(int top, int bottom, int left, int right) {
        ensureOptions();
        List<Region> regions = new ArrayList<>(screenshotOptions.getCustomConsiderRegions());
        regions.add(new Region(top, bottom, left, right));
        screenshotOptions.setCustomConsiderRegions(regions);
    }

    @Given("I add ignore region Appium element {string}")
    public void iAddIgnoreRegionAppiumElement(String locator) {
        ensureOptions();
        WebElement element = driver.findElement(org.openqa.selenium.By.xpath(locator));
        List<Object> elements = new ArrayList<>(screenshotOptions.getIgnoreRegionAppiumElements());
        elements.add(element);
        screenshotOptions.setIgnoreRegionAppiumElements(elements);
    }

    @Given("I add consider region Appium element {string}")
    public void iAddConsiderRegionAppiumElement(String locator) {
        ensureOptions();
        WebElement element = driver.findElement(org.openqa.selenium.By.xpath(locator));
        List<Object> elements = new ArrayList<>(screenshotOptions.getConsiderRegionAppiumElements());
        elements.add(element);
        screenshotOptions.setConsiderRegionAppiumElements(elements);
    }

    @Given("I clear Percy options")
    public void iClearPercyOptions() {
        screenshotOptions = new ScreenshotOptions();
    }

    // ------------------------------------------------------------------
    // When steps - Screenshot
    // ------------------------------------------------------------------

    @When("I take a Percy screenshot named {string}")
    public void iTakeScreenshot(String name) {
        percy.screenshot(name);
    }

    @When("I take a Percy screenshot named {string} with full page")
    public void iTakeScreenshotFullPage(String name) {
        percy.screenshot(name, true);
    }

    @When("I take a Percy screenshot named {string} with full screen")
    public void iTakeScreenshotFullScreen(String name) {
        ensureOptions();
        screenshotOptions.setFullScreen(true);
        percy.screenshot(name, screenshotOptions);
        screenshotOptions = new ScreenshotOptions();
    }

    @When("I take a Percy screenshot named {string} with options")
    public void iTakeScreenshotWithOptions(String name) {
        ensureOptions();
        percy.screenshot(name, screenshotOptions);
        screenshotOptions = new ScreenshotOptions();
    }

    // ------------------------------------------------------------------
    // Then steps
    // ------------------------------------------------------------------

    @Then("Percy should be enabled")
    public void percyShouldBeEnabled() {
        if (percy == null) {
            throw new IllegalStateException("Percy instance not initialized.");
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void ensureOptions() {
        if (screenshotOptions == null) {
            screenshotOptions = new ScreenshotOptions();
        }
    }
}
