package io.percy.appium.lib;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.Environment;

public class CliWrapper {
    // Maybe get the CLI server address
    private static String PERCY_SERVER_ADDRESS = System.getenv().getOrDefault("PERCY_SERVER_ADDRESS",
            "http://localhost:5338");

    // Environment information like Java, driver, & SDK versions
    private Environment env;

    public CliWrapper(AppiumDriver driver) {
        this.env = new Environment(driver);
    }

    /**
     * Checks to make sure the local Percy server is running. If not, disable Percy.
     */
    public boolean healthcheck() {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            // Creating a HttpGet object
            HttpGet httpget = new HttpGet(PERCY_SERVER_ADDRESS + "/percy/healthcheck");

            // Executing the Get request
            HttpResponse response = httpClient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            JSONObject myObject = new JSONObject(responseString);
            JSONObject buildJsonObject = (JSONObject) myObject.get("build");
            Environment.setPercyBuildID((String) buildJsonObject.get("id"));
            Environment.setPercyBuildUrl((String) buildJsonObject.get("url"));
            Environment.setSessionType((String) myObject.optString("type", null));

            if (statusCode != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
            }

            String version = response.getFirstHeader("x-percy-core-version").getValue();
            Integer majorVersion = Integer.parseInt(version.split("\\.")[0]);
            Integer minorVersion = Integer.parseInt(version.split("\\.")[1]);

            if (majorVersion < 1) {
                AppPercy.log("Unsupported Percy CLI version, " + version);
                return false;
            } else {
                if (minorVersion < 27) {
                    AppPercy.log("Percy CLI version, " + version
                            + " is not minimum version required "
                            + "Percy on Automate is available from 1.27.0-beta.0.",
                            "warn");
                    return false;
                }
            }

            return true;
        } catch (Exception ex) {
            AppPercy.log("Percy is not running, disabling screenshots");
            AppPercy.log(ex.toString(), "debug");

            return false;
        }
    }

    /**
     * POST the Screenshot taken from the app to the Percy CLI node process.
     *
     * @param name The human-readable name of the screenshot. Should be
     *             unique.
     */
    public String postScreenshot(String name, JSONObject tag, List<Tile> tiles, String externalDebugUrl,
            JSONObject ignoredElementsData, JSONObject consideredElementsData) {
        // Build a JSON object to POST back to the cli node process
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("tag", tag);
        data.put("tiles", Tile.getTilesAsJson(tiles));
        data.put("externalDebugUrl", externalDebugUrl);
        data.put("ignoredElementsData", ignoredElementsData);
        data.put("consideredElementsData", consideredElementsData);
        data.put("clientInfo", env.getClientInfo(false));
        data.put("environmentInfo", env.getEnvironmentInfo());

        StringEntity entity = new StringEntity(data.toString(), ContentType.APPLICATION_JSON);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(PERCY_SERVER_ADDRESS + "/percy/comparison");
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            JSONObject jsonResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
            return jsonResponse.getString("link");
        } catch (Exception ex) {
            AppPercy.log(ex.toString(), "debug");
            AppPercy.log("Could not post screenshot " + name);
        }
        return null;
    }

    public void postFailedEvent(String err) {
        // Build a JSON object to POST back to the cli node process
        JSONObject data = new JSONObject();
        data.put("clientInfo", env.getClientInfo(true));
        data.put("errorMessage", err);

        StringEntity entity = new StringEntity(data.toString(), ContentType.APPLICATION_JSON);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(PERCY_SERVER_ADDRESS + "/percy/events");
            request.setEntity(entity);
            httpClient.execute(request);
        } catch (Exception ex) {
            AppPercy.log(ex.toString(), "debug");
        }
    }

    public String postScreenshotPOA(String name, String sessionId, String commandExecutorUrl,
            Map<String, Object> capabilities, Map<String, Object> options) {
        // Build a JSON object to POST back to the cli node process
        JSONObject data = new JSONObject();
        data.put("snapshotName", name);
        data.put("sessionId", sessionId);
        data.put("commandExecutorUrl", commandExecutorUrl);
        data.put("capabilities", capabilities);
        data.put("options", options);
        data.put("clientInfo", env.getClientInfo(false));
        data.put("environmentInfo", env.getEnvironmentInfo());

        StringEntity entity = new StringEntity(data.toString(), ContentType.APPLICATION_JSON);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(PERCY_SERVER_ADDRESS + "/percy/automateScreenshot");
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
        } catch (Exception ex) {
            AppPercy.log(ex.toString(), "debug");
            AppPercy.log("Could not post screenshot " + name);
        }
        return null;
    }

}
