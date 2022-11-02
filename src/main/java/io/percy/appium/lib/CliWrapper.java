package io.percy.appium.lib;

import java.util.List;

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

            if (statusCode != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
            }

            String version = response.getFirstHeader("x-percy-core-version").getValue();

            if (!version.split("\\.")[0].equals("1")) {
                AppPercy.log("Unsupported Percy CLI version, " + version);

                return false;
            }

            return true;
        } catch (Exception ex) {
            AppPercy.log("Percy is not running, disabling screenshots");
            if (AppPercy.PERCY_DEBUG) {
                AppPercy.log(ex.toString());
            }

            return false;
        }
    }

    /**
     * POST the Screenshot taken from the app to the Percy CLI node process.
     *
     * @param name       The human-readable name of the screenshot. Should be
     *                   unique.
     * @param fullScreen It indicates if the app is a full screen
     */
    public String postScreenshot(String name, JSONObject tag, List<Tile> tiles, String externalDebugUrl) {
        // Build a JSON object to POST back to the cli node process
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("tag", tag);
        data.put("tiles", Tile.getTilesAsJson(tiles));
        data.put("externalDebugUrl", externalDebugUrl);
        data.put("clientInfo", env.getClientInfo());
        data.put("environmentInfo", env.getEnvironmentInfo());

        StringEntity entity = new StringEntity(data.toString(), ContentType.APPLICATION_JSON);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(PERCY_SERVER_ADDRESS + "/percy/comparison");
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            JSONObject jsonResponse = new JSONObject(EntityUtils.toString(response.getEntity()));
            return jsonResponse.getString("link");
        } catch (Exception ex) {
            if (AppPercy.PERCY_DEBUG) {
                AppPercy.log(ex.toString());
            }
            AppPercy.log("Could not post screenshot " + name);
        }
        return null;
    }

}
