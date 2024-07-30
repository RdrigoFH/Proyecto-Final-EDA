package TNTmusic.models;

import java.text.Normalizer.Form;
import java.util.Random;
import java.util.regex.Pattern;

import TNTmusic.dataStructures.interfaces.List;
import TNTmusic.dataStructures.interfaces.ListIterator;
import TNTmusic.dataStructures.interfaces.Stack;
import TNTmusic.dataStructures.list.DoubleLinkedList;
import TNTmusic.dataStructures.list.ListaArray;
import TNTmusic.dataStructures.list.StackLink;
import TNTmusic.exceptions.ItemNoFound;
import TNTmusic.exceptions.OutBoundIndex;
import TNTmusic.models.helpers.Campos;
import TNTmusic.models.helpers.Comparador;
import TNTmusic.models.helpers.ComparadoresParaCancion;
import TNTmusic.models.helpers.FormaReproduccion;
import TNTmusic.models.helpers.Reader;
import TNTmusic.models.helpers.Router;
import TNTmusic.models.helpers.Writer;

public class Playlist {

  private static Playlist instance;
  private static final Random r = new Random();
  private static final String MODO_PATH = Router.getNotResourcePath("playlist", "modo.dat");
  private static final String CURRENT_SONG_PATH = Router.getNotResourcePath("playlist", "currentSong.dat");
  private static final String PLAYLIST_PATH = Router.getNotResourcePath("playlist", "playlist.dat");

  private List<Cancion> personal;
  private List<Cancion> orden;
  private List<Cancion> sortedByID;
  private Stack<Cancion> prevRandomSave;
  private Stack<Cancion> nextRandomSave;

  private ListIterator<Cancion> iterator;

  private FormaReproduccion modo;
  private Cancion currentSong;
  private NextFunction toNext;
  private PrevFunction toPrev;

  private interface NextFunction {
    Cancion nextFunction();
  }

  private interface PrevFunction {
    Cancion prevFunction();
  }

  private Playlist() {

    this.prevRandomSave = new StackLink<>();
    this.nextRandomSave = new StackLink<>();

    // Leer el modo
    Reader<FormaReproduccion> modoReader = new Reader<FormaReproduccion>(MODO_PATH);
    this.modo = modoReader.read();
    if(this.modo == null)
      this.modo = FormaReproduccion.PERSONALIZADO;
    modoReader.close();

    // Leer  la playlist
    Reader<List<Cancion>> playlistReader = new Reader<List<Cancion>>(PLAYLIST_PATH);
    // Primero la lista enlazada
    this.personal = playlistReader.read();
    if(this.personal == null)
      this.personal = new DoubleLinkedList<>();
    // Por último la lista en arreglo
    this.orden = new ListaArray<>(this.personal);
    playlistReader.close();

    // Leer la canción actual
    Reader<Cancion> currentSongReader = new Reader<Cancion>(CURRENT_SONG_PATH);
    this.currentSong = currentSongReader.read();
    currentSongReader.close();

    // Se carga la forma de reproducción con los datos recolectados de los archivos
    if (modo == FormaReproduccion.ALEATORIO) {
      loadIterator();
    }
    this.formaDeReproduccion(modo);
    

  }

  public static Playlist getInstance() {
    if (instance == null) {
      return instance = new Playlist();
    }
    return instance;
  }

  public void add(Cancion c) {
    this.personal.addFirst(c);
    this.orden.addLast(c);
    this.nextRandomSave.push(c);
  }

  // Se puededar en dos ocasiones: cuando se esté mostrando la lsita personalizada
  // o
  // cuando se esté mostrando la lista ordenada.
  // Si se está mostrando la lista personalizada, se hace su remove por index y en la lista de orden
  // se hace el remove por objeto. Viceversa cuando se estémostrando la lista por orden
  public void delete(Cancion c, int index) {
    try {
      this.prevRandomSave.remove(c);
      this.nextRandomSave.remove(c);
      if(this.modo == FormaReproduccion.PERSONALIZADO){
        this.personal.remove(index);
        this.orden.remove(c);
      }else{
        this.personal.remove(c);
        this.orden.remove(index);
      }
    } catch (ItemNoFound e) {
      System.out.println("No se ha encontrado esta canción: " + c);
    } catch (OutBoundIndex e) {
      System.out.println("Fuera de índice: " + index + " en el arreglo: " + this.orden);
    }
  }

  public boolean has(Cancion c) {
    // Ojo que esto deberiamos cambiarlo por una lista que sea ordenada
    this.sortedByID = new ListaArray<>(this.orden);
    Comparador<Cancion> comp = ComparadoresParaCancion.campo(null);
    this.sortedByID.sort(comp);
    int l = 0, r = this.sortedByID.size() - 1;
    while (l <= r) {
      try {
        int m = l + (r - l) / 2;
        int resComp = comp.compare(this.sortedByID.get(m), c);
        if (resComp == 0) {
          return true;
        } else if (resComp > 0) {
          r = m - 1;
        } else {
          l = m + 1;
        }
        
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  public List<Cancion> ordenarPor(Campos c, boolean upward) {
    Comparador<Cancion> co = ComparadoresParaCancion.campo(c);
    if (!upward) {
      co.falling();
    }
    this.orden.sort(co);
    System.out.println("Ordenando por: " + c);
    for(Cancion ca : this.orden) {
      System.out.println(ca);
    }
    return this.orden;
  }

  public void cambiarPosición(int current, int newPos) {
    try {
      this.personal.changePos(current, newPos);
    } catch (OutBoundIndex e) {
      System.out.println("Fuera de los límites en esta lista: " + this.personal);
    }
  }

  private Cancion getPrevRandom() {
    if (this.orden.size() == 1) {
      return this.currentSong;
    }
    this.nextRandomSave.push(this.currentSong);
    if (this.prevRandomSave.isEmpty()) {
      loadStacks();
    }
    Cancion temp = this.currentSong;
    while (temp.equals(this.currentSong)) {
      if (this.prevRandomSave.isEmpty()) {
        loadStacks();
      }
      this.currentSong = this.prevRandomSave.pop();
    }
    return this.currentSong;

  }
  
  private Cancion getNextRandom() {
    if (this.orden.size() == 1) {
      return this.currentSong;
    }
    this.prevRandomSave.push(this.currentSong);
    if (this.nextRandomSave.isEmpty()) {
      loadStacks();
    }
    Cancion temp = this.currentSong;
    while (temp.equals(this.currentSong)) {
      if (this.nextRandomSave.isEmpty()) {
        loadStacks();
      }
      this.currentSong = this.nextRandomSave.pop();
    }
    return this.currentSong;
  }

  public Cancion next() {
    return toNext.nextFunction();
  }

  public Cancion prev() {
    return toPrev.prevFunction();
  }

  public Cancion getCurrentSong() {
    return this.currentSong;
  }

  public FormaReproduccion getModo() {
    return this.modo;
  }

  public Cancion loadCancion(Cancion c) {
    return this.currentSong = this.iterator.moveTo(c);
  }

  public List<Cancion> getPlaylist() {
    return this.personal;
  }

  public void formaDeReproduccion(FormaReproduccion f) {
    this.modo = f;
    switch (f) {
      case PERSONALIZADO:
        loadIterator();
        break;
      case ALEATORIO:
        loadStacks();
        this.toNext = () -> getPrevRandom();
        this.toPrev = () -> getNextRandom();
        break;
      case BUCLE_UNO:
        this.toNext = () -> this.currentSong;
        this.toPrev = () -> this.currentSong;
        break;
      case ORDENADO:
        loadIterator();
        break;
      default:
        System.out.println("No encontré esta forma de reproducción");
        break;
    }
    
  }

  public List<Cancion> search(String name) {
    List<Cancion> res = new ListaArray<>();
    String regex = ".*" + Pattern.quote(name) + ".*";
    for (Cancion c : this.personal) {
        if (c.getTrack_name().matches(regex)) {
            res.add(c);
        }
    }
    return res;
}

  public void save() {
    Writer<FormaReproduccion> modoWriter = new Writer<FormaReproduccion>(this.modo, MODO_PATH);
    modoWriter.write();
    modoWriter.close();

    Writer<Cancion> currentSongWriter = new Writer<Cancion>(this.currentSong, CURRENT_SONG_PATH);
    currentSongWriter.write();
    currentSongWriter.close();

    Writer<List<Cancion>> playlistWriter = new Writer<List<Cancion>>(personal, PLAYLIST_PATH);
    playlistWriter.write();
    playlistWriter.close();
  }

  private void loadToNextIterator() {
    if (this.modo == FormaReproduccion.PERSONALIZADO)
      this.iterator = this.personal.lIterator(false);
    else
      this.iterator = this.orden.lIterator(false);
  }

  private void loadToPrevIterator() {
    if (this.modo == FormaReproduccion.PERSONALIZADO)
      this.iterator = this.personal.lIterator(true);
    else
      this.iterator = this.orden.lIterator(true);
  }
 
  private void loadIterator() {
    loadToNextIterator();
    this.toNext = () -> {
      if (!this.iterator.hasNext())
        loadToNextIterator();
      return this.currentSong = this.iterator.next();
    };
    this.toPrev = () -> {
      if (!this.iterator.hasPrevious())
        loadToPrevIterator();
      this.iterator.moveTo(currentSong);
      return this.currentSong = this.iterator.previous();
    };
    this.iterator.moveTo(currentSong);
  }

  private void loadStacks() {
    int size = this.orden.size();
    boolean[] loaded = new boolean[size];
    this.prevRandomSave = new StackLink<>();
    this.nextRandomSave = new StackLink<>();
    try {
      int j = 0;
      for(int i = 0; i < size / 2;) {
        j = r.nextInt(size);
        if(!loaded[j]){
          this.prevRandomSave.push(this.orden.get(j));
          loaded[j] = true;
          i++;
        }
      }
      for(int i = size / 2; i < size;) {
        j = r.nextInt(size);
        if (!loaded[j]) {
          this.nextRandomSave.push(this.orden.get(j));
          loaded[j] = true;
          i++;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
