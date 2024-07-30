package TNTmusic;

import TNTmusic.dataStructures.interfaces.List;
import TNTmusic.dataStructures.list.StackLink;
import TNTmusic.dataStructures.tree.BTree;
import TNTmusic.models.Biblioteca;
import TNTmusic.models.Cancion;
import TNTmusic.models.helpers.Router;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application{
  public static void main( String[] args ) {
    //testsXD();
    launch(args);
  }

  private static void testsXD () {
    StackLink<Integer> s = new StackLink<>();
    int arr[] = { 1,2,3,4,5,6,7,8,9,10 };
    for (int i : arr) {
      s.add(i);
    }
    s.pop();
    System.out.println(s);
    System.out.println(s.search(3));
    while (!s.isEmpty())
      System.out.println(s.pop());
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    UserAgentBuilder.builder()
      .themes(JavaFXThemes.MODENA)
      .themes(MaterialFXStylesheets.forAssemble(true))
      .setDeploy(true)
      .setResolveAssets(true)
      .build()
      .setGlobal();

    String fxmlPath = Router.getResourcePath("fxml", "MisCanciones.fxml");
    System.out.println(fxmlPath);  // Imprimir la ruta para depuración

    Parent root = FXMLLoader.load(getClass().getResource(Router.getResourcePath("fxml", "MisCanciones.fxml")));
    primaryStage.setTitle("Init App");
    primaryStage.setScene(new Scene(root));
    primaryStage.setMinWidth(500.0);
    primaryStage.setMinHeight(400.0);
    primaryStage.setMaximized(true);
    primaryStage.show();
    final Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
      @Override
      protected ObservableList<String> call() throws Exception {
        Biblioteca.getBiblioteca();
        return FXCollections.observableArrayList("Loading");
      }
    };
    new Thread(task).start();
    primaryStage.setOnCloseRequest(w -> System.exit(0));
    //Borrar:
  }
}
