package co.edu.uniquindio.algoritmos;

import co.edu.uniquindio.modelo.Paquete;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DBusquedaBinariaTest {

    private Paquete paquete(String codigo) {
        return new Paquete(codigo, "Armenia", 1.0, 3, 10);
    }

    private List<Paquete> ordenada(String... codigos) {
        List<Paquete> lista = new ArrayList<>();
        for (String c : codigos) {
            lista.add(paquete(c));
        }
        return lista;
    }

    private List<Paquete> ejemplo() {
        return ordenada("PQ105", "PQ214", "PQ318", "PQ421", "PQ567", "PQ731", "PQ842", "PQ942");
    }

    @Test
    void encuentraElPaqueteDelEnunciado() {
        assertEquals(5, BusquedaBinaria.buscarPorCodigo(ejemplo(), "PQ731"));
    }

    @Test
    void encuentraElPrimero() {
        assertEquals(0, BusquedaBinaria.buscarPorCodigo(ejemplo(), "PQ105"));
    }

    @Test
    void encuentraElUltimo() {
        assertEquals(7, BusquedaBinaria.buscarPorCodigo(ejemplo(), "PQ942"));
    }

    @Test
    void encuentraTodosLosElementos() {
        List<Paquete> lista = ejemplo();
        for (int i = 0; i < lista.size(); i++) {
            assertEquals(i, BusquedaBinaria.buscarPorCodigo(lista, lista.get(i).getCodigo()));
        }
    }

    @Test
    void devuelveMenosUnoSiNoExiste() {
        assertEquals(-1, BusquedaBinaria.buscarPorCodigo(ejemplo(), "PQ500"));
    }

    @Test
    void devuelveMenosUnoSiEsMenorQueTodos() {
        assertEquals(-1, BusquedaBinaria.buscarPorCodigo(ejemplo(), "PQ001"));
    }

    @Test
    void devuelveMenosUnoSiEsMayorQueTodos() {
        assertEquals(-1, BusquedaBinaria.buscarPorCodigo(ejemplo(), "PQ999"));
    }

    @Test
    void listaVaciaDevuelveMenosUno() {
        assertEquals(-1, BusquedaBinaria.buscarPorCodigo(new ArrayList<>(), "PQ105"));
    }

    @Test
    void listaDeUnElemento() {
        List<Paquete> lista = ordenada("PQ105");
        assertEquals(0, BusquedaBinaria.buscarPorCodigo(lista, "PQ105"));
        assertEquals(-1, BusquedaBinaria.buscarPorCodigo(lista, "PQ200"));
    }

    @Test
    void listaDeDosElementos() {
        List<Paquete> lista = ordenada("PQ105", "PQ200");
        assertEquals(0, BusquedaBinaria.buscarPorCodigo(lista, "PQ105"));
        assertEquals(1, BusquedaBinaria.buscarPorCodigo(lista, "PQ200"));
    }
}
