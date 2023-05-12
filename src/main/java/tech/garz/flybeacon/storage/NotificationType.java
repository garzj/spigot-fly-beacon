package tech.garz.flybeacon.storage;

public enum NotificationType {
  ALL("ALL"),
  INFO("INFO"),
  WARN("WARN"),
  NONE("NONE");

  private String type;

  NotificationType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static String valuesToString() {
    NotificationType[] vals = values();
    String[] options = new String[vals.length];
    for (int i = 0; i < vals.length; i++) {
      options[i] = vals[i].getType();
    }
    return String.join(",", options);
  }
}
