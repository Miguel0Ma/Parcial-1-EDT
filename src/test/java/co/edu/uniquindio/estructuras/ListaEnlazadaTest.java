package co.edu.uniquindio.estructuras;

import co.edu.uniquindio.modelo.Paquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ListaEnlazadaTest {

    private ListaEnlazada<String> lista;

    @BeforeEach
    void preparar() {
        lista = new ListaEnlazada<>();
    }

    private void llenar(String... valores) {
        for (String v : valores) {
            lista.agregar(v);
        }
    }

    private List<String> comoJavaList() {
        List<String> salida = new ArrayList<>();
        for (String v : lista) {
            salida.add(v);
        }
        return salida;
    }

    // ---------- lista vacía ----------

    @Test
    void listaNuevaEstaVacia() {
        assertTrue(lista.estaVacia());
        assertEquals(0, lista.tamano());
        assertEquals("(vacía)", lista.toString());
    }

    @Test
    void obtenerEnListaVaciaLanzaExcepcion() {
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(0));
    }

    @Test
    void eliminarEnListaVaciaDevuelveFalse() {
        assertFalse(lista.eliminar("A"));
        assertEquals(0, lista.tamano());
    }

    @Test
    void iteradorDeListaVaciaNoTieneElementos() {
        Iterator<String> it = lista.iterator();
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    // ---------- agregar / obtener ----------

    @Test
    void agregarConservaElOrdenDeInsercion() {
        llenar("A", "B", "C");
        assertEquals(3, lista.tamano());
        assertFalse(lista.estaVacia());
        assertEquals("A", lista.obtener(0));
        assertEquals("B", lista.obtener(1));
        assertEquals("C", lista.obtener(2));
    }

    @Test
    void agregarNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> lista.agregar(null));
        assertEquals(0, lista.tamano());
    }

    @Test
    void obtenerConPosicionInvalidaLanzaExcepcion() {
        llenar("A", "B");
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(2));
    }

    // ---------- un único elemento ----------

    @Test
    void listaConUnSoloElemento() {
        llenar("A");
        assertEquals(1, lista.tamano());
        assertEquals("A", lista.obtener(0));
        assertEquals("A", lista.toString());
    }

    @Test
    void eliminarElUnicoElementoDejaLaListaVacia() {
        llenar("A");
        assertTrue(lista.eliminar("A"));
        assertTrue(lista.estaVacia());
        assertEquals(0, lista.tamano());
    }

    @Test
    void despuesDeVaciarSePuedeVolverAAgregar() {
        llenar("A");
        lista.eliminar("A");
        lista.agregar("B");
        lista.agregar("C");
        assertEquals(List.of("B", "C"), comoJavaList());
    }

    // ---------- eliminación ----------

    @Test
    void eliminarElPrimerElemento() {
        llenar("A", "B", "C");
        assertTrue(lista.eliminar("A"));
        assertEquals(2, lista.tamano());
        assertEquals(List.of("B", "C"), comoJavaList());
    }

    @Test
    void eliminarUnElementoIntermedio() {
        llenar("A", "B", "C", "D");
        assertTrue(lista.eliminar("B"));
        assertEquals(3, lista.tamano());
        assertEquals(List.of("A", "C", "D"), comoJavaList());
    }

    @Test
    void eliminarElUltimoElemento() {
        llenar("A", "B", "C");
        assertTrue(lista.eliminar("C"));
        assertEquals(2, lista.tamano());
        assertEquals(List.of("A", "B"), comoJavaList());
    }

    @Test
    void agregarDespuesDeEliminarElUltimoEnlazaBien() {
        llenar("A", "B", "C");
        lista.eliminar("C");
        lista.agregar("D");
        assertEquals(List.of("A", "B", "D"), comoJavaList());
        assertEquals("D", lista.obtener(2));
    }

    @Test
    void eliminarElementoInexistenteDevuelveFalseYNoCambiaLaLista() {
        llenar("A", "B", "C");
        assertFalse(lista.eliminar("Z"));
        assertEquals(3, lista.tamano());
        assertEquals(List.of("A", "B", "C"), comoJavaList());
    }

    @Test
    void eliminarSoloLaPrimeraAparicion() {
        llenar("A", "B", "A");
        assertTrue(lista.eliminar("A"));
        assertEquals(List.of("B", "A"), comoJavaList());
    }

    @Test
    void eliminarTodosUnoAUno() {
        llenar("A", "B", "C");
        assertTrue(lista.eliminar("B"));
        assertTrue(lista.eliminar("A"));
        assertTrue(lista.eliminar("C"));
        assertTrue(lista.estaVacia());
    }

    // ---------- Iterable / Iterator ----------

    @Test
    void iteradorRecorreEnOrden() {
        llenar("A", "B", "C");
        Iterator<String> it = lista.iterator();
        assertTrue(it.hasNext());
        assertEquals("A", it.next());
        assertEquals("B", it.next());
        assertEquals("C", it.next());
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void cadaLlamadaDevuelveUnIteradorIndependiente() {
        llenar("A", "B", "C");
        Iterator<String> it1 = lista.iterator();
        Iterator<String> it2 = lista.iterator();
        assertEquals("A", it1.next());
        assertEquals("B", it1.next());
        assertEquals("A", it2.next());
    }

    @Test
    void funcionaConForEach() {
        llenar("A", "B", "C");
        assertEquals(List.of("A", "B", "C"), comoJavaList());
    }

    // ---------- genericidad y uso como historial ----------

    @Test
    void esGenericaYFuncionaConEnteros() {
        ListaEnlazada<Integer> numeros = new ListaEnlazada<>();
        numeros.agregar(10);
        numeros.agregar(20);
        assertEquals(20, numeros.obtener(1).intValue());
        assertTrue(numeros.eliminar(10));
        assertEquals(1, numeros.tamano());
    }

    @Test
    void cumpleElContratoDeColeccion() {
        Coleccion<String> coleccion = new ListaEnlazada<>();
        coleccion.agregar("X");
        assertEquals(1, coleccion.tamano());
        assertEquals("X", coleccion.obtener(0));
        assertFalse(coleccion.estaVacia());
        assertTrue(coleccion.eliminar("X"));
        assertTrue(coleccion.estaVacia());
    }

    @Test
    void historialDeEntregasConservaElOrdenReal() {
        ListaEnlazada<Paquete> historial = new ListaEnlazada<>();
        historial.agregar(new Paquete("PQ105", "Salento", 3.0, 5, 25));
        historial.agregar(new Paquete("PQ942", "Armenia", 2.0, 4, 20));
        historial.agregar(new Paquete("PQ731", "Armenia", 5.0, 5, 45));

        List<String> codigos = new ArrayList<>();
        for (Paquete p : historial) {
            codigos.add(p.getCodigo());
        }
        assertEquals(List.of("PQ105", "PQ942", "PQ731"), codigos);
    }

    @Test
    void toStringUsaFlechas() {
        llenar("PQ105", "PQ942", "PQ731");
        assertEquals("PQ105 → PQ942 → PQ731", lista.toString());
    }
}
