package TNTmusic.dataStructures.tree;

import java.io.Serializable;

public class Node<E extends Comparable<E>> implements Serializable{
    protected E[] keys;
    // T representa el grado minimo del tree
    protected int T;
    protected Node<E>[] children;
    // n representara el numero de keys que tiene actualmente un nodo
    protected int n;
    protected boolean leaf;
    //Constructor especializado para node de 2 keys y 3 childrens
    @SuppressWarnings("unchecked")
    public Node(int t, boolean leaf) {
        this.T = t;
        this.leaf = leaf;
        // Se calcula el numero de hijos y de claves que puede contener un nodo
        this.keys = (E[]) new Comparable[2 * t - 1];
        this.children = new Node[2 * t];
        this.n = 0;
    }
}
