package co.edu.uniquindio.algoritmos;

import co.edu.uniquindio.modelo.Paquete;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecursivosTest {

    private List<Paquete> pesosEnunciado() {
        List<Paquete> lista = new ArrayList<>();
        lista.add(new Paquete("PQ101", "Armenia", 4.5, 3, 10));
        lista.add(new Paquete("PQ203", "Salento", 3.0, 3, 10));
        lista.add(new Paquete("PQ415", "Armenia", 7.2, 3, 10));
        lista.add(new Paquete("PQ730", "Armenia", 2.3, 3, 10));
        return lista;
    }

    private List<Paquete> conPrioridades(int... prioridades) {
        List<Paquete> lista = new ArrayList<>();
        int n = 1;
        for (int prioridad : prioridades) {
            lista.add(new Paquete("PQ" + n, "Armenia", 1.0, prioridad, 10));
            n++;
        }
        return lista;
    }

    // ---------- pesoTotalMunicipio ----------

    @Test
    void pesoTotalDeArmeniaSegunElEnunciado() {
        assertEquals(14.0, Recursivos.pesoTotalPorMunicipio(pesosEnunciado(), "Armenia", 0), 1e-9);
    }

    @Test
    void pesoTotalDeSalento() {
        assertEquals(3.0, Recursivos.pesoTotalPorMunicipio(pesosEnunciado(), "Salento", 0), 1e-9);
    }

    @Test
    void pesoTotalIgnoraMayusculas() {
        assertEquals(14.0, Recursivos.pesoTotalPorMunicipio(pesosEnunciado(), "ARMENIA", 0), 1e-9);
    }

    @Test
    void pesoTotalDeMunicipioSinPaquetesEsCero() {
        assertEquals(0.0, Recursivos.pesoTotalPorMunicipio(pesosEnunciado(), "Calarcá", 0), 1e-9);
    }

    @Test
    void pesoTotalConListaVaciaEsCero() {
        assertEquals(0.0, Recursivos.pesoTotalPorMunicipio(new ArrayList<>(), "Armenia", 0), 1e-9);
    }

    @Test
    void pesoTotalDesdeUnIndiceIntermedio() {
        // desde el índice 2 solo quedan PQ415 (7.2) y PQ730 (2.3)
        assertEquals(9.5, Recursivos.pesoTotalPorMunicipio(pesosEnunciado(), "Armenia", 2), 1e-9);
    }

    @Test
    void pesoTotalConIndiceFueraDeRangoEsCero() {
        assertEquals(0.0, Recursivos.pesoTotalPorMunicipio(pesosEnunciado(), "Armenia", 4), 1e-9);
    }

    // ---------- contarPrioridadMinina ----------

    @Test
    void cuentaCuatroPaquetesConPrioridadMinimaCuatro() {
        List<Paquete> lista = conPrioridades(5, 2, 4, 1, 5, 3, 4);
        assertEquals(4, Recursivos.contarPrioridadMinima(lista, 4, 0));
    }

    @Test
    void cuentaTodosConPrioridadMinimaUno() {
        List<Paquete> lista = conPrioridades(5, 2, 4, 1, 5, 3, 4);
        assertEquals(7, Recursivos.contarPrioridadMinima(lista, 1, 0));
    }

    @Test
    void cuentaSoloLosDePrioridadCinco() {
        List<Paquete> lista = conPrioridades(5, 2, 4, 1, 5, 3, 4);
        assertEquals(2, Recursivos.contarPrioridadMinima(lista, 5, 0));
    }

    @Test
    void ningunoCumpleLaPrioridadMinima() {
        List<Paquete> lista = conPrioridades(1, 2, 3);
        assertEquals(0, Recursivos.contarPrioridadMinima(lista, 5, 0));
    }

    @Test
    void contarConListaVaciaEsCero() {
        assertEquals(0, Recursivos.contarPrioridadMinima(new ArrayList<>(), 3, 0));
    }

    @Test
    void contarConUnSoloElemento() {
        assertEquals(1, Recursivos.contarPrioridadMinima(conPrioridades(4), 4, 0));
        assertEquals(0, Recursivos.contarPrioridadMinima(conPrioridades(3), 4, 0));
    }
}
