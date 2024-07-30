package TNTmusic.controllers;

import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.kordamp.ikonli.material2.Material2RoundMZ;

import TNTmusic.models.Cancion;
import TNTmusic.models.Playlist;
import TNTmusic.models.helpers.Campos;
import TNTmusic.models.helpers.ComparadoresParaCancion;
import TNTmusic.models.helpers.FormaReproduccion;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.cell.MFXComboBoxCell;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListCell;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PlaylistController {

  @FXML
  private VBox rootList;

  @FXML
  private MFXComboBox<Campos> ordenes;

  @FXML
  private MFXButton orderButton;

  @FXML
  private MFXButton personalOrderButton;

  @FXML
  private MFXLegacyListView<Cancion> listView;

  @FXML
  private StackPane containerList;

  @FXML
  private FontIcon toggleIcon;

  private Playlist playlist;

  private OrderView orderView;

  private boolean currentTypeOrder;

  private Cancion originalSelectedSong;

  private boolean onDrag;

  private Task<String> loadingPlaylist;

  // Permite escuchar el evento de hacer clic en los detalles de la canción
  private final SimpleObjectProperty<Cancion> changeDescription = new SimpleObjectProperty<>();

  public void initialize() {

    loadingPlaylist = new Task<String>() {
      @Override
      public String call() throws Exception {
        playlist = Playlist.getInstance();
        return "Playlist cargada exitosamente";
      }
    };
    ObservableList<Cancion> songs = FXCollections.observableArrayList();
    loadingPlaylist.stateProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == Worker.State.RUNNING) {
        containerList.getChildren().setAll(new Text("Cargando"));
        disabledInteraction(true);
      } else if (newValue == Worker.State.SUCCEEDED) {
        containerList.getChildren().setAll(listView);
        if (playlist.getModo() == FormaReproduccion.ORDENADO) {
          ordenarPor();
        } else {
          personalOrder();
        }
        listView.getSelectionModel().select(playlist.getCurrentSong());
        disabledInteraction(false);
        Stage stage = (Stage) rootList.getScene().getWindow();
        stage.setOnCloseRequest(e -> {
          playlist.save();
        });
      } else if (newValue == Worker.State.FAILED) {
        containerList.getChildren().setAll(new Text("Error al cargar la playlist"));
      }
    });

    loadingPlaylist.setOnFailed(e -> {
      loadingPlaylist.getException().printStackTrace();
    });

    Thread playlistThread = new Thread(loadingPlaylist, "loadingPlaylist");
    playlistThread.setDaemon(true);
    playlistThread.start();

    ObservableList<Campos> optionsOrdenes = FXCollections.observableArrayList(Campos.values());
    this.ordenes.setCellFactory(f -> new CamposCellList(ordenes, f));
    this.ordenes.setItems(optionsOrdenes);
    this.ordenes.selectFirst();
    this.currentTypeOrder = ComparadoresParaCancion.campo(ordenes.getSelectedItem()).isUpward();

    this.listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    this.listView.setCellFactory(c -> new CancionCellList());
    this.listView.setItems(songs);

    this.settingDragAndSelectList();

    this.orderView = OrderView.PERSONAL;
  }

  public Playlist getPlaylist() {
    return this.playlist;
  }

  public int currentIndexItem() {
    return this.listView.getSelectionModel().getSelectedIndex();
  }

  public MFXLegacyListView<Cancion> getListView() {
    return this.listView;
  }

  public ReadOnlyObjectProperty<Cancion> selectEmitter() {
    return this.listView.getSelectionModel().selectedItemProperty();
  }

  public ObservableValue<Cancion> emitDescription() {
    return this.changeDescription;
  }

  private void settingDragAndSelectList() {
    this.listView.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
    this.listView.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
      if (e.isSecondaryButtonDown()) {
        originalSelectedSong = listView.getSelectionModel().getSelectedItem();
        listView.getSelectionModel().clearSelection();
        onDrag = true;
      }
    });
    this.listView.addEventFilter(MouseEvent.DRAG_DETECTED, e -> {
      if (e.isPrimaryButtonDown()) {
        e.consume();
      }
    });
    this.listView.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
      onDrag = false;
    });
    this.listView.getSelectionModel().selectedItemProperty().addListener(e -> {
      if (onDrag && !listView.getSelectionModel().getSelectedItem().equals(originalSelectedSong)) {
        listView.getSelectionModel().select(originalSelectedSong);
        System.out.println("Se volvió a la canción seleccionada anteriormente");
        return;
      }
      // Aquí se debe capturar el evento para cambiar de canción si se selecciona con
      // el click derecho
      Cancion selected = this.listView.getSelectionModel().getSelectedItem();
      if (selected != null && !selected.equals(originalSelectedSong)) {
        System.out.println("Se tocará: " + selected);
        this.playlist.loadCancion(selected);
      }

    });
  }

  private void disabledInteraction(boolean disabled) {
    ordenes.setDisable(disabled);
    orderButton.setDisable(disabled);
    personalOrderButton.setDisable(disabled);
  }

  public Task<String> getLoaderPlaylist() {
    return this.loadingPlaylist;
  }

  public void select(Cancion c) {
    this.listView.getSelectionModel().select(c);
  }

  @FXML
  public void personalOrder() {
    // Obtener playlist y renderizar
    this.orderView = OrderView.PERSONAL;
    ObservableList<Cancion> personalOrder = FXCollections
        .observableArrayList(this.playlist.getPlaylist().toArray(Cancion.class));
    this.listView.setItems(personalOrder);
  }

  @FXML
  public void ordenarPor() {
    this.orderView = OrderView.ORDER;
    loadCurrentView();
  }

  @FXML
  public void changeOrder() {
    if (this.currentTypeOrder) {
      this.toggleIcon.setIconCode(FontAwesome.ARROW_UP);
    } else {
      this.toggleIcon.setIconCode(FontAwesome.ARROW_DOWN);
    }
    this.currentTypeOrder = !this.currentTypeOrder;
    loadCurrentView();

  }

  private void loadCurrentView() {
    Campos campoActual = this.ordenes.getSelectedItem();
    ObservableList<Cancion> order = FXCollections
        .observableArrayList(this.playlist.ordenarPor(campoActual, this.currentTypeOrder).toArray(Cancion.class));
    this.listView.setItems(order);
  }

  private static class CamposCellList extends MFXComboBoxCell<Campos> {

    CamposCellList(MFXComboBox<Campos> box, Campos campo) {
      super(box, campo);
      setData(campo);
      this.render(getData());
    }

    @Override
    protected void render(Campos campo) {
      super.render(campo);
      if (campo == null)
        return;
      Label campoText = new Label(campo.getDisplayName());
      super.getChildren().setAll(campoText);
    }
  }

  private class CancionCellList extends MFXLegacyListCell<Cancion> {

    private final HBox container = new HBox();

    CancionCellList() {
      super();
      setUpDragAndDrop();
      container.setSpacing(15);
      container.setAlignment(Pos.CENTER);
    }

    private void setUpDragAndDrop() {
      this.setOnDragDetected(event -> {
        if (orderView == OrderView.PERSONAL) {
          Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
          ClipboardContent content = new ClipboardContent();
          content.putString(String.valueOf(getIndex()));
          dragboard.setContent(content);
          onDrag = true;
          event.consume();
        }
      });

      this.setOnDragOver(event -> {
        if (event.getGestureSource() != this && event.getDragboard().hasString()) {
          event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
      });
      this.setOnDragEntered(event -> {
        if (event.getGestureSource() != this && event.getDragboard().hasString()) {
          if (event.getDragboard().hasString()) {
            int draggedIndex = Integer.parseInt(event.getDragboard().getString());
            int thisIndex = getIndex();
            if (draggedIndex > thisIndex)
              getStyleClass().add("placeholder-up");
            else
              getStyleClass().add("placeholder-down");
          }
        }
      });
      this.setOnDragExited(event -> {
        if (event.getGestureSource() != this && event.getDragboard().hasString()) {
          getStyleClass().remove("placeholder-up");
          getStyleClass().remove("placeholder-down");
          onDrag = false;
        }
      });
      this.setOnDragDropped(event -> {
        if (event.getDragboard().hasString()) {
          int draggedIndex = Integer.parseInt(event.getDragboard().getString());
          int thisIndex = getIndex();
          if (draggedIndex != thisIndex) {
            ObservableList<Cancion> items = listView.getItems();
            Cancion draggedItem = items.remove(draggedIndex);
            items.add(thisIndex, draggedItem);
            playlist.cambiarPosición(draggedIndex, thisIndex);
            event.setDropCompleted(true);
          }
        } else {
          event.setDropCompleted(false);
        }
        onDrag = false;
        event.consume();
      });
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
