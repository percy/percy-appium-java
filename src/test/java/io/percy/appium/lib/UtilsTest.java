package io.percy.appium.lib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilsTest {
  @Test
  public void testExtractStatusBarHeighWhenPatternIsPresent() {
    String input = "InsetsSource type=ITYPE_STATUS_BAR frame=[0,0][2400,74] visible=true\n" +
        "InsetsSource type=ITYPE_NAVIGATION_BAR frame=[0,2358][1080,2400] visible=true";

    int expectedStatBarHeight = 74;
    int actualStatBarHeight = Utils.extractStatusBarHeight(input);

    assertEquals(expectedStatBarHeight, actualStatBarHeight);
  }

  @Test
  public void testExtractStatusBarHeighWhenNewPatternIsPresent() {
    String input = "InsetsSource type=statusBars frame=[0,0][2400,74] visible=true\n" +
        "InsetsSource type=navigationBars frame=[0,2358][1080,2400] visible=true";

    int expectedStatBarHeight = 74;
    int actualStatBarHeight = Utils.extractStatusBarHeight(input);

    assertEquals(expectedStatBarHeight, actualStatBarHeight);
  }

  @Test
  public void testExtractNavigationBarHeightWhenPatternIsPresent() {
    String input = "InsetsSource type=ITYPE_STATUS_BAR frame=[0,0][2400,74] visible=true\n" +
        "InsetsSource type=ITYPE_NAVIGATION_BAR frame=[0,2358][1080,2400] visible=true";

    int expectedNavBarHeight = 42;
    int actualNavBarHeight = Utils.extractNavigationBarHeight(input);

    assertEquals(expectedNavBarHeight, actualNavBarHeight);
  }

  @Test
  public void testExtractNavigationBarHeightWhenNewPatternIsPresent() {
    String input = "InsetsSource type=statusBars frame=[0,0][2400,74] visible=true\n" +
        "InsetsSource type=navigationBars frame=[0,2358][1080,2400] visible=true";

    int expectedNavBarHeight = 42;
    int actualNavBarHeight = Utils.extractNavigationBarHeight(input);

    assertEquals(expectedNavBarHeight, actualNavBarHeight);
  }

  @Test
  public void testExtractStatusBarHeighWhenPatternIsNotPresent() {
    String input = "RANDOM frame=[0,0][2400,74] visible=true\n" +
        "RANDOM frame=[0,2358][1080,2400] visible=true";

    Integer actualStatBarHeight = Utils.extractStatusBarHeight(input);

    assertEquals(null, actualStatBarHeight);
  }

  @Test
  public void testExtractNavigationBarHeightWhenPatternIsNotPresent() {
    String input = "RANDOM [0,0][2400,74] visible=true\n" +
        "RANDOM [0,2358][1080,2400] visible=true";

    Integer actualNavBarHeight = Utils.extractNavigationBarHeight(input);

    assertEquals(null, actualNavBarHeight);
  }
}
