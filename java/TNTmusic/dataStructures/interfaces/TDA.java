package TNTmusic.dataStructures.interfaces;

import TNTmusic.exceptions.ItemNoFound;

public interface TDA<E> extends Iterable<E>{
  boolean isEmpty();
  void add(E e);
  E search(E e) throws ItemNoFound;
  void remove(E e) throws ItemNoFound;
  int size();
} 