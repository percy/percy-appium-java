package io.percy.appium.lib;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.appium.java_client.android.AndroidDriver;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class CliWrapperTest {
    WireMockServer server = new WireMockServer(5338);
    @Mock
    AndroidDriver androidDriver;
    @Before
    public void setup() throws JSONException {
        CliWrapper.PERCY_SERVER_ADDRESS = "http://127.0.0.1:5338";
        JSONObject responseJsonObject = new JSONObject();
        JSONObject buildObject = new JSONObject();
        buildObject.put("id", "27591981");
        buildObject.put("url", "https://percy.io/9560f98d/app-proj-temp/builds/27591981");
        responseJsonObject.put("build", buildObject);
        ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder();
        mockResponse.withHeader("x-percy-core-version", "1.2");
        mockResponse.withStatus(200);
        mockResponse.withBody(String.valueOf(responseJsonObject));
        server.start();
        WireMock.configureFor("127.0.0.1", 5338);
        WireMock.stubFor(
                WireMock.get("/percy/healthcheck")
                        .willReturn(mockResponse)
        );
    }

    @Test
    public void testHealthcheck() {
        CliWrapper cliWrapper = new CliWrapper(androidDriver);
        cliWrapper.healthcheck();
        Assert.assertEquals(CliWrapper.PERCY_BUILD_ID, "27591981");
        Assert.assertEquals(CliWrapper.PERCY_BUILD_URL, "https://percy.io/9560f98d/app-proj-temp/builds/27591981");
    }
}
