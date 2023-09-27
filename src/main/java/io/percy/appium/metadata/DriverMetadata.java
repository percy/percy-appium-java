package io.percy.appium.metadata;

import io.appium.java_client.AppiumDriver;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import io.percy.appium.lib.Cache;

public class DriverMetadata {
    private String sessionId;
    private AppiumDriver driver;
    public DriverMetadata(AppiumDriver driver) {
        this.driver = driver;
        this.sessionId = driver.getSessionId().toString();
    }

    public  String getSessionId() {
        return this.sessionId;
    }

    public Map<String, Object> getCapabilities() {
        String key = "capabilities_" + this.sessionId;
        if (Cache.CACHE_MAP.get(key) == null) {
            Map<String, Object> capabilities = driver.getCapabilities().asMap();
            Cache.CACHE_MAP.put(key, capabilities);
        }
        return (Map<String, Object>) Cache.CACHE_MAP.get(key);
    }

    public String getCommandExecutorUrl() {
        String key = "commandExecutorUrl_" + this.sessionId;
        if (Cache.CACHE_MAP.get(key) == null) {
            String commandExecutorUrl = driver.getRemoteAddress().toString();
            Cache.CACHE_MAP.put(key, commandExecutorUrl);
        }
        return (String) Cache.CACHE_MAP.get(key);
    }

    protected void finalize() throws Throwable {
        Set<String> set = new HashSet<>();
        set.add("capabilities_" + this.sessionId);
        set.add("commandExecutorUrl_" + this.sessionId);
        Cache.CACHE_MAP.keySet().removeAll(set);
    }
}
