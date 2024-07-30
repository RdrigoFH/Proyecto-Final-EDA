package TNTmusic.dataStructures.list;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Arrays;


import TNTmusic.dataStructures.interfaces.List;
import TNTmusic.dataStructures.interfaces.ListIterator;
import TNTmusic.dataStructures.interfaces.TDA;
import TNTmusic.exceptions.ItemNoFound;
import TNTmusic.exceptions.OperationNotSupported;
import TNTmusic.exceptions.OutBoundIndex;
import TNTmusic.models.helpers.Comparador;

public class OrderedList <E extends Comparable<E>> implements List<E> {
    private E array[];
    private int size;

    public OrderedList() {this(10);}

    @SuppressWarnings("unchecked")
    public OrderedList(int len){
        this.array = (E[]) new Object[len];
        this.size = 0;
    }
    @Override
    public void add(E e) {
        int index = findLocation(e, 0 , size -1);
        // elemento ya existente
        if (index < 0) return;
        E[] arrayNew = createArray();
        System.arraycopy(this.array, 0, arrayNew, 0, index);
        arrayNew[index] = e;
        System.arraycopy(this.array, index, arrayNew, index + 1, size - index);
        this.array = arrayNew;
        size++;
    }

    private E[]  createArray(){
        if (size + 1 >= array.length - 1){
            return (E[]) new Object[array.length + 7];
        }
        return array;
    }

    private int findLocation(E e , int i , int j){
        int midIdx = (i + j) / 2;
        // si ya no existen elementos intermedios devolvemos el indice donde insertaremos
        if ( i > j) return i;

        if (e.compareTo(array[midIdx]) > 0){
            findLocation(e ,midIdx + 1, j);
        }else if ( e.compareTo(array[midIdx]) < 0){
            findLocation(e , i , midIdx - 1);
        }
        // el elemento ya existe
        return -1;
    }
    @Override
    public void addFirst(E e) {
        throw new OperationNotSupported("Insercion automatica, no puede agregar al inicio");
    }

    @Override
    public void addLast(E e) throws OperationNotSupported{
        throw new OperationNotSupported("Insercion automatica, no puede agregar al final");
    }

    @Override
    public void add(int index, E e) throws OperationNotSupported {
        throw new OperationNotSupported("Lista ordenada no acepta indices");
    }

    @Override
    public E get(int index) throws OutBoundIndex {
        if(index< 0 || index > size - 1)
            throw new OutBoundIndex("El elemento en el indice" + index + "no existe");
        return array[index];
    }

    @Override
    public E getFirst() throws ItemNoFound {
        if(size <= 0)
            throw new ItemNoFound("El elemento en el indice" + 0 + "no existe");
        return array[0];
    }

    @Override
    public E getLast() throws ItemNoFound {
        if(size <= 0){
            throw new ItemNoFound("El elemento en el indice" + (size-1) + "no existe");
        }
        return array[size-1];
    }

    @Override
    public E remove(int index) throws OutBoundIndex {
        return null;
    }

    @Override
    public void changePos(int a, int b) throws OperationNotSupported {
        throw new OperationNotSupported("No puede cambiar posiciones en una lista ordenda");
    }

    @Override
    public void sort(Comparador<E> c) throws OperationNotSupported{
        throw new OperationNotSupported("La lista ya se encuentra ordenada");
    }

    @Override
    public ListIterator<E> lIterator(boolean fromLast) {
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public E next() {
                if (!hasNext());
                    return array[currentIndex++];
            }
        };
    }
    @Override
    public E[] toArray(Class<E> clazz) {
        return Arrays.copyOf(array, size);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public E search(E e) throws ItemNoFound {
        int index = binarySearch(e, 0, size - 1);
        if (index >= 0) {
            return array[index];
        } else {
            throw new ItemNoFound("No se encontro el elemento" + e);
        }
    }
    private int binarySearch(E e, int i, int j) {
        if (i > j) return -1;
        int mid = (i + j) / 2;
        int cmp = e.compareTo(array[mid]);

        if (cmp < 0) {
            return binarySearch(e, i, mid - 1);
        } else if (cmp > 0) {
            return binarySearch(e, mid + 1, j);

        } else return mid;
    }
    @Override
    public void remove(E e) throws ItemNoFound {
        int index = findLocation((E) e, 0, size - 1);
        if (index < 0 || index >= size || array[index].compareTo((E) e) != 0)
            return;

        int aux = size - index - 1;
        if (aux > 0)
            System.arraycopy(array, index + 1, array, index, aux);
        array[--size] = null;
    }

    @Override
    public int size() {return size;}
}