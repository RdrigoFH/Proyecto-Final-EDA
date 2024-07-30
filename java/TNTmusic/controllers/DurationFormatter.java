package TNTmusic.controllers;

public class DurationFormatter {

  public static String parse(long duration) {
    String seconds = String.valueOf((duration / 1000) % 60);
    if (seconds.length() == 1)
      seconds = "0" + seconds;
    String minutes = String.valueOf((duration / (1000 * 60)) % 60);
    if (minutes.length() == 1)
      minutes = "0" + minutes;
    String res = String.format("%s:%s", minutes, seconds);
    String hours = String.valueOf(duration / (1000 * 60 * 60));
    if (hours.equals("0"))
      return res;
    if (hours.length() == 1)
      hours = "0" + hours;
    return hours + ":" + res;
  }

  public static String parseSeconds(long duration) {
    String seconds = String.valueOf(duration % 60);
    if (seconds.length() == 1)
      seconds = "0" + seconds;
    String minutes = String.valueOf((duration / 60) % 60);
    if (minutes.length() == 1)
      minutes = "0" + minutes;
    String res = String.format("%s:%s", minutes, seconds);
    String hours = String.valueOf(duration / (60 * 60));
    if (hours.equals("0"))
      return res;
    if (hours.length() == 1)
      hours = "0" + hours;
    return hours + ":" + res;
  }

}
