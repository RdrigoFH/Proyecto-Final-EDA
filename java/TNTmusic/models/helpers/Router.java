package TNTmusic.models.helpers;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

public class Router {

  private static FileSystem sys = FileSystems.getDefault();
  
  public static String getResourcePath(String... routes) {
    String rootMaven = sys.getSeparator();
    return sys.getPath(rootMaven, routes).toString();
  }

  public static String getNotResourcePath(String... routes) {
    return "src/main/java/TNTmusic/data/" + String.join("/", routes);
  }

  public static String concatPath(String init, String... routes) {
    return sys.getPath(init, routes).toString() ;
  }

}
