package com.familyorg.familyorganizationapp.domain.search;

public enum CalendarField {
  FAMILY("family"), CALENDAR("calendar"), USER("user");

  private String display;

  CalendarField(String display) {
    this.display = display;
  }

  public String getDisplay() {
    return this.display;
  }
}
