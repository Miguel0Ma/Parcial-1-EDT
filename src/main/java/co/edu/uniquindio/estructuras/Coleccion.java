package co.edu.uniquindio.estructuras;

/**
 * Contrato común de las estructuras internas de QuindíoExpress.
 * Define QUÉ operaciones ofrece una colección; cada implementación
 * decide CÓMO se realizan internamente.
 *
 * @param <T> tipo de los elementos almacenados
 */
public interface Coleccion<T> {

    /** Agrega un elemento al final de la colección. */
    void agregar(T elemento);

    /**
     * Elimina la primera aparición del elemento.
     *
     * @return true si el elemento existía y fue eliminado; false si no estaba
     */
    boolean eliminar(T elemento);

    /**
     * Obtiene el elemento que se encuentra en una posición (base 0).
     *
     * @throws IndexOutOfBoundsException si la posición no es válida
     */
    T obtener(int posicion);

    /** Cantidad de elementos almacenados. */
    int tamano();

    /** Indica si la colección no tiene elementos. */
    boolean estaVacia();
}
