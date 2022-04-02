package com.familyorg.familyorganizationapp.util;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
  /**
   * Checks if the string provided is a string of either length 6 or length 3 and only contains
   * valid hexadecimal digits.
   *
   * @param hexCode
   * @return
   */
  public static boolean isValidHexCode(String hexCode) {
    String regex = "([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    Pattern p = Pattern.compile(regex);

    if (hexCode == null) {
      return false;
    }

    Matcher matcher = p.matcher(hexCode);
    return matcher.matches();
  }

  /**
   * Converts a hexcode color string to a java.awt.Color object and returns it.
   *
   * @param hexCode
   * @return
   */
  public static Color hexToRGB(String hexCode) {
    if (!isValidHexCode(hexCode)) {
      throw new IllegalStateException("Hexcode supplied is invalid");
    }
    return Color.decode(hexCode);
  }
}
