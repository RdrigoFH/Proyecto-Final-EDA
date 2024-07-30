package TNTmusic.dataStructures.list;
import java.lang.reflect.Array;
import java.util.Iterator;

import TNTmusic.dataStructures.interfaces.List;
import TNTmusic.dataStructures.interfaces.ListIterator;
import TNTmusic.dataStructures.interfaces.TDA;
import TNTmusic.exceptions.ItemNoFound;
import TNTmusic.exceptions.OutBoundIndex;
import TNTmusic.models.helpers.Comparador;

public class ListaArray <E> implements List<E> {
    private E array[];
    private int size;

    public ListaArray() {
      this(10);
    }

    public ListaArray(TDA<E> tda) {
        this(tda.size() + 10);
        for(E x : tda){
          this.add(x);
      }
    }

    @SuppressWarnings("unchecked")
    public ListaArray(int len){
        this.array = (E[]) new Object[len];
        this.size = 0;
    }

    @Override
    public void add(E e) {
        addLast(e);
    }

    @Override
    public void add(int index, E e) throws OutBoundIndex{
        if(index > size){
            throw new OutBoundIndex();
        }
        if(size >= array.length){
            growUpArray();
        }
        for(int i = index; i <= size; i++){
            array[i + 1] = array[i];
        }
        size++;
        array[index] = e;

    }

    @Override
    public void addFirst(E e) {
        if (size >= array.length){
            growUpArray();
        }
        for(int i = size; i > 0 ; i--)
            array[i] = array[i-1];
        array[0] = e;
        size++;
    }

    @Override
    public void addLast(E e) {
        if (size >= array.length){
            growUpArray();
        }
        array[size++] = e;
    }

    // Verifica que el indice exista y ademas no sea nulo
    @Override
    public E get(int index) throws OutBoundIndex {
        if(index< 0 || index > size - 1)
            throw new OutBoundIndex("El elemento en el indice" + index + "no existe");
        return array[index];
    }

    @Override
    public E getFirst() throws ItemNoFound{
        if(size <= 0)
            throw new ItemNoFound("El elemento en el indice" + 0 + "no existe");
        return array[0];
    }

    @Override
    public E getLast() throws ItemNoFound{
        if(size <= 0){
            throw new ItemNoFound("El elemento en el indice" + (size-1) + "no existe");
        }
        return array[size-1];
    }

    @Override
    public E remove(int index) throws OutBoundIndex{
        if(index < 0 || index > size -1)
            throw new  OutBoundIndex();
        // retroceder todos lo elementos que esten delante de index
        E finded = array[index];
        for(int i = index; i < size -1 ; i++)
            array[i] = array[i+1];
        --size;
        return finded;
    }

    // Elimina la primera ocurrencia del elemento
    @Override
    public void remove(E e) throws ItemNoFound {
        for(int i = 0 ; i < size -1; i++)
            if(array[i].equals(e)){
                try {
                    remove(i);
                } catch (OutBoundIndex ex) {
                    ex.printStackTrace();
                }
                
                return;
            }
        throw new ItemNoFound("Elemento no encontrado");
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public E search(E e) throws ItemNoFound {
        for(int i = 0; i < size-1; i++){
            if(array[i].equals(e)){
                return array[i];
            }
        }
        throw new ItemNoFound("Elemento no encontrado");
    }


    @Override
    public void changePos(int a, int b) throws OutBoundIndex{
        if(a < 0 || a >= size || b < 0 || b >= size) throw new OutBoundIndex();
        swap(a, b);
    }

    @SuppressWarnings("unchecked")
    private void growUpArray(){
        E[] newArray = (E[]) new Object[array.length+25];
        for(int i = 0; i < array.length ; i++){
            newArray[i] = array[i];
        }
        this.array = newArray;
    }

    @Override
    public void sort(Comparador<E> c) {
        quicksort(0, size - 1, c);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray(Class<E> clazz) {
        System.out.println("Size del array: " + size);
        E[] arr = (E[]) Array.newInstance(clazz, size);
        int i = 0;
        for(E e : this){
            arr[i++] = e;
        }
        return arr;
    }

    @Override
    public Iterator<E> iterator() {
        return new ListaArrayIterator();
    }

    @Override
    public ListIterator<E> lIterator(boolean fromLast) {
        return new ListaArrayIterator(fromLast);
    }

    @Override
    public String toString() {
        if(isEmpty()) return "[]";
        String res = "[";
        for(int i = 0; i < size - 1; i++)
            res += array[i] + ", ";
        res += array[size - 1] + "]";
        return res;
    }

    private void quicksort(int l, int r, Comparador<E> c) {
        if(l < r) {
            int p = partition(l, r, c);
            quicksort(l, p - 1, c);
            quicksort(p + 1, r, c);
        }
    }

    private int partition(int l, int r, Comparador<E> c) {
        E p = array[r];
        int i = l - 1;
        for(int j = l; j <= r - 1; j++){
            if(c.compare(array[j], p) <= 0){
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, r);
        return i + 1;
    }

    private void swap(int i, int j) {
        E temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private class ListaArrayIterator implements ListIterator<E>{
        private int current;

        public ListaArrayIterator() {
            this(false);
        }

        public ListaArrayIterator(boolean last) {
            if(last) {
                this.current = size;
            }else{
                this.current = -1;
            }
        }

        @Override
        public boolean hasNext() {
            return current + 1 < size;
        }

        @Override
        public E next() {
            return array[++current];
        }

        @Override
        public boolean hasPrevious() {
            return current - 1 > 0;
        }

        @Override
        public E previous() {
            return array[--current];
        }

        @Override
        public E moveTo(E e) {
            if(isEmpty()) return null;
            int i = -1;
            while(++i < size){
                if(array[i].equals(e))
                    break;
            }
            this.current = i;
            return array[i];
        }

    }
}
