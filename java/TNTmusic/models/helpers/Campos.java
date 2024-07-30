package TNTmusic.models.helpers;

public enum Campos {
  ANO("Año"),
  POPULARIDAD("Popularidad"),
  NOMBRE_CANCION("Nombre"),
  NOMBRE_ARTISTA("Artista");

  private final String displayName;

  Campos(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public String toString() {
    return this.getDisplayName();
  }
}
