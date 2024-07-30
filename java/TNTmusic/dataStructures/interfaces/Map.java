package TNTmusic.dataStructures.interfaces;

public interface Map<K, V> {
  boolean isEmpty();
  void set(K key, V value);
  V get(K key);
}
