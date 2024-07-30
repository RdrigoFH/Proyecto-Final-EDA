package TNTmusic.controllers;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import TNTmusic.models.Cancion;
import TNTmusic.models.Playlist;
import TNTmusic.models.helpers.FormaReproduccion;
import TNTmusic.models.helpers.Router;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MisCancionesController {

  @FXML
  private StackPane root;

  @FXML
  private BorderPane layout;

  @FXML
  private Label showPopularity;

  @FXML
  private Label showNombre;

  @FXML
  private Label showArtista;

  @FXML
  private Label showGenero;

  @FXML
  private Label showAno;

  @FXML
  private Label showDuracion;

  @FXML
  private ImageView imageContainer;

  @FXML
  private MFXButton searchButton;

  @FXML
  private MFXTextField seachField;

  @FXML
  private FontIcon changeIcon;

  @FXML
  private FontIcon playAndPauseIcon;

  @FXML
  private ImageView coverImg;

  @FXML
  private Label timeCounter;

  @FXML
  private Label totalTime;

  @FXML
  private Label nameSong;

  @FXML
  private Region leftBackground;

  @FXML
  private MFXButton removeOrAddButton;

  @FXML
  private MFXSlider sliderTime;

  private PlaylistController playlistController;

  private BibliotecaController bibliotecaController;

  private boolean isShowingPlayList;

  private TokenManager tokenManager;

  private Cancion lastSongShowed;

  private FormaReproduccion mode;

  private ModeChanger modeChanger;

  private Playlist playlist;

  private boolean loadingPlaylistComplete;

  private boolean playing;

  private boolean removing;

  private Timeline currentTimeline;

  @FXML
  public void initialize() {
    tokenManager = new TokenManager();
    playing = true;
    loadingPlaylistComplete = false;
    this.currentTimeline = new Timeline();
    this.sliderTime.valueProperty().subscribe(e -> {
      double currentValue = sliderTime.getValue();
      this.timeCounter.setText(DurationFormatter.parse((long) currentValue * 1000));
      if(((long)this.sliderTime.getMax()) == ((long)this.sliderTime.getValue())) {
        this.nextSong();
      }
    });
    loadGeneralDescription();
    setPlayList();
    playlistController.getLoaderPlaylist().stateProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == Worker.State.SUCCEEDED) {
        playlist = playlistController.getPlaylist();
        mode = playlist.getModo();
        modeChanger = new ModeChanger(mode);
        loadingPlaylistComplete = true;
      }
    });

  }

  @FXML
  public void pipeButtonDescription() {
    if (loadingPlaylistComplete) {
      if (isShowingPlayList) {
        this.removing = true;
        this.playlist.delete(lastSongShowed, this.playlistController.currentIndexItem());
        this.playlistController.getListView().getItems().remove(lastSongShowed);
        lastSongShowed = null;
        loadGeneralDescription();
      } else {
        // El botón debe estar deshabilitado para los que ya están en la playlist
        this.playlist.add(lastSongShowed);
        this.removeOrAddButton.setDisable(true);
      }
    }
  }

  private void loadGeneralDescription() {
    this.showPopularity.setText("");
    this.showAno.setText("");
    this.showArtista.setText("");
    this.showDuracion.setText("");
    this.showGenero.setText("");
    this.showNombre.setText("Selecciona una canción");


    Image albumImage = new Image(Router.getResourcePath("images", "vinilo.gif"));
    this.imageContainer.setImage(albumImage);
  }

  @FXML
  public void changeMode() {
    if (loadingPlaylistComplete) {
      modeChanger.next();
    }

  }

  @FXML
  public void prevSong() {
    if (loadingPlaylistComplete) {
      loadSong(this.playlist.prev());
    }
  }

  @FXML
  public void nextSong() {
    if (loadingPlaylistComplete) {
      loadSong(this.playlist.next());
    }
  }

  @FXML
  public void playAndPause() {
    if (loadingPlaylistComplete) {
      if (playing) {
        this.currentTimeline.pause();
        this.playAndPauseIcon.setIconCode(FontAwesome.PLAY);
      } else {
        this.currentTimeline.play();
        this.playAndPauseIcon.setIconCode(FontAwesome.PAUSE);
      }
      playing = !playing;
    }
  }

  public void setBiblioteca() {
    if (!isShowingPlayList)
      return;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(Router.getResourcePath("fxml", "Biblioteca.fxml")));
      VBox containerBiblioteca = loader.load();
      bibliotecaController = loader.getController();
      layout.setCenter(containerBiblioteca);
      this.isShowingPlayList = false;
      FontIcon icon = (FontIcon) this.removeOrAddButton.getGraphic();
      icon.setIconCode(FontAwesome.PLUS);
      this.removeOrAddButton.setStyle("-fx-background-color: green;");
      if (loadingPlaylistComplete && this.playlist.has(lastSongShowed)) {
        this.removeOrAddButton.setDisable(true);
      }
      bibliotecaController.selectEmitter().subscribe((c) -> {
        // @RODRIGO: Aquí obtienes la canción para reproducirla
        if (c != null && !c.equals(lastSongShowed)) {
          lastSongShowed = c;
          loadDescription(c);
        }
      });

      bibliotecaController.emitDescription().subscribe((c) -> {
        if (c != null && !c.equals(lastSongShowed)) {
          lastSongShowed = c;
          loadDescription(c);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void setPlayList() {
    if (isShowingPlayList)
      return;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(Router.getResourcePath("fxml", "Playlist.fxml")));
      VBox containerPlayList = loader.load();
      playlistController = loader.getController();
      layout.setCenter(containerPlayList);
      this.isShowingPlayList = true;
      FontIcon icon = (FontIcon) this.removeOrAddButton.getGraphic();
      icon.setIconCode(FontAwesome.TRASH);
      this.removeOrAddButton.setStyle("-fx-background-color: red;");
      this.removeOrAddButton.setDisable(false);
      playlistController.selectEmitter().subscribe((c) -> {
        // @RODRIGO: Aquí obtienes la canción para reproducirla
        if (removing) {
          removing = false;
          return;
        }
        if (c != null && !c.equals(lastSongShowed)) {
          lastSongShowed = c;
          loadSong(c);
          loadDescription(c);
        }
      });

      playlistController.emitDescription().subscribe((c) -> {
        if (removing) {
          removing = false;
          return;
        }
        if (c != null && !c.equals(lastSongShowed)) {
          lastSongShowed = c;
          loadDescription(c);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private class ModeChanger {
    private FormaReproduccion current;
    private int index;
    private final FormaReproduccion[] formas = FormaReproduccion.values();

    ModeChanger(FormaReproduccion current) {
      this.current = (current == null ? formas[0] : current);
      for (int i = 0; i < formas.length; i++) {
        if (formas[i].equals(this.current)) {
          index = i;
          break;
        }
      }
      changeIcon.setIconLiteral(this.current.getIcon().getIconLiteral());
      ;
    }

    public void next() {
      this.current = formas[++index % formas.length];
      changeIcon.setIconLiteral(this.current.getIcon().getIconLiteral());
      playlist.formaDeReproduccion(this.current);
    }

  }

  public void busqueda() {
    String query = seachField.getText();
    System.out.println("Se buscará: " + query);
    if (this.isShowingPlayList) {
      // Hacer lo que la playlist tenga que hacer
    } else {
      // Lo que la Biblioteca tenga que hacer
      this.bibliotecaController.searchMusics(query);
    }
  }

  private void loadDescription(Cancion c) {
    this.showPopularity.setText(c.getPopularity() + "");
    this.showAno.setText(c.getYear() + "");
    this.showArtista.setText(c.getArtist_name());
    this.showDuracion.setText(DurationFormatter.parse(c.getDuration_ms()));
    this.showGenero.setText(c.getGenre().substring(0, 1).toUpperCase() + c.getGenre().substring(1));
    this.showNombre.setText(c.getTrack_name());

    if (loadingPlaylistComplete) {
      System.out.println("Tengo esta canción en la playlist?: " + this.playlist.has(c));  
      System.out.println("Se está mostrando la playlist?: " + isShowingPlayList);    
    }

    if (loadingPlaylistComplete && !isShowingPlayList && this.playlist.has(c)) {
      this.removeOrAddButton.setDisable(true);
    } else {
      this.removeOrAddButton.setDisable(false);
    }

    Image albumImage = new Image(Router.getResourcePath("images", "vinilo.gif"));
    this.imageContainer.setImage(albumImage);

    this.loadImageDescription(c);

    System.out.println("Cambiar descripción con esta canción: " + c);
  }

  private void loadImageDescription(Cancion c) {
    final Task<String> task = new Task<String>() {
      @Override
      protected String call() throws Exception {
        try {
          HttpResponse<JsonNode> response = Unirest.get(tokenManager.getApiBaseUrl() + "/tracks/{id}")
              .header("Authorization", "Bearer " + tokenManager.getAccessToken())
              .routeParam("id", c.getTrack_id())
              .asJson();
          JSONObject trackObject = response.getBody().getObject();
          JSONObject albumObject = trackObject.getJSONObject("album");
          JSONArray imagesArray = albumObject.getJSONArray("images");

          if (imagesArray.length() > 0) {
            JSONObject img = imagesArray.getJSONObject(0);
            Image albumImage = new Image(img.getString("url"));
            imageContainer.setImage(albumImage);
            BackgroundImage b = new BackgroundImage(albumImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            leftBackground.setBackground(new Background(b));
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
        return "Loading";
      }
    };
    new Thread(task).start();

  }

  private void loadSong(Cancion c) {
    this.playlistController.select(c);
    this.totalTime.setText(DurationFormatter.parse(c.getDuration_ms()));
    this.nameSong.setText(c.getTrack_name());
    this.sliderTime.setMax(c.getDuration_ms() / 1000.0);
    this.sliderTime.setValue(0);
    currentTimeline.stop();
    this.sliderTime.setPopupSupplier(null);
    currentTimeline = new Timeline(
        new KeyFrame(
            Duration.seconds(1),
            event -> {
              double currentValue = sliderTime.getValue();
              sliderTime.setValue(currentValue + 1);
            }));
    currentTimeline.setCycleCount(Timeline.INDEFINITE);
    currentTimeline.play();
    loadImagePlayer(c);
  }

  private void loadImagePlayer(Cancion c) {
    final Task<String> task = new Task<String>() {
      @Override
      protected String call() throws Exception {
        try {
          HttpResponse<JsonNode> response = Unirest.get(tokenManager.getApiBaseUrl() + "/tracks/{id}")
              .header("Authorization", "Bearer " + tokenManager.getAccessToken())
              .routeParam("id", c.getTrack_id())
              .asJson();
          JSONObject trackObject = response.getBody().getObject();
          JSONObject albumObject = trackObject.getJSONObject("album");
          JSONArray imagesArray = albumObject.getJSONArray("images");

          if (imagesArray.length() > 0) {
            JSONObject img = imagesArray.getJSONObject(0);
            Image albumImage = new Image(img.getString("url"));
            coverImg.setImage(albumImage);
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
        return "Loading";
      }
    };
    new Thread(task).start();

  }
}
