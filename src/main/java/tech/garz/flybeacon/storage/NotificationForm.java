package tech.garz.flybeacon.storage;

public enum NotificationForm {
  CHAT("CHAT"),
  ACTIONBAR("ACTIONBAR");

  private String form;

  NotificationForm(String form) {
    this.form = form;
  }

  public String getForm() {
    return form;
  }

  public static String valuesToString() {
    NotificationForm[] vals = values();
    String[] options = new String[vals.length];
    for (int i = 0; i < vals.length; i++) {
      options[i] = vals[i].getForm();
    }
    return String.join(",", options);
  }
}
