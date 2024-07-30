package TNTmusic.controllers;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.kordamp.ikonli.material2.Material2RoundMZ;

import TNTmusic.dataStructures.interfaces.List;
import TNTmusic.dataStructures.list.ListaArray;
import TNTmusic.models.Biblioteca;
import TNTmusic.models.Cancion;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXComboBoxCell;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListCell;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BibliotecaController {

  @FXML
  private MFXComboBox<String> ordenes;

  @FXML
  private MFXTextField numberOfElements;

  @FXML
  private MFXLegacyListView<Cancion> listView;

  @FXML
  private FontIcon toggleIcon;

  @FXML
  private MFXButton botonOrdenar;

  private static String fields [] = {
    "Artista",
    "Nombre de c",
    "Año",
    "Popularidad",
    "Duración"
  };

  private static String fieldBiblioteca [] = {
    "artist_name",
    "track_name",
    "year",
    "popularity",
    "duration_ms"
  };

  private static Biblioteca biblioteca = Biblioteca.getBiblioteca();

  // Permite escuchar el evento de hacer clic en los detalles de la canción
  private final SimpleObjectProperty<Cancion> changeDescription = new SimpleObjectProperty<>();

  private boolean isAscending;

  public void initialize() {
    this.isAscending = true;
    ObservableList<String> optionsOrdenes = FXCollections.observableArrayList(BibliotecaController.fields);
    this.ordenes.setCellFactory(f -> new CamposCellList(ordenes, f));
    this.ordenes.setItems(optionsOrdenes);
    this.ordenes.selectFirst();
    //para las canciones
    //ObservableList<Cancion> songs = FXCollections
    //  .observableArrayList(this.playlist.getPlaylist().toArray(Cancion.class));
    this.listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    this.listView.setCellFactory(c -> new CancionCellList());
    new Thread(() -> {
      while (true) {
        if (BibliotecaController.biblioteca.isReady()){
          Platform.runLater(() -> botonOrdenar.setText("Ordenar"));
          break;
        }
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  public ReadOnlyObjectProperty<Cancion> selectEmitter() {
    return this.listView.getSelectionModel().selectedItemProperty();
  }

  public ObservableValue<Cancion> emitDescription() {
    return this.changeDescription;
  }

  private String getParameter() {
    String field = this.ordenes.getSelectedItem(); 
    int i;
    for (i = 0; i < BibliotecaController.fields.length; i++)
      if (field.equals(BibliotecaController.fields[i])) break;
    field = BibliotecaController.fieldBiblioteca[i]; 
    return field;
  }

  private int getNElements() {
    try {
      int n = Integer.parseInt( this.numberOfElements.getText() );
      return n;
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    this.numberOfElements.setText(Integer.toString(40));
    return 40;
  }
  
  @FXML
  public void sortCanciones() {
    // Obtener playlist y renderizar
    String field = this.getParameter(); 
    ObservableList<Cancion> list = FXCollections
        .observableArrayList();
    this.listView.setItems(list);
    int i = 0;
    try {
      for (List<Cancion> l : BibliotecaController.biblioteca.getSortField(field, getNElements())){
        if (i++ < 50){
          list.add(l.getFirst());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void sortReverseCanciones() {
    // Obtener playlist y renderizar
    String field = this.getParameter(); 
    ObservableList<Cancion> list = FXCollections
        .observableArrayList();
    this.listView.setItems(list);
    int i = 0;
    try {
      for (List<Cancion> l : BibliotecaController.biblioteca.getSortDescendingField(field, getNElements())){
        if (i++ < 50){
          list.add(l.getFirst());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void changeOrder () {
    this.isAscending = ! this.isAscending;
    if (this.isAscending) this.sortCanciones();
    else this.sortReverseCanciones();
  }

  @FXML
  public void searchMusics(String query) {
    ObservableList<Cancion> list = FXCollections
      .observableArrayList();
    this.listView.setItems(list);
    int i = 0;
    try {
      for (Cancion c : this.autoCastQuery(query) )
        if (i++ > getNElements()) break;
        else 
          list.add(c);
    } catch (Exception e) {
      System.err.println(e);
    }

  }

  private List<Cancion> autoCastQuery(String query) {
    String field = this.getParameter();
    if (field.equals("artist_name") || field.equals("track_name")) 
      return BibliotecaController.biblioteca.getField(field, query);
    if (field.equals("popularity") || field.equals("year")) 
      return BibliotecaController.biblioteca.getField(field, Short.parseShort(query));
    if (field.equals("duration")) 
      return BibliotecaController.biblioteca.getField(field, Integer.parseInt(query));
    return new ListaArray<>();
  }

  private static class CamposCellList extends MFXComboBoxCell<String> {

    CamposCellList(MFXComboBox<String> box, String campo) {
      super(box, campo);
      setData(campo);
      this.render(getData());
    }

    @Override
    protected void render(String campo) {
      super.render(campo);
      if (campo == null)
        return;
      Label campoText = new Label(campo);
      super.getChildren().setAll(campoText);
    }
  }

  private class CancionCellList extends MFXLegacyListCell<Cancion> {

    private final HBox container = new HBox();

    CancionCellList() {
      super();
      container.setSpacing(15);
      container.setAlignment(Pos.CENTER);
    }


    @Override
    protected void updateItem(Cancion song, boolean empty) {
      super.updateItem(song, empty);
      if (song == null || empty) {
        setGraphic(null);
        setText(null);
        setDisable(true);
        return;
      }
      setDisable(false);

      setText("");

      VBox nameAndArtist = new VBox();
      Label titleSong = new Label(song.getTrack_name());
      titleSong.getStyleClass().add("title-song");
      Label artistName = new Label(song.getArtist_name());
      artistName.getStyleClass().add("artist-name-song");
      nameAndArtist.getChildren().addAll(titleSong, artistName);
      HBox iconCurrentPlay = new HBox(); // Possible icon
      iconCurrentPlay.setAlignment(Pos.CENTER_RIGHT);
      if (isSelected()) {
        FontIcon icon = new FontIcon(Material2RoundAL.BAR_CHART);
        icon.getStyleClass().add("icon-selected-song");
        iconCurrentPlay.getChildren().add(icon);
      } else {
        iconCurrentPlay.getChildren().clear();
      }
      HBox.setHgrow(iconCurrentPlay, Priority.ALWAYS);

      Label duration = new Label(DurationFormatter.parse(song.getDuration_ms()));
      duration.getStyleClass().add("duration-song");

      FontIcon detailsIcon = new FontIcon(Material2RoundMZ.VIEW_HEADLINE);
      MFXButton details = new MFXButton("", detailsIcon);
      details.setOnMouseClicked(e -> changeDescription(song));
      details.getStyleClass().add("details-button");

      container.getChildren().setAll(nameAndArtist, iconCurrentPlay, duration, details);

      setGraphic(container);

    }
  }
 
  private void changeDescription(Cancion c) {
    this.changeDescription.set(c);
  }


}

