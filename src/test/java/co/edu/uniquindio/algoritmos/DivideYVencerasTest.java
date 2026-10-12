package co.edu.uniquindio.algoritmos;

import co.edu.uniquindio.modelo.Paquete;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DivideYVencerasTest {

    private List<Paquete> conPesos(double... pesos) {
        List<Paquete> lista = new ArrayList<>();
        int n = 1;
        for (double peso : pesos) {
            lista.add(new Paquete("PQ" + n, "Armenia", peso, 3, 10));
            n++;
        }
        return lista;
    }

    @Test
    void encuentraElMayorPesoDelEnunciado() {
        List<Paquete> lista = conPesos(4.5, 12.0, 3.2, 18.7, 7.4, 5.1);
        Paquete mayor = DivideYVenceras.mayorPeso(lista);
        assertEquals(18.7, mayor.getPeso(), 1e-9);
        assertEquals("PQ4", mayor.getCodigo());
    }

    @Test
    void elMayorEstaAlPrincipio() {
        Paquete mayor = DivideYVenceras.mayorPeso(conPesos(20.0, 3.0, 4.0, 5.0));
        assertEquals("PQ1", mayor.getCodigo());
    }

    @Test
    void elMayorEstaAlFinal() {
        Paquete mayor = DivideYVenceras.mayorPeso(conPesos(1.0, 3.0, 4.0, 50.0));
        assertEquals("PQ4", mayor.getCodigo());
    }

    @Test
    void conUnSoloPaqueteEsElCasoBase() {
        List<Paquete> lista = conPesos(7.5);
        assertSame(lista.get(0), DivideYVenceras.mayorPeso(lista));
    }

    @Test
    void conDosPaquetes() {
        assertEquals("PQ2", DivideYVenceras.mayorPeso(conPesos(1.0, 2.0)).getCodigo());
        assertEquals("PQ1", DivideYVenceras.mayorPeso(conPesos(2.0, 1.0)).getCodigo());
    }

    @Test
    void cantidadImparDePaquetes() {
        assertEquals("PQ3", DivideYVenceras.mayorPeso(conPesos(1.0, 2.0, 9.0, 3.0, 4.0)).getCodigo());
    }

    @Test
    void enEmpateDevuelveElDeLaIzquierda() {
        Paquete mayor = DivideYVenceras.mayorPeso(conPesos(5.0, 5.0, 5.0));
        assertEquals("PQ1", mayor.getCodigo());
    }

    @Test
    void listaVaciaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> DivideYVenceras.mayorPeso(new ArrayList<>()));
    }

    @Test
    void listaNulaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> DivideYVenceras.mayorPeso(null));
    }

    @Test
    void coincideConUnaBusquedaLinealEnMuchosDatos() {
        List<Paquete> lista = new ArrayList<>();
        Paquete esperado = null;
        for (int i = 1; i <= 101; i++) {
            double peso = ((i * 37) % 101) + 1.0; // pesos variados, todos distintos
            Paquete p = new Paquete("PQ" + i, "Armenia", peso, 3, 10);
            lista.add(p);
            if (esperado == null) {
                esperado = p;
            } else if (p.getPeso() > esperado.getPeso()) {
                esperado = p;
            }
        }
        assertSame(esperado, DivideYVenceras.mayorPeso(lista));
    }

    @Test
    void laTrazaMuestraLasEtapasDeDivideYVenceras() {
        PrintStream original = System.out;
        ByteArrayOutputStream captura = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captura));
        Paquete mayor;
        try {
            mayor = DivideYVenceras.mayorPesoConTraza(conPesos(4.5, 12.0, 3.2, 18.7));
        } finally {
            System.setOut(original);
        }
        String salida = captura.toString();
        assertEquals("PQ4", mayor.getCodigo());
        assertTrue(salida.contains("dividir"));
        assertTrue(salida.contains("caso base"));
        assertTrue(salida.contains("combinar"));
    }

    @Test
    void laTrazaConListaVaciaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> DivideYVenceras.mayorPesoConTraza(new ArrayList<>()));
    }
}
