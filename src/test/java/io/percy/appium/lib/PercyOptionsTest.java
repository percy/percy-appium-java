package io.percy.appium.lib;

import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.SessionId;

import io.appium.java_client.android.AndroidDriver;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class PercyOptionsTest {
    PercyOptions percyOptions;

    @Mock
    AndroidDriver androidDriver;

    @Mock
    Capabilities capabilities;

    @After
    public void clearCache() {
        Cache.CACHE_MAP.clear();
    }

    @Before
    public void setup() {
        when(androidDriver.getSessionId()).thenReturn(new SessionId("abc"));
    }

    @Test
    public void testPercyOptionEnabledWithNull() {
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertTrue(percyOptions.percyOptionEnabled());
    }

    @Test
    public void testPercyOptionEnabledWithEnabledJsonProtocol() {
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("percy.enabled")).thenReturn("true");
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertTrue(percyOptions.percyOptionEnabled());
    }

    @Test
    public void testPercyOptionEnabledWithDisabledJsonProtocol() {
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("percy.enabled")).thenReturn("false");
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertFalse(percyOptions.percyOptionEnabled());
    }

    @Test
    public void testPercyOptionEnabledWithEnabledW3CProtocol() {
        HashMap<String, Object> percyOptionsHash = new HashMap<String, Object>();
        percyOptionsHash.put("enabled", "true");
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("percyOptions")).thenReturn(percyOptionsHash);
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertTrue(percyOptions.percyOptionEnabled());
    }

    @Test
    public void testPercyOptionEnabledWithDisabledW3CProtocol() {
        HashMap<String, Object> percyOptionsHash = new HashMap<String, Object>();
        percyOptionsHash.put("enabled", "false");
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("percyOptions")).thenReturn(percyOptionsHash);
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertFalse(percyOptions.percyOptionEnabled());
    }

    @Test
    public void testSetPercyIgnoreErrorsWithNull() {
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertTrue(percyOptions.setPercyIgnoreErrors());
    }

    @Test
    public void testSetPercyIgnoreErrorsWithEnabledJsonProtocol() {
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("percy.ignoreErrors")).thenReturn("true");
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertTrue(percyOptions.setPercyIgnoreErrors());
    }

    @Test
    public void testSetPercyIgnoreErrorsWithDisabledJsonProtocol() {
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("percy.ignoreErrors")).thenReturn("false");
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertFalse(percyOptions.setPercyIgnoreErrors());
    }

    @Test
    public void testSetPercyIgnoreErrorsWithEnabledW3CProtocol() {
        HashMap<String, Object> percyOptionsHash = new HashMap<String, Object>();
        percyOptionsHash.put("ignoreErrors", "true");
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("percyOptions")).thenReturn(percyOptionsHash);
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertTrue(percyOptions.setPercyIgnoreErrors());
    }

    @Test
    public void testSetPercyIgnoreErrorsWithDisabledW3CProtocol() {
        HashMap<String, Object> percyOptionsHash = new HashMap<String, Object>();
        percyOptionsHash.put("ignoreErrors", "false");
        when(androidDriver.getCapabilities()).thenReturn(capabilities);
        when(capabilities.getCapability("percyOptions")).thenReturn(percyOptionsHash);
        percyOptions = new PercyOptions(androidDriver);
        Assert.assertFalse(percyOptions.setPercyIgnoreErrors());
    }
    
}
