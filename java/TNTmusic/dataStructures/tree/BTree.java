package TNTmusic.dataStructures.tree;

import TNTmusic.dataStructures.interfaces.BST;
import TNTmusic.dataStructures.interfaces.List;
import TNTmusic.dataStructures.list.DoubleLinkedList;
import TNTmusic.exceptions.ItemNoFound;
import TNTmusic.exceptions.OperationNotSupported;

import java.io.Serializable;
import java.util.Iterator;

public class BTree<E extends Comparable<E>> implements BST<E>, Serializable {
  private int T;
  private Node<E> root;

  public BTree(int t) {
    this.T = t;
    root = new Node<>(t, true);
  }

  public void insert(E e){
    // Verificamos si esta lleno
    if (root.n == 2 * T - 1) {
      // dividir el nodo y actualizar las referencias
      Node<E> aux = new Node<>(T, false);
      aux.children[0] = root;
      splitChild(aux, 0, root);
      root = aux;
    }
    insertAux(root, e);
  }
  // Si el nodo no esta lleno
  public void insertAux(Node<E> node, E key ){
    int aux = node.n - 1;
    // si el nodo es una hoja y tiene capacidad (inseercion directa)
    if(node.leaf){
      while (aux >= 0 && node.keys[aux].compareTo(key) > 0) {
        node.keys[aux + 1] = node.keys[aux];
        aux--;
      }
      node.keys[aux + 1] = key;
      node.n += 1;
    }else {
      // encontramos la posicion para insertar
      while (aux >= 0 && node.keys[aux].compareTo(key) > 0) {
        aux--;
      }
      if (node.children[aux + 1].n == 2 * T - 1) {
        //significa que esta lleno y debemos dividir el nodo
        splitChild(node, aux + 1, node.children[aux + 1]);
        if (node.keys[aux + 1].compareTo(key) < 0) {
          aux++;
        }
      }
      //si hay espacio, llamamos a la funcion en ese nodo
      insertAux(node.children[aux + 1], key);
    }
  }

  //Este metodo recibe como argumento el padre del nodo a dividir
  // int i que es la posicion donde ira la mediana
  // full node que es el nodo a dividir
  private void splitChild(Node<E> parent, int i, Node<E> fullNode) {
    //Nodo auxiliar que almacenara las T-1 ultimas claves del nodo y
    Node<E> aux = new Node<>(fullNode.T, fullNode.leaf);
    aux.n = T - 1;
    // copeamos las T-1 claves de full node a aux
    for (int j = 0; j < T - 1; j++)
      aux.keys[j] = fullNode.keys[j + T];
    //si no es una hoja copeamos los elementos
    if (!fullNode.leaf)
      for (int j = 0; j < T; j++)
        aux.children[j] = fullNode.children[j + T];
    //reducimos el numero de keys
    fullNode.n = T - 1;
    // desplazamos los elementos a la derecha para la insercion
    for (int j = parent.n; j >= i + 1; j--)
      parent.children[j + 1] = parent.children[j];

    parent.children[i + 1] = aux;

    for (int j = parent.n - 1; j >= i; j--)
      parent.keys[j + 1] = parent.keys[j];

    parent.keys[i] = fullNode.keys[T - 1];
    parent.n += 1;
  }
  public void inorder() {
    inorder(root);
  }
  private void inorder(Node<E> node) {
    int i;
    for (i = 0; i < node.n; i++) {
      if (!node.leaf)
        inorder(node.children[i]);
      System.out.print(" " + node.keys[i]);
    }

    if (!node.leaf)
      inorder(node.children[i]);

  }

  public void preorder() {
    preorder(root);
  }

  private void preorder(Node node) {
    if (node == null)  return;

    for (int i = 0; i < node.n; i++)
      System.out.print(" " + node.keys[i]);

    for (int i = 0; i <= node.n; i++)
      if (!node.leaf)
        preorder(node.children[i]);
  }
  public void postorder() {
    postorder(root);
  }


  private void postorder(Node node) {
    if (node == null) return;

    for (int i = 0; i <= node.n; i++)
      if (!node.leaf)
        postorder(node.children[i]);

    for (int i = 0; i < node.n; i++)
      System.out.print(" " + node.keys[i]);

  }
  public E search(E key) {
    return search(root, key);
  }
  private E search(Node<E> node, E k) {
    int i = 0;
    while (i < node.n && k.compareTo(node.keys[i]) > 0)
      i++;

    if (i < node.n && node.keys[i].compareTo(k) == 0)  return node.keys[i];
    if (node.leaf)  return null;

    return search(node.children[i], k);
  }
  public static void main(String[] args) {
    BTree<Integer> t = new BTree<>(2);
    t.insert(50);
    t.insert(49);
    t.insert(7);
    t.insert(14);
    t.insert(2);
    t.insert(1);
    t.insert(10);
    t.insert(5);
    t.insert(21);
    t.insert(16);
    t.insert(15);

    System.out.println(t.search(7));
    t.inorder();
    t.preorder();
    t.postorder();
  }

  @Override
  public void add(E e) {
    insert(e);
  }
  @Override
  public E minElement() {
    return null;
  }

  @Override
  public E maxElement() {
    return null;
  }

  @Override
  public boolean isEmpty() {return root == null;}



  @Override
  public Iterator<E> iterator() {throw new OperationNotSupported();}
  @Override
  public List<E> inOrder() {
    return inOrder(this.root);
  }

  protected DoubleLinkedList<E> inOrder(Node<E> node) {
    DoubleLinkedList<E> l = new DoubleLinkedList<>();
    int i;
    for (i = 0; i < node.n; i++) {
      if (!node.leaf)
        l.appendList(inOrder( node.children[i] ));
      l.addLast(node.keys[i]);
    }
    if (!node.leaf) l.appendList(inOrder(node.children[i]));
    return l;
  }

  @Override
  public List<E> inOrderReverse() {throw new OperationNotSupported();}
  @Override
  public void remove(E e) throws ItemNoFound {throw new OperationNotSupported();  }
  @Override
  public int size() {throw new OperationNotSupported();}
}
