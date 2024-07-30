package TNTmusic.dataStructures.list;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Iterator;

import TNTmusic.dataStructures.interfaces.List;
import TNTmusic.dataStructures.interfaces.ListIterator;
import TNTmusic.dataStructures.interfaces.TDA;
import TNTmusic.exceptions.ItemNoFound;
import TNTmusic.exceptions.OutBoundIndex;
import TNTmusic.models.helpers.Comparador;

public class DoubleLinkedList<E> implements List<E> {

  private Node<E> first;
  private Node<E> last;
  private int size;

  public DoubleLinkedList() {
    this.first = this.last = null;
    this.size = 0;
  }

  public DoubleLinkedList(TDA<E> tda) {
    this();
    for (E e : tda) {
      this.add(e);
    }
  }

  public void appendList(DoubleLinkedList<E> l) {
    if (l == null || l.size() == 0) return;
    if (this.isEmpty()) {
      this.last = l.last;
      this.first = l.first;
      this.size = l.size;
    } else {
      this.last.join(l.first);
      this.last = l.last;
      this.size += l.size;
    }
  }

  @Override
  public void add(E e) {
    addLast(e);
  }

  @Override
  public void add(int index, E e) throws OutBoundIndex {
    if (index > size)
      throw new OutBoundIndex();
    if (index == 0) {
      addFirst(e);
      return;
    } else if (index == size) {
      addLast(e);
      return;
    }
    Node<E> current = getNode(index);
    Node<E> newNode = new Node<E>(current.prev, e, current);
    current.prev.next = newNode;
    current.prev = newNode;
    ++size;
  }

  @Override
  public void addFirst(E e) {
    if (isEmpty()) {
      this.first = new Node<E>(e);
      this.last = first;
    } else {
      this.first = new Node<E>(null, e, this.first);
      this.first.next.prev = this.first;
    }
    this.size++;
  }

  @Override
  public void addLast(E e) {
    if (isEmpty())
      addFirst(e);
    else {
      this.last = new Node<E>(this.last, e, null);
      this.last.prev.next = this.last;
      this.size++;
    }
  }

  @Override
  public E get(int index) throws OutBoundIndex {
    return this.getNode(index).data;
  }

  @Override
  public E getFirst() throws ItemNoFound {
    return this.first.data;
  }

  @Override
  public E getLast() throws ItemNoFound {
    return this.last.data;
  }

  @Override
  public E remove(int index) throws OutBoundIndex {
    Node<E> current = getNode(index);
    removeNode(current);
    return current.data;
  }

  @Override
  public void remove(E x) throws ItemNoFound {
    Node<E> current = searchNode(x);
    removeNode(current);
  }

  @Override
  public int size() {
    return this.size;
  }

  @Override
  public boolean isEmpty() {
    return this.size == 0;
  }

  @Override
  public E search(E x) throws ItemNoFound {
    Node<E> i = searchNode(x);
    return i.data;
  }

  @Override
  public void sort(Comparador<E> c) {
    // De momento, no es necesario un ordenamiento ya que los ordeanmientos se harán en ListArray
  }

  @Override
  public void changePos(int a, int b) throws OutBoundIndex {
    if(a < 0 || a >= size || b < 0 || b >= size) throw new OutBoundIndex();
    if(a == b) return;
    E item = remove(a);
    add(b, item);
  }

  @Override
  @SuppressWarnings("unchecked")
  public E[] toArray(Class<E> clazz) {
    E[] arr = (E[]) Array.newInstance(clazz, size);
    int i = 0;
    for(E e : this)
      arr[i++] = e;
    return arr;
  }
 
  @Override
  public Iterator<E> iterator() {
    return new DoubleLinkedListIterator();
  }

  @Override
  public ListIterator<E> lIterator(boolean fromLast) {
    return new DoubleLinkedListIterator(fromLast);
  }
  
  @Override
  public String toString() {
    if (isEmpty())
      return "[]";
    String str = "[";
    for (E i : this)
      str += i + ", ";
    return str.substring(0, str.length() - 2) + "]";
  }

  private Node<E> getNode(int index) throws OutBoundIndex {
    if (index >= size || index < 0)
      throw new OutBoundIndex();
    Node<E> current;
    if (index < size / 2) {
      current = this.first;
      int i = -1;
      while (++i < index)
        current = current.next;
    } else {
      current = this.last;
      int i = size;
      while (--i > index)
        current = current.prev;
    }
    return current;
  }

  private Node<E> searchNode(E x) throws ItemNoFound {
    Node<E> i = this.first;
    while (i != null && !i.data.equals(x))
      i = i.next;
    if (i == null)
      throw new ItemNoFound();
    return i;
  }

  private void removeNode(Node<E> current) {
    if (this.size == 1)
      this.last = this.first = null;
    else if (current == this.first) {
      this.first = this.first.next;
      this.first.prev = null;
    } else if (current == this.last) {
      this.last = this.last.prev;
      this.last.next = null;
    } else {
      current.prev.join(current.next);
    }
    this.size--;
  }

  private static class Node<E> implements Serializable {
    Node<E> prev;
    Node<E> next;
    E data;

    public Node(Node<E> prev, E data, Node<E> next) {
      this.prev = prev;
      this.next = next;
      this.data = data;
    }

    public Node(E data) {
      this(null, data, null);
    }

    protected void join(Node<E> n) {
      this.next = n;
      if (n != null)
        n.prev = this;
    }
  }

  private class DoubleLinkedListIterator implements ListIterator<E> {
    private Node<E> current;

    public DoubleLinkedListIterator() {
      this(false);
    }

    public DoubleLinkedListIterator(boolean fromLast) {
      if(fromLast) {
        this.current = new Node<E>(last, null, null);
      }else{
        this.current = new Node<E>(null, null, first);
      }
      
    }

    @Override
    public boolean hasNext() {
      return this.current.next != null;
    }

    @Override
    public E next() {
      E data = this.current.next.data;
      this.current = this.current.next;
      return data;
    }

    @Override
    public boolean hasPrevious() {
      return this.current.prev != null;
    }

    @Override
    public E previous() {
      E data = this.current.prev.data;
      this.current = this.current.prev;
      return data;
    }

    @Override
    public E moveTo(E e) {
      try {
        this.current = searchNode(e);
        return this.current.data;
      } catch (ItemNoFound ex) {
        return null;
      }
      

    }

  }

}
