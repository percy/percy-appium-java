package io.percy.appium.lib;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import io.appium.java_client.android.AndroidDriver;
import io.percy.appium.AppPercy;
import io.percy.appium.Environment;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class CliWrapperTest {
    @Mock
    AndroidDriver androidDriver;

    @Test
    public void testHealthcheck() {
        try {
            Integer version = Runtime.version().major();
            if (version > 10) {
                com.github.tomakehurst.wiremock.WireMockServer server = new com.github.tomakehurst.wiremock.WireMockServer(5338);
                JSONObject responseJsonObject = new JSONObject();
                JSONObject buildObject = new JSONObject();
                buildObject.put("id", "27591981");
                buildObject.put("url", "https://percy.io/9560f98d/app-proj-temp/builds/27591981");
                responseJsonObject.put("build", buildObject);
                com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder mockResponse = new com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder();
                mockResponse.withHeader("x-percy-core-version", "1.2");
                mockResponse.withStatus(200);
                mockResponse.withBody(String.valueOf(responseJsonObject));
                server.start();
                com.github.tomakehurst.wiremock.client.WireMock.configureFor("127.0.0.1", 5338);
                com.github.tomakehurst.wiremock.client.WireMock.stubFor(
                    com.github.tomakehurst.wiremock.client.WireMock.get("/percy/healthcheck")
                                .willReturn(mockResponse)
                );
        
                CliWrapper cliWrapper = new CliWrapper(androidDriver);
                cliWrapper.healthcheck();
                Assert.assertEquals(Environment.getPercyBuildID(), "27591981");
                Assert.assertEquals(Environment.getPercyBuildUrl(), "https://percy.io/9560f98d/app-proj-temp/builds/27591981");
            } else {
                AppPercy.log("Skipping test as runtime version is below 11");
            }
        } catch (Exception e) {
            AppPercy.log("Skipping test as runtime version couldn't be fetched.");
        }
    }
}
