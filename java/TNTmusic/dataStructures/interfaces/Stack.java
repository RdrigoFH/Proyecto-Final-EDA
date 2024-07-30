package TNTmusic.dataStructures.interfaces;

public interface Stack<T> extends TDA<T> {
  void push(T x);
  T pop();
  T top();
}
