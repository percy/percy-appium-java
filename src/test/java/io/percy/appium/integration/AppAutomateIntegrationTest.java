package io.percy.appium.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.percy.appium.AppPercy;

/**
 * REAL end-to-end Percy-on-Automate integration test.
 *
 * <p>This test launches a live session on a BrowserStack App Automate device and drives the SDK's
 * {@link io.percy.appium.AppPercy} screenshot flow against it. It is tagged {@code "integration"} so
 * the normal unit run ({@code make test} / {@code mvn test}) excludes it, and it self-skips
 * (green, never failing) whenever the required credentials are absent.
 *
 * <p>Run it explicitly under the Percy CLI so a real Percy build is created:
 * <pre>
 *   export BROWSERSTACK_USERNAME=... BROWSERSTACK_ACCESS_KEY=... PERCY_TOKEN=...
 *   npx percy app:exec -- mvn test -Dgroups=integration -Dgpg.skip=true
 * </pre>
 *
 * <p>Percy-on-Automate requires {@code @percy/cli} &gt;= 1.27.0.
 */
@Tag("integration")
public class AppAutomateIntegrationTest {

    private static final String HUB_URL = "https://hub-cloud.browserstack.com/wd/hub";
    private static final String UPLOAD_URL = "https://api-cloud.browserstack.com/app-automate/upload";
    private static final String SAMPLE_APK =
            "https://www.browserstack.com/app-automate/sample-apps/android/WikipediaSample.apk";

    private static String userName;
    private static String accessKey;

    private AndroidDriver driver;

    private static String env(String key) {
        return System.getenv(key);
    }

    private static boolean credentialsPresent() {
        return isSet(env("BROWSERSTACK_USERNAME"))
                && isSet(env("BROWSERSTACK_ACCESS_KEY"))
                && isSet(env("PERCY_TOKEN"));
    }

    private static boolean isSet(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @BeforeAll
    static void verifyCredentialsPresent() {
        // Without creds the whole class is skipped -> the CI job stays GREEN before secrets are set,
        // and runs REAL App Automate once BROWSERSTACK_USERNAME / BROWSERSTACK_ACCESS_KEY / PERCY_TOKEN exist.
        Assumptions.assumeTrue(credentialsPresent(),
                "Skipping live App Automate test: set BROWSERSTACK_USERNAME, BROWSERSTACK_ACCESS_KEY and PERCY_TOKEN");
        userName = env("BROWSERSTACK_USERNAME");
        accessKey = env("BROWSERSTACK_ACCESS_KEY");
    }

    @BeforeEach
    void setUp() throws Exception {
        Assumptions.assumeTrue(credentialsPresent(),
                "Skipping live App Automate test: BrowserStack/Percy credentials are not configured");

        String appUrl = uploadSampleApp();

        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName", userName);
        bstackOptions.put("accessKey", accessKey);
        bstackOptions.put("deviceName", "Samsung Galaxy S22");
        bstackOptions.put("platformVersion", "12.0");
        bstackOptions.put("projectName", "Percy Appium Java");
        bstackOptions.put("buildName", "percy-appium-java app-automate integration");
        bstackOptions.put("sessionName", "Percy on Automate - Wikipedia sample");
        // Enables Percy-on-Automate session tagging on the BrowserStack side.
        bstackOptions.put("percy", true);
        bstackOptions.put("percyOptions", percyOptions());

        UiAutomator2Options options = new UiAutomator2Options();
        options.setApp(appUrl);
        options.setDeviceName("Samsung Galaxy S22");
        options.setPlatformVersion("12.0");
        options.setCapability("bstack:options", bstackOptions);

        driver = new AndroidDriver(new URL(HUB_URL), options);
    }

    private static Map<String, Object> percyOptions() {
        Map<String, Object> percyOptions = new HashMap<>();
        percyOptions.put("percyAutoEnabled", true);
        return percyOptions;
    }

    /**
     * Uploads the public BrowserStack Wikipedia sample APK to App Automate at runtime and returns
     * the resulting {@code bs://...} app id used by the session capabilities.
     */
    private static String uploadSampleApp() throws Exception {
        String credentials = userName + ":" + accessKey;
        String basicAuth = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(UPLOAD_URL);
            post.setHeader("Authorization", "Basic " + basicAuth);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(new JSONObject().put("url", SAMPLE_APK).toString(),
                    StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                int status = response.getStatusLine().getStatusCode();
                if (status < 200 || status >= 300) {
                    throw new IllegalStateException(
                            "App Automate upload failed (HTTP " + status + "): " + body);
                }
                JSONObject json = new JSONObject(body);
                String appUrl = json.optString("app_url", null);
                assertNotNull(appUrl, "Expected app_url in App Automate upload response: " + body);
                assertTrue(appUrl.startsWith("bs://"), "Unexpected app_url: " + appUrl);
                return appUrl;
            }
        }
    }

    @Test
    void takesPercyScreenshotsOnLiveDevice() {
        assertNotNull(driver, "AndroidDriver should be created against the BrowserStack hub");
        assertNotNull(driver.getSessionId(), "A live App Automate session should be established");

        AppPercy percy = new AppPercy(driver);

        // First snapshot of the launched app.
        percy.screenshot("Percy Appium Java - App Automate Integration");

        // Second snapshot after a trivial interaction (orientation toggle is widely supported and
        // does not depend on app-specific element ids), to exercise a post-interaction capture.
        driver.rotate(org.openqa.selenium.ScreenOrientation.LANDSCAPE);
        percy.screenshot("Percy Appium Java - App Automate Integration - Landscape");

        // Session must still be valid after taking screenshots.
        assertNotNull(driver.getSessionId(), "Session should remain valid after taking screenshots");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
