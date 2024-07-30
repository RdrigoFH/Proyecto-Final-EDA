package TNTmusic.dataStructures.interfaces;

import java.io.Serializable;

import TNTmusic.exceptions.ItemNoFound;
import TNTmusic.exceptions.OutBoundIndex;
import TNTmusic.models.helpers.Comparador;

public interface List<E> extends TDA<E>, Serializable{
  void addFirst(E e);
  void addLast(E e);
  void add(int index, E e) throws OutBoundIndex;
  E get(int index) throws OutBoundIndex;
  E getFirst() throws ItemNoFound;
  E getLast() throws ItemNoFound;
  E remove(int index) throws OutBoundIndex;
  void changePos(int a, int b) throws OutBoundIndex;
  void sort(Comparador<E> c);
  ListIterator<E> lIterator(boolean fromLast);
  E[] toArray(Class<E> clazz);
}