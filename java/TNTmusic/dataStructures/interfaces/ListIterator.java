package TNTmusic.dataStructures.interfaces;

import java.util.Iterator;

public interface ListIterator<E> extends Iterator<E>{
  boolean hasPrevious();
  E previous();
  E moveTo(E e);
}
