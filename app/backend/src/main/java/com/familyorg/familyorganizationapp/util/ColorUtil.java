package com.familyorg.familyorganizationapp.util;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
  public static boolean isValidHexCode(String hexCode) {
    String regex = "([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    Pattern p = Pattern.compile(regex);

    if (hexCode == null) {
      return false;
    }

    Matcher matcher = p.matcher(hexCode);
    return matcher.matches();
  }

  public static Color hexToRGB(String hexCode) {
    if (!isValidHexCode(hexCode)) {
      throw new IllegalStateException("Hexcode supplied is invalid");
    }
    return Color.decode(hexCode);
  }
}
