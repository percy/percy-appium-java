package io.percy.appium.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.percy.appium.AppPercy;

public class Utils {
  public static Integer extractStatusBarHeight(String input) {
    try {
      Pattern pattern = Pattern.compile("ITYPE_STATUS_BAR frame=\\[\\d+,\\d+\\]\\[\\d+,([\\d]+)\\]");
      Matcher matcher = pattern.matcher(input);
      if (matcher.find()) {
        return Integer.parseInt(matcher.group(1));
      }
      return null; // Return -1 if no match found
    } catch (Exception ex) {
      AppPercy.log(ex.toString(), "debug");
      return null;
    }
  }

  public static Integer extractNavigationBarHeight(String input) {
    try {
      Pattern pattern = Pattern.compile("ITYPE_NAVIGATION_BAR frame=\\[\\d+,([\\d]+)\\]\\[\\d+,([\\d]+)\\]");
      Matcher matcher = pattern.matcher(input);

      if (matcher.find()) {
        int bottomCoordinate = Integer.parseInt(matcher.group(1));
        int topCoordinate = Integer.parseInt(matcher.group(2));
        // int topCoordinate = extractTopCoordinate(input); // Extracting top coordinate
        // from input
        return topCoordinate - bottomCoordinate; // Calc
      }
      return null; // Return -1 if no match found
    } catch (Exception ex) {
      AppPercy.log(ex.toString(), "debug");
      return null;
    }
  }
}
