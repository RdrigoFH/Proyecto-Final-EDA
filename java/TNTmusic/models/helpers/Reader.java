package TNTmusic.models.helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Reader<T> {

  private ObjectInputStream reader;
  private boolean exists;

  public Reader(String path) {
    try {
      this.reader = new ObjectInputStream(new FileInputStream(path));
      this.exists = true;
    } catch (IOException e) {
      this.reader = null;
      this.exists = false;
      System.out.println("No se pudo inicializar el lector del archivo");
    }
  }

  @SuppressWarnings("unchecked")
  public T read() {
    if (!this.fileExists())
      return null;
    try {
      return (T) this.reader.readObject();
    } catch (Exception e) {
      System.out.println("Has llegado al final del archivo");
      return null;
    }
  }

  public void close() {
    if (this.fileExists()) {
      try {
        this.reader.close();
      } catch (IOException e) {
        System.out.println("Ocurrió un error al cerrar el lector");
      }
    }
  }

  public boolean fileExists() {
    return this.exists;
  }

}
