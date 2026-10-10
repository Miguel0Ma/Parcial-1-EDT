package co.edu.uniquindio.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista simplemente enlazada propia y genérica.
 *
 * <p>No usa ninguna colección del Java Collections Framework para almacenar
 * sus elementos: solo nodos conectados por una referencia al siguiente.</p>
 *
 * <p>Se guarda también una referencia al último nodo ({@code cola}) para que
 * {@link #agregar(Object)} cueste O(1) y no O(n).</p>
 *
 * <p>Costos: agregar O(1); eliminar O(n); obtener O(n); tamano y estaVacia O(1);
 * recorrer con el iterador O(n) en total (O(1) por paso).</p>
 *
 * @param <T> tipo de los elementos (no está atada a Paquete)
 */
public class ListaEnlazada<T> implements Coleccion<T>, Iterable<T> {

    /** Nodo interno: valor y referencia al siguiente. Nunca sale de la clase. */
    private static class Nodo<E> {
        private final E valor;
        private Nodo<E> siguiente;

        private Nodo(E valor) {
            this.valor = valor;
        }
    }

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamano;

    /** Agrega al final. Los elementos nulos no se admiten. */
    @Override
    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("La lista no admite elementos nulos");
        }
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (cabeza == null) {          // lista vacía: el nuevo es primero y último
            cabeza = nuevo;
            cola = nuevo;
        } else {                        // enlazar después del último
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamano++;
    }

    /**
     * Elimina la primera aparición del elemento (comparación con equals).
     * Cubre: lista vacía, único elemento, primero, intermedio, último e inexistente.
     */
    @Override
    public boolean eliminar(T elemento) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;

        while (actual != null && !actual.valor.equals(elemento)) {
            anterior = actual;
            actual = actual.siguiente;
        }

        if (actual == null) {           // vacía o elemento inexistente
            return false;
        }

        if (anterior == null) {         // era el primero (o el único)
            cabeza = actual.siguiente;
        } else {                        // intermedio o último
            anterior.siguiente = actual.siguiente;
        }

        if (actual == cola) {           // era el último: la cola retrocede
            cola = anterior;
        }

        tamano--;
        return true;
    }

    @Override
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tamano) {
            throw new IndexOutOfBoundsException(
                    "Posición " + posicion + " fuera de rango (tamaño " + tamano + ")");
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < posicion; i++) {
            actual = actual.siguiente;
        }
        return actual.valor;
    }

    @Override
    public int tamano() {
        return tamano;
    }

    @Override
    public boolean estaVacia() {
        return tamano == 0;
    }

    /** Cada llamada devuelve un iterador nuevo e independiente. */
    @Override
    public Iterator<T> iterator() {
        return new IteradorLista();
    }

    /**
     * Iterador: lleva su propia posición. El código externo recorre la lista
     * sin conocer los nodos.
     */
    private class IteradorLista implements Iterator<T> {
        private Nodo<T> actual = cabeza;

        @Override
        public boolean hasNext() {
            return actual != null;
        }

        @Override
        public T next() {
            if (actual == null) {
                throw new NoSuchElementException("No hay más elementos");
            }
            T valor = actual.valor;
            actual = actual.siguiente;
            return valor;
        }
    }

    /** Ejemplo: PQ105 → PQ942 → PQ731 */
    @Override
    public String toString() {
        if (cabeza == null) {
            return "(vacía)";
        }
        StringBuilder sb = new StringBuilder();
        for (T elemento : this) {
            if (sb.length() > 0) {
                sb.append(" → ");
            }
            sb.append(elemento);
        }
        return sb.toString();
    }
}
