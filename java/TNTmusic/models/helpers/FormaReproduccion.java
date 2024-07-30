package TNTmusic.models.helpers;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2RoundMZ;

public enum FormaReproduccion {
  ALEATORIO(new FontIcon(Material2RoundMZ.SHUFFLE)),
  BUCLE_UNO(new FontIcon(Material2RoundMZ.REPEAT_ONE)),
  PERSONALIZADO(new FontIcon(Material2RoundMZ.REPEAT)),
  ORDENADO(new FontIcon(Material2RoundMZ.SORT));

  private FontIcon icon;

  FormaReproduccion(FontIcon icon) {
    this.icon = icon;
  }

  public FontIcon getIcon() {
    return this.icon;
  }

  public String getDisplay() {
    return "Here";
  }
}
