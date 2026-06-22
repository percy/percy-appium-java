package io.percy.appium.lib;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.Environment;

@RunWith(org.mockito.junit.MockitoJUnitRunner.Silent.class)
public class CliWrapperTest {
    @Mock
    AndroidDriver androidDriver;

    CliWrapper cliWrapper;

    @Before
    public void setup() {
        cliWrapper = new CliWrapper(androidDriver);
    }

    // -- helpers -------------------------------------------------------------

    private CloseableHttpClient stubBuilderClient(MockedStatic<HttpClientBuilder> builders) {
        HttpClientBuilder builder = mock(HttpClientBuilder.class);
        CloseableHttpClient client = mock(CloseableHttpClient.class);
        builders.when(HttpClientBuilder::create).thenReturn(builder);
        when(builder.build()).thenReturn(client);
        return client;
    }

    private CloseableHttpClient stubCustomClient(MockedStatic<HttpClients> clients) {
        HttpClientBuilder builder = mock(HttpClientBuilder.class);
        CloseableHttpClient client = mock(CloseableHttpClient.class);
        clients.when(HttpClients::custom).thenReturn(builder);
        when(builder.setDefaultRequestConfig(any())).thenReturn(builder);
        when(builder.build()).thenReturn(client);
        return client;
    }

    private CloseableHttpResponse responseWithStatus(int status) {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(status);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(mock(HttpEntity.class));
        return response;
    }

    private void withVersionHeader(CloseableHttpResponse response, String version) {
        Header header = mock(Header.class);
        when(header.getValue()).thenReturn(version);
        when(response.getFirstHeader("x-percy-core-version")).thenReturn(header);
    }

    // -- getEnvironment ------------------------------------------------------

    @Test
    public void getEnvironmentReturnsEnvironment() {
        Assert.assertNotNull(cliWrapper.getEnvironment());
    }

    // -- healthcheck ---------------------------------------------------------

    @Test
    public void healthcheckReturnsTrueForSupportedVersion() throws Exception {
        try (MockedStatic<HttpClientBuilder> builders = mockStatic(HttpClientBuilder.class);
                MockedStatic<EntityUtils> entityUtils = mockStatic(EntityUtils.class)) {
            CloseableHttpClient client = stubBuilderClient(builders);
            CloseableHttpResponse response = responseWithStatus(200);
            withVersionHeader(response, "1.28.0");
            when(client.execute(any())).thenReturn(response);
            entityUtils.when(() -> EntityUtils.toString(any(), eq("UTF-8")))
                    .thenReturn("{\"build\":{\"id\":\"bid\",\"url\":\"burl\"},\"type\":\"web\"}");

            Assert.assertTrue(cliWrapper.healthcheck());
            Assert.assertEquals("bid", Environment.getPercyBuildID());
            Assert.assertEquals("burl", Environment.getPercyBuildUrl());
            Assert.assertEquals("web", Environment.getSessionType());
        }
    }

    @Test
    public void healthcheckReturnsFalseForUnsupportedMajorVersion() throws Exception {
        try (MockedStatic<HttpClientBuilder> builders = mockStatic(HttpClientBuilder.class);
                MockedStatic<EntityUtils> entityUtils = mockStatic(EntityUtils.class)) {
            CloseableHttpClient client = stubBuilderClient(builders);
            CloseableHttpResponse response = responseWithStatus(200);
            withVersionHeader(response, "0.9.0");
            when(client.execute(any())).thenReturn(response);
            entityUtils.when(() -> EntityUtils.toString(any(), eq("UTF-8")))
                    .thenReturn("{\"build\":{\"id\":\"bid\",\"url\":\"burl\"}}");

            Assert.assertFalse(cliWrapper.healthcheck());
        }
    }

    @Test
    public void healthcheckReturnsFalseForOldMinorVersion() throws Exception {
        try (MockedStatic<HttpClientBuilder> builders = mockStatic(HttpClientBuilder.class);
                MockedStatic<EntityUtils> entityUtils = mockStatic(EntityUtils.class)) {
            CloseableHttpClient client = stubBuilderClient(builders);
            CloseableHttpResponse response = responseWithStatus(200);
            withVersionHeader(response, "1.26.0");
            when(client.execute(any())).thenReturn(response);
            entityUtils.when(() -> EntityUtils.toString(any(), eq("UTF-8")))
                    .thenReturn("{\"build\":{\"id\":\"bid\",\"url\":\"burl\"}}");

            Assert.assertFalse(cliWrapper.healthcheck());
        }
    }

    @Test
    public void healthcheckReturnsFalseForNon200Status() throws Exception {
        try (MockedStatic<HttpClientBuilder> builders = mockStatic(HttpClientBuilder.class);
                MockedStatic<EntityUtils> entityUtils = mockStatic(EntityUtils.class)) {
            CloseableHttpClient client = stubBuilderClient(builders);
            CloseableHttpResponse response = responseWithStatus(500);
            when(client.execute(any())).thenReturn(response);
            entityUtils.when(() -> EntityUtils.toString(any(), eq("UTF-8")))
                    .thenReturn("{\"build\":{\"id\":\"bid\",\"url\":\"burl\"}}");

            // statusCode != 200 -> throws internally -> caught -> false
            Assert.assertFalse(cliWrapper.healthcheck());
        }
    }

    @Test
    public void healthcheckReturnsFalseOnException() throws Exception {
        try (MockedStatic<HttpClientBuilder> builders = mockStatic(HttpClientBuilder.class)) {
            CloseableHttpClient client = stubBuilderClient(builders);
            when(client.execute(any())).thenThrow(new RuntimeException("connection refused"));

            Assert.assertFalse(cliWrapper.healthcheck());
        }
    }

    // -- postScreenshot ------------------------------------------------------

    @Test
    public void postScreenshotReturnsResponse() throws Exception {
        try (MockedStatic<HttpClients> clients = mockStatic(HttpClients.class);
                MockedStatic<EntityUtils> entityUtils = mockStatic(EntityUtils.class)) {
            CloseableHttpClient client = stubCustomClient(clients);
            CloseableHttpResponse response = responseWithStatus(200);
            when(client.execute(any(HttpPost.class))).thenReturn(response);
            entityUtils.when(() -> EntityUtils.toString(any(HttpEntity.class)))
                    .thenReturn("{\"success\":true}");

            List<Tile> tiles = new ArrayList<>();
            tiles.add(new Tile(null, 1, 1, 0, 0, false, "sha"));
            JSONObject result = cliWrapper.postScreenshot("name", new JSONObject(), tiles, "debug",
                    new JSONObject(), new JSONObject(), false, "tc", "labels", "exec-1");

            Assert.assertNotNull(result);
            Assert.assertTrue(result.getBoolean("success"));
        }
    }

    @Test
    public void postScreenshotReturnsNullOnException() throws Exception {
        try (MockedStatic<HttpClients> clients = mockStatic(HttpClients.class)) {
            CloseableHttpClient client = stubCustomClient(clients);
            when(client.execute(any(HttpPost.class))).thenThrow(new RuntimeException("boom"));

            JSONObject result = cliWrapper.postScreenshot("name", new JSONObject(), new ArrayList<>(), "debug",
                    new JSONObject(), new JSONObject(), false, "tc", "labels", "exec-1");

            Assert.assertNull(result);
        }
    }

    // -- postScreenshotPOA ---------------------------------------------------

    @Test
    public void postScreenshotPOAReturnsResponse() throws Exception {
        try (MockedStatic<HttpClients> clients = mockStatic(HttpClients.class);
                MockedStatic<EntityUtils> entityUtils = mockStatic(EntityUtils.class)) {
            CloseableHttpClient client = stubCustomClient(clients);
            CloseableHttpResponse response = responseWithStatus(200);
            when(client.execute(any(HttpPost.class))).thenReturn(response);
            entityUtils.when(() -> EntityUtils.toString(any(HttpEntity.class)))
                    .thenReturn("{\"success\":true}");

            Map<String, Object> caps = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            JSONObject result = cliWrapper.postScreenshotPOA("name", "session", "url", caps, options);

            Assert.assertNotNull(result);
            Assert.assertTrue(result.getBoolean("success"));
        }
    }

    @Test
    public void postScreenshotPOAReturnsNullOnException() throws Exception {
        try (MockedStatic<HttpClients> clients = mockStatic(HttpClients.class)) {
            CloseableHttpClient client = stubCustomClient(clients);
            when(client.execute(any(HttpPost.class))).thenThrow(new RuntimeException("boom"));

            JSONObject result = cliWrapper.postScreenshotPOA("name", "session", "url",
                    new HashMap<>(), new HashMap<>());

            Assert.assertNull(result);
        }
    }

    // -- postFailedEvent -----------------------------------------------------

    @Test
    public void postFailedEventPostsWithoutThrowing() throws Exception {
        try (MockedStatic<HttpClientBuilder> builders = mockStatic(HttpClientBuilder.class)) {
            CloseableHttpClient client = stubBuilderClient(builders);
            CloseableHttpResponse response = responseWithStatus(200);
            when(client.execute(any())).thenReturn(response);

            cliWrapper.postFailedEvent("some error");
        }
    }

    @Test
    public void postFailedEventSwallowsException() throws Exception {
        try (MockedStatic<HttpClientBuilder> builders = mockStatic(HttpClientBuilder.class)) {
            CloseableHttpClient client = stubBuilderClient(builders);
            when(client.execute(any())).thenThrow(new RuntimeException("boom"));

            cliWrapper.postFailedEvent("some error");
        }
    }
}
