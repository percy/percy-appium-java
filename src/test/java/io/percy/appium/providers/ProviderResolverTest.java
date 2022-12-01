package io.percy.appium.providers;

import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import io.appium.java_client.android.AndroidDriver;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class ProviderResolverTest {

    @Mock
    AndroidDriver androidDriver;

    @Test
    public void testAppAutomate() {
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://browserstack.com/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(ProviderResolver.resolveProvider(androidDriver).getClass(), AppAutomate.class);
    }

    @Test
    public void testGenericProvider() {
        try {
            when(androidDriver.getRemoteAddress()).thenReturn(new URL("http://example.com/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(ProviderResolver.resolveProvider(androidDriver).getClass(), GenericProvider.class);
    }

}
