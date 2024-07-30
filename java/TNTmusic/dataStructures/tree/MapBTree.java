package TNTmusic.dataStructures.tree;

import java.io.Serializable;

import TNTmusic.dataStructures.interfaces.List;
import TNTmusic.dataStructures.interfaces.Map;
import TNTmusic.dataStructures.list.DoubleLinkedList;

public class MapBTree<K extends Comparable<K>, V> implements Map<K, V>, Serializable {
  private BTree<Entry> tree;
  protected class Entry implements Comparable<Entry>, Serializable{
    K key;
    V value;
    public Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }
    @Override
    public int compareTo(MapBTree<K, V>.Entry o) {
      return this.key.compareTo(o.key);
    }

  }

  public MapBTree() {
    this.tree = new BTree<>(3);
  }

  public List<V> sortValues() {
    DoubleLinkedList<V> l = new DoubleLinkedList<>();
    for (Entry e : this.tree.inOrder())
      l.add(e.value);
    return l;
  }

  @Override
  public boolean isEmpty() {
    return this.tree.isEmpty();
  }

  //se inserta uno nuevo, o se chanca el valor del anterior
  @Override
  public void set(K key, V value) {
    Entry s = this.tree.search(new Entry(key, null));
    if (s == null) {
      this.tree.insert(new Entry(key, value));
    } else {
      s.value = value;
    }
  }
  @Override
  public V get(K key) {
    Entry e = this.tree.search(new Entry(key, null));
    return (e == null)? null:e.value;
  }


}

