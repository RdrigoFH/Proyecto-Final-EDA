package TNTmusic.dataStructures.list;

import java.util.Iterator;

import TNTmusic.dataStructures.interfaces.Stack;

public class StackLink<T> implements Stack<T> {
  private Node head;
  private Node tail;
  private int size;

  private class Node {
    Node prev;
    Node next;
    T value;
    public Node(Node prev, T value, Node next ) {
      this.prev = prev;
      if (prev != null) prev.next = this;
      this.value = value;
      this.next = next;
      if (next != null) next.prev = this;
    }
  }

  @Override
  public boolean isEmpty() {
    return this.size == 0;
  }
  
  /*
   * Llama al método push
   * */
  @Override
  public void add(T e) {
    this.push(e);
  }

  @Override
  public T search(T e) {
    for (T x : this ) 
      if (x.equals(e)) return x;
    return null;
  }

  @Override
  public void remove(T e)  {
    if (isEmpty()) {
      return;
    }
    if (this.head.value.equals(e)) {
      this.head = this.head.next;
      return;
    }
    Node current = this.head;
    while (current.next != null && !current.next.value.equals(e)) {
      current = current.next;
    }
    if (current.next != null) {
      current.next = current.next.next;
    }

  }

  @Override
  public int size() {
    return this.size;
  }

  @Override
  public void push(T x) {
    if (isEmpty()) {
      this.head = this.tail = new Node(null, x, null);
    } else {
      this.tail = new Node(this.tail, x, null);
    }
    this.size++;
  }

  @Override
  public T pop() {
    if (isEmpty()) throw new NullPointerException("Stack sin elementos");
    T x = this.tail.value;
    if (this.size-- == 1) {
      this.tail = this.head = null;
      return x;
    }
    this.tail = this.tail.prev;
    this.tail.next = null;
    return x;
  }

  @Override
  public T top() {
    if (isEmpty() ) throw new NullPointerException("Stack sin elementos");
    return this.tail.value;
  }

  @Override
  public Iterator<T> iterator() {
    return new StackIterator();
  }

  private class StackIterator implements Iterator<T> {
    private Node current;
    public StackIterator() {
      this.current = head;
    }

    @Override
    public boolean hasNext() {
      return this.current != null;
    }

    @Override
    public T next() {
      T x = this.current.value;
      this.current = this.current.next;
      return x;
    }
  }

  @Override
  public String toString() {
    if (isEmpty()) return "[]";
    String s = "[";
    for (T x : this) s += x + ", ";
    return s.substring(0, s.length()-2) + "]";
  }

}


