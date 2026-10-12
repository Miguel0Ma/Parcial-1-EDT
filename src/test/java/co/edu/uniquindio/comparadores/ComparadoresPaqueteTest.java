package co.edu.uniquindio.comparadores;

import co.edu.uniquindio.modelo.Paquete;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComparadoresPaqueteTest {

    private List<String> codigos(List<Paquete> paquetes) {
        List<String> salida = new ArrayList<>();
        for (Paquete p : paquetes) {
            salida.add(p.getCodigo());
        }
        return salida;
    }

    private List<Paquete> ejemplo() {
        List<Paquete> lista = new ArrayList<>();
        lista.add(new Paquete("PQ420", "Armenia", 6.0, 3, 30));
        lista.add(new Paquete("PQ731", "Armenia", 12.0, 5, 45));
        lista.add(new Paquete("PQ105", "Salento", 3.0, 5, 25));
        lista.add(new Paquete("PQ942", "Calarcá", 8.0, 4, 20));
        return lista;
    }

    @Test
    void ordenaPorPrioridadDeMayorAMenor() {
        List<Paquete> lista = ejemplo();
        lista.sort(ComparadoresPaquete.PRIORIDAD_DESC);
        assertEquals(5, lista.get(0).getPrioridad());
        assertEquals(5, lista.get(1).getPrioridad());
        assertEquals(4, lista.get(2).getPrioridad());
        assertEquals(3, lista.get(3).getPrioridad());
    }

    @Test
    void ordenaPorPesoDeMayorAMenor() {
        List<Paquete> lista = ejemplo();
        lista.sort(ComparadoresPaquete.PESO_DESC);
        assertEquals(List.of("PQ731", "PQ942", "PQ420", "PQ105"), codigos(lista));
    }

    @Test
    void ordenaPorTiempoEstimadoDeMenorAMayor() {
        List<Paquete> lista = ejemplo();
        lista.sort(ComparadoresPaquete.TIEMPO_ASC);
        assertEquals(List.of("PQ942", "PQ105", "PQ420", "PQ731"), codigos(lista));
    }

    @Test
    void despachoPrioritarioSigueElEjemploDelEnunciado() {
        List<Paquete> lista = ejemplo();
        lista.sort(ComparadoresPaquete.DESPACHO_PRIORITARIO);
        assertEquals(List.of("PQ105", "PQ731", "PQ942", "PQ420"), codigos(lista));
    }

    @Test
    void despachoPrioritarioDesempataPorTiempo() {
        Paquete rapido = new Paquete("PQ900", "Armenia", 1.0, 5, 10);
        Paquete lento = new Paquete("PQ100", "Armenia", 1.0, 5, 50);
        assertTrue(ComparadoresPaquete.DESPACHO_PRIORITARIO.compare(rapido, lento) < 0);
        assertTrue(ComparadoresPaquete.DESPACHO_PRIORITARIO.compare(lento, rapido) > 0);
    }

    @Test
    void despachoPrioritarioDesempataPorCodigoAlfabetico() {
        Paquete a = new Paquete("PQ100", "Armenia", 1.0, 5, 30);
        Paquete b = new Paquete("PQ200", "Armenia", 9.0, 5, 30);
        assertTrue(ComparadoresPaquete.DESPACHO_PRIORITARIO.compare(a, b) < 0);
        assertTrue(ComparadoresPaquete.DESPACHO_PRIORITARIO.compare(b, a) > 0);
    }

    @Test
    void despachoPrioritarioDevuelveCeroParaElMismoPaquete() {
        Paquete a = new Paquete("PQ100", "Armenia", 1.0, 5, 30);
        assertEquals(0, ComparadoresPaquete.DESPACHO_PRIORITARIO.compare(a, a));
    }

    @Test
    void ordenarConUnComparatorNoCambiaElOrdenNatural() {
        List<Paquete> lista = ejemplo();
        lista.sort(ComparadoresPaquete.PESO_DESC);
        Collections.sort(lista); // orden natural: por código
        assertEquals(List.of("PQ105", "PQ420", "PQ731", "PQ942"), codigos(lista));
    }

    @Test
    void comparadoresSonReutilizablesYEstables() {
        Paquete a = new Paquete("PQ1", "Armenia", 5.0, 4, 10);
        Paquete b = new Paquete("PQ2", "Armenia", 5.0, 4, 10);
        assertEquals(0, ComparadoresPaquete.PRIORIDAD_DESC.compare(a, b));
        assertEquals(0, ComparadoresPaquete.PESO_DESC.compare(a, b));
        assertEquals(0, ComparadoresPaquete.TIEMPO_ASC.compare(a, b));
    }
}
