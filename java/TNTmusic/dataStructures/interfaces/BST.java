package TNTmusic.dataStructures.interfaces;

import TNTmusic.exceptions.ItemNoFound;

public interface BST<E> extends TDA<E>{
  List<E> inOrder();
  List<E> inOrderReverse();
  E minElement();
  E maxElement();
  E search(E key) throws ItemNoFound;
}
