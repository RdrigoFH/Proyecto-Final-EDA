package TNTmusic.exceptions;

public class OutBoundIndex extends Exception {

  public OutBoundIndex(String msg) {
    super(msg);
  }

  public OutBoundIndex() {
    super("Fuera de los límites en esta TDA");
  }

}
