package io.percy.appium.lib;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.javafaker.Faker;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class TileTest {
    Faker faker = new Faker();
    Integer statusBarHeight = (int) faker.number().randomNumber(3, false);
    Integer navigationBarHeight = (int) faker.number().randomNumber(3, false);

    @Test
    public void testGetClientInfo() {
        Tile tile = new Tile("/tmp", statusBarHeight, navigationBarHeight, 0, 0, false);
        List<Tile> tiles = new ArrayList<Tile>();
        tiles.add(tile);
        JSONObject jsonTile = Tile.getTilesAsJson(tiles).get(0);
        Assert.assertEquals(jsonTile.getString("filepath"), "/tmp");
        Assert.assertEquals(jsonTile.getInt("statusBarHeight"), statusBarHeight.intValue());
        Assert.assertEquals(jsonTile.getInt("navBarHeight"), navigationBarHeight.intValue());
        Assert.assertEquals(jsonTile.getInt("headerHeight"), 0);
        Assert.assertEquals(jsonTile.getInt("footerHeight"), 0);
        Assert.assertEquals(jsonTile.get("fullscreen"), false);
    }

}
