package io.percy.appium.lib;

import static org.junit.Assert.assertTrue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class IgnoreRegionTest {
    @Test
    public void testIsValid() {
        IgnoreRegion region = new IgnoreRegion(0, 0, 0, 0);
        region.setTop(10);
        region.setBottom(20);
        region.setLeft(5);
        region.setRight(15);

        Assert.assertTrue(region.isValid(30, 30));
        Assert.assertTrue(region.isValid(20, 20));
        Assert.assertTrue(region.isValid(30, 20));
        Assert.assertFalse(region.isValid(10, 30));

        region.setTop(15);
        region.setBottom(10);
        region.setLeft(5);
        region.setRight(15);
        Assert.assertFalse(region.isValid(30, 30));

        region.setTop(10);
        region.setBottom(20);
        region.setLeft(15);
        region.setRight(5);
        Assert.assertFalse(region.isValid(30, 30));

        region.setTop(10);
        region.setBottom(20);
        region.setLeft(5);
        region.setRight(35);
        Assert.assertFalse(region.isValid(30, 30));

        IgnoreRegion ir = new IgnoreRegion(10, 20, 30, 40);
        Assert.assertTrue(ir.isValid(30, 60));
        Assert.assertFalse(ir.isValid(30, 30));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTopWithNegativeValue() {
        IgnoreRegion region = new IgnoreRegion(0, 0, 0, 0);
        region.setTop(-10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBottomWithNegativeValue() {
        IgnoreRegion region = new IgnoreRegion(0, 0, 0, 0);
        region.setBottom(-10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLeftWithNegativeValue() {
        IgnoreRegion region = new IgnoreRegion(0, 0, 0, 0);
        region.setLeft(-10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRightWithNegativeValue() {
        IgnoreRegion region = new IgnoreRegion(0, 0, 0, 0);
        region.setRight(-10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor() {
        IgnoreRegion region = new IgnoreRegion(10, 20, -1, 40);
    }
}
