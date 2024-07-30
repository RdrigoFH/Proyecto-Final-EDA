package TNTmusic.models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import TNTmusic.dataStructures.interfaces.List;
import TNTmusic.dataStructures.interfaces.Map;
import TNTmusic.dataStructures.list.ListaArray;
import TNTmusic.dataStructures.tree.MapBTree;
import TNTmusic.exceptions.ItemNoFound;
import TNTmusic.models.helpers.Reader;
import TNTmusic.models.helpers.Router;
import TNTmusic.models.helpers.Writer;
import javafx.concurrent.Task;

/*
 * NOTAS: que sea singleton
 *
 * */

public class Biblioteca {
  private static int DATA_SIZE = 1473495;
  private static Biblioteca b;
  //End with "/"
  private static String PATH_ARRAYS = Router.getNotResourcePath("objectFiles", "arrays");
  private static String PATH_CSV = Router.getResourcePath("saveFiles", "csv");
  private static String PATH_DIR_TREES = Router.getNotResourcePath( "objectFiles", "trees");
  private Map<String, Map<? extends Comparable<?>, List<Integer>> > fields;
  private Cancion[] directAddressing;
  private boolean cancionesReady;
  private boolean fieldReady;

  public static Biblioteca getBiblioteca () {
    if (b == null) {
      synchronized (Biblioteca.class) {
        if (b == null) {
          b = new Biblioteca();
          if (b.directAddressing == null) {
            Thread c  = new Thread(() -> {
              System.out.println("Se cargarán las canciones");
              b.loadDirectAddressing();
              b.cancionesReady = true;
            });
            c.start();
          }
          if (b.fields == null) {
            Thread f = new Thread(() -> {
              System.out.println("Se cargará los árboles");
              b.loadFields();
              b.fieldReady = true;
            });
            f.start();
          }
          b.cancionesReady = false;
          b.fieldReady = false;
        }
      }
    }
    return b;
  }

  private Biblioteca() {
    //if ( directAddressing == null ) loadDirectAddressing();
    //if (fields == null) loadFields();
  }

  public boolean isCancionesReady () {
    return this.cancionesReady;
  }

  public boolean isFieldReady () {
    return this.fieldReady;
  }

  public boolean isReady() {
    return this.cancionesReady && this.fieldReady;
  }

  public synchronized void loadDirectAddressing() {
    if (this.directAddressing != null) return;
    this.directAddressing = new Cancion[DATA_SIZE];
    boolean lecturaCompleta = false;
    while (! lecturaCompleta){
      try { 
        String path;
        for (int i = 0; i < 10; i++) {
          String filePath = Router.concatPath(PATH_ARRAYS, "data%d.dat");
          path = String.format( filePath, i );
          Reader<Cancion> input = new Reader<>(path);
          System.out.println("Procesando archivo de objetos " + i );
          if (!input.fileExists()) {
            throw new FileNotFoundException();
          }
          // Lee objetos hasta que se llegue al final del archivo
          Cancion cancion = input.read();
          while (cancion != null) {
              directAddressing[cancion.getId()] = cancion;
              cancion = input.read();
          }
          input.close();
          System.out.println("Array " + i + " leido");
        }
        lecturaCompleta = true;
      } catch (FileNotFoundException e) {
        System.out.println("Se crearán los archivos de objetos");
        writeBytecodeFile();
      }
    }     
    System.out.println("Lectura del array completa");
    /*
     * Leer archivo de objetos
     * Si no está, crear archivos
     *  crear directorio
     *  leer archivos csv
     *  procesas sus datos en un array
     *  guardar los array en los 10 archivos
     * Si está, leer los archivos cada uno
     * crear el super array
     * */
  }

  private void writeBytecodeFile() {
    for (int i = 0; i < 10; i++){
      String fileName = String.format(Router.concatPath(PATH_CSV, "data%d.csv"), i);
      System.out.println(fileName);
      try (InputStream inputStream = Biblioteca.class.getResourceAsStream(fileName);
          BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        Cancion cancion;
        String p = String.format(Router.concatPath(PATH_ARRAYS, "data%d.dat"), i);
        System.out.println("Se escribirá en: " + p);
        Writer<Cancion> outputStream = new Writer<Cancion>(p);
        System.out.println("Leyendo y escribiendo: " + i);
        while ((line = reader.readLine()) != null) {
          cancion = cancionFromData(line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
          outputStream.setObject(cancion);
          outputStream.write();
        }
        outputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Escritura finalizada");
  }

  public void writeTrees () {
    System.out.println("Se guardarán los árboles en:" + PATH_DIR_TREES);
    this.fields = new MapBTree<>();
    this.fields.set("artist_name", new MapBTree<String, List<Integer>>());
    this.fields.set("track_name", new MapBTree<String, List<Integer>>());
    this.fields.set("year", new MapBTree<Short, List<Integer>>() ); 
    this.fields.set("popularity", new MapBTree<Short, List<Integer>> ()); 
    this.fields.set("duration_ms", new MapBTree<Integer, List<Integer>> ()); 
    Writer<?> outputStream;
    List<Integer> l;
    for (Cancion c : directAddressing) {
      if (c == null) continue;
      l = this.getIds("artist_name", c.getArtist_name());
      if (l == null) l =  this.createListID("artist_name", c.getArtist_name());
      l.add(c.getId());
      l = this.getIds("track_name", c.getTrack_name());
      if (l == null) l =  this.createListID("track_name", c.getTrack_name());
      l.add(c.getId());
      l = this.getIds("year", c.getYear());
      if (l == null) l =  this.createListID("year", c.getYear());
      l.add(c.getId());
      l = this.getIds("popularity", c.getPopularity());
      if (l == null) l =  this.createListID("popularity", c.getPopularity());
      l.add(c.getId());
      l = this.getIds("duration_ms", c.getDuration_ms());
      if (l == null) l =  this.createListID("duration_ms", c.getDuration_ms());
      l.add(c.getId());
    }
    String fields[] = { "artist_name", "track_name", "year", "popularity", "duration_ms" };
    //System.out.println("Se intentará recuperar canciones del 2012");
    //try {
    //  for (Cancion c : getField("year", (short)2001))
    //    System.out.println(c.getYear() + "->" + c);
    //} catch (Exception e) {
    //  System.err.println(e);
    //}
    for (String f : fields) {
      String p = Router.concatPath(PATH_DIR_TREES, f + ".dat");
      System.out.println("Se guardará archivo: " + p);
      outputStream = new Writer<>(this.fields.get(f), p);
      outputStream.write();
      outputStream.close();
    }
    System.out.println("Guardado completado");
      //outputStream = new Writer<Cancion>(String.format(Router.concatPath( "" , "%d.dat"), c.getId()));
      //System.out.println("Se escribe canción: " + c );
      //outputStream.setObject(c);
      //outputStream.write();
      //outputStream.close();
  }

  private Cancion cancionFromData (String [] songData) {
    int id = Integer.parseInt(songData[0]);
    String artist_name = songData[1];
    String track_name = songData[2];
    String track_id = songData[3];
    short popularity = Short.parseShort(songData[4]);
    short year = Short.parseShort(songData[5]);
    String genre = songData[6];
    double danceability = Double.parseDouble(songData[7]);
    double energy = Double.parseDouble(songData[8]);
    byte key = Byte.parseByte(songData[9]);
    double loudness = Double.parseDouble(songData[10]);
    byte mode = Byte.parseByte(songData[11]);
    double speechiness = Double.parseDouble(songData[12]);
    double acousticness = Double.parseDouble(songData[13]);
    double instrumentalness = Double.parseDouble(songData[14]);
    double liveness = Double.parseDouble(songData[15]);
    double valence = Double.parseDouble(songData[16]);
    double tempo = Double.parseDouble(songData[17]);
    int duration_ms = Integer.parseInt(songData[18]);
    byte time_signature = Byte.parseByte(songData[19]);

    return new Cancion(id, artist_name, track_name, track_id, 
        popularity, year, genre, danceability, 
        energy, key, loudness, mode, speechiness, 
        acousticness, instrumentalness, liveness,
        valence, tempo, duration_ms, time_signature);
  }

  public synchronized void loadFields(){
    if (this.fields != null) return;
    boolean lecturaCompleta = false;
    String options[] = { "artist_name", "track_name", "year", "popularity", "duration_ms" };
    //String path = PATH_DIR_TREES;
    Reader< MapBTree<? extends Comparable<?>, List<Integer>>> input;
    //MapBTree<? extends Comparable<?>, List<Integer>> t;
    while (! lecturaCompleta){
      this.fields = new MapBTree<>();
      try {
        for (String f:options) {
          String p = Router.concatPath(PATH_DIR_TREES, f + ".dat");
          System.out.println("Se leerá: " + p);
          input = new Reader<>(p);
          if (! input.fileExists()) throw new FileNotFoundException();
          MapBTree<? extends Comparable<?>, List<Integer>> m = input.read();
          if (m == null) throw new NoSuchElementException("No se leyó el arbol: " + p);
          this.fields.set(f, m);
        }
        lecturaCompleta = true;
      } catch (Exception e) {
        System.out.println(e);
        System.out.println("Se escribirán los árboles");
        this.writeTrees();
      }
    }

  }

  public Cancion getCancion(int i) {
    return directAddressing[i];
  }

  public <T extends Comparable<T>> List<Cancion> getField(String param, T key) {
    List <Cancion> l = new ListaArray<>();
    ListaArray<Integer> ids = (ListaArray<Integer>) this.getIds(param, key);
    System.out.println("Se realizará la búsqueda de: " + param + " para: " + key);
    for (int i : ids) 
      l.add(this.directAddressing[i]);
    return l;
  }

  public void setField(String key, Map<Comparable<?>, List<Integer>> value) {
    this.fields.set(key, value);
  }

  public  List<List<Cancion>> getSortField(String param, int n) {
    List<List<Cancion>> l = new ListaArray<List<Cancion>>();
    try {
      MapBTree<? extends Comparable<?>, List<Integer>> inner = (MapBTree<? extends Comparable<?>, List<Integer>>) this.fields.get(param);
      if (inner == null) throw new NoSuchElementException("No se encontró el campo " + param );
      for ( List<Integer> ids: inner.sortValues()) {
        System.out.println("Procesando elemento de " + param + " " + n + " restantes");
        if (n-- <= 0) break;
        l.add(new ListaArray<>());
        for (int i : ids) {
          if (this.directAddressing[i] == null) throw new ItemNoFound("No se encontró la canción con id: " + i);
          l.getLast().add(this.directAddressing[i]);
        }
      }
    } catch (Exception e){
      System.out.println(e);
    }
    return l;
  }
  
  public  List<List<Cancion>> getSortDescendingField(String param, int n) {
    List<List<Cancion>> l = new ListaArray<List<Cancion>>();
    try {
      MapBTree<? extends Comparable<?>, List<Integer>> inner = (MapBTree<? extends Comparable<?>, List<Integer>>) this.fields.get(param);
      if (inner == null) throw new NoSuchElementException("No se encontró el campo " + param );
      List< List<Integer>> ids = inner.sortValues();
      for (int e = ids.size()-1; e >= 0; e--) {
        System.out.println("Procesando elemento de " + param);
        if (n-- <= 0) break;
        l.add(new ListaArray<>());
        for (int i : ids.get(e)) {
          if (this.directAddressing[i] == null) throw new ItemNoFound("No se encontró la canción con id: " + i);
          l.getLast().add(this.directAddressing[i]);
        }
      }
    } catch (Exception e){
      System.out.println(e);
    }
    return l;
  }

  @SuppressWarnings("unchecked")
  public <T extends Comparable<T>> List<Integer> getIds(String param,T key) {
    System.out.println("Apend to id: "+param + " key: "+key);
    if (this.fields.isEmpty()) throw new NoSuchFieldError("El árbol de campos (this.field) está vacío");
    Map<T, List<Integer>> inner = (Map<T, List<Integer>>) this.fields.get(param);
    if (inner == null) throw new NoSuchElementException("No se encontró el campo " + param + " - " + key);
    return inner.get(key);
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<T>> List<Integer> createListID(String param, T key) {
    System.out.println("Se crará nueva lista id " + param + " key " + key);
    List<Integer> l = new ListaArray<Integer>();
    Map<T, List<Integer>> inner = (Map<T, List<Integer>>) this.fields.get(param);
    if (inner == null) 
      throw new NoSuchElementException("No se encontró el campo " + param );
    else{
      inner.set(key, l);
    }
    return l;
  }


  //private getTree(String param) {
  //  param = param.toUpperCase();
  //  if (param.equals("YEAR"))
  //  if (param.equals("popularity"))
  //  if (param.equals("duration_ms"))
  //  if (param.equals("popularity"))
  //}


}


class Cell<K, V> implements Map<K, V> {
  K key;
  V value;
  Cell(K key, V value) {
    this.key = key;
    this.value = value;
  }
  @Override
  public boolean isEmpty() {
    return this.key == null;
  }
  @Override
  public void set(K key, V value) {
    this.key = key;
    this.value = value;
  }
  @Override
  public V get(K key) {
    return this.value;
  }


}
