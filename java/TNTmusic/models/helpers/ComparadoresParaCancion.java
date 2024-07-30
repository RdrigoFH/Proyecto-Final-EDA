package TNTmusic.models.helpers;

import TNTmusic.models.Cancion;

public class ComparadoresParaCancion {
  
  public static Comparador<Cancion> campo(Campos c){
    if (c == null) {
      return new ComparadorPrincipal();
    }
    switch (c) {
      case POPULARIDAD:
        return new ComparadorPorPopularidad();
      case ANO:
        return new ComparadorPorAno();
      case NOMBRE_ARTISTA:
        return new ComparadorPorNombreArtista();
      case NOMBRE_CANCION:
        return new ComparadorPorNombreCancion();
      default:
        return new ComparadorPrincipal();
    }
  }

  private static class ComparadorPorPopularidad extends Comparador<Cancion> {

    public int compare(Cancion x, Cancion y) {
      if(x.getPopularity() > y.getPopularity()) return this.orden;
      if(x.getPopularity() < y.getPopularity()) return -1 * this.orden;
      return 0;
    }

  }

  private static class ComparadorPorAno extends Comparador<Cancion> {

    public int compare(Cancion x, Cancion y) {
      if(x.getYear() > y.getYear()) return this.orden;
      if(x.getYear() < y.getYear()) return -1 * this.orden;
      return 0;
    }

  }

  private static class ComparadorPorNombreCancion extends Comparador<Cancion> {

    public int compare(Cancion x, Cancion y) {
      return this.orden * x.getTrack_name().compareTo(y.getTrack_name());
    }

  }

  private static class ComparadorPorNombreArtista extends Comparador<Cancion> {

    public int compare(Cancion x, Cancion y) {
      return this.orden * x.getArtist_name().compareTo(y.getArtist_name());
    }



  }
  private static class ComparadorPrincipal extends Comparador<Cancion> {

    public int compare(Cancion x, Cancion y) {
      if(x.getId() > y.getId()) return this.orden;
      if(x.getId() < y.getId()) return -1 * this.orden;
      return 0;
    }

  }
}
