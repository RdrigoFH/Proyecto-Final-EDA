package TNTmusic.models.helpers;

public abstract class Comparador<E> {

  protected int orden;

  protected Comparador() {
    this.orden = 1;
  }

  protected Comparador(boolean upward) {
    // Si es verdadero es ascendente
    this.orden  = (upward ? 1 : -1);
  }

  public abstract int compare(E x, E y);

  public void falling() {
    this.orden = -1;
  }

  public void upward() {
    this.orden = 1;
  }

  public boolean isUpward() {
    return this.orden == 1;
  }
}
