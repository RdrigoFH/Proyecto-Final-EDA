package TNTmusic.models.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Writer<T> {

  private ObjectOutputStream writer;
  private T object;

  public Writer(T object, String path) {
    this(path);
    this.object = object;
  }

  public Writer(String path) {
    try { 
      File f = new File(path);
      if(!f.exists()){
        System.out.println("El archivo en la dirección: \"" + f.getAbsolutePath() + "\" no existe. Se crearán los direcotorios necesarios de acuerdo a la dirección proporcionada");
        f.getParentFile().mkdirs();
        f.createNewFile();
    }
      this.writer = new ObjectOutputStream(new FileOutputStream(path));  
    } catch (IOException e) {
      System.out.println("Ocurrió un error al crear el escritor del archivo");
      System.out.println(e);
    }
  }

  public void setObject(T object) {
    this.object = object;
  }

  public void write(){
    try {
      this.writer.writeObject(this.object);
    } catch (IOException e) {
      System.out.println("Ocurrió un error al escribir el objecto");
      System.out.println(e);
    }
  }

  public void close() {
    try {
      this.writer.close();
    } catch (IOException e) {
      System.out.println("Ocurrió un error cerrrando escritor");
    }
  }

}
