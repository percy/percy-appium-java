package io.percy.appium;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import io.appium.java_client.android.AndroidDriver;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class EnvironmentTest {
    Environment environment;

    @Mock
    AndroidDriver androidDriver;

    @Before
    public void setup() {
        environment = new Environment(androidDriver);
    }

    @Test
    public void testGetClientInfo() {
        Assert.assertEquals(environment.getClientInfo(), "percy-appium-app/" + Environment.SDK_VERSION);
    }

    @Test
    public void testGetEnvironmentInfo() {
        Assert.assertTrue(environment.getEnvironmentInfo().contains("appium-java; AndroidDriver"));
    }

    @Test
    public void testSetPercyBuildId() {
        Environment.setPercyBuildID("123");
        Assert.assertEquals(Environment.getPercyBuildID(), "123");
    }

    @Test
    public void testSetPercyBuildUrl() {
        String buildUrl = "https://percy.io/9560f98d/app-proj-temp/builds/27591981";
        Environment.setPercyBuildUrl(buildUrl);
        Assert.assertEquals(Environment.getPercyBuildUrl(), buildUrl);
    }

}
