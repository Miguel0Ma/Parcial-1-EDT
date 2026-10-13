package co.edu.uniquindio.servicios;

import co.edu.uniquindio.comparadores.ComparadoresPaquete;
import co.edu.uniquindio.modelo.Paquete;
import co.edu.uniquindio.modelo.Repartidor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de las operaciones de consulta, entrega y análisis de CentroDistribucion.
 */
class CentroDistribucionOperacionesTest {

    private CentroDistribucion centro;

    @BeforeEach
    void preparar() {
        centro = new CentroDistribucion();
    }

    private List<String> codigos(List<Paquete> paquetes) {
        List<String> salida = new ArrayList<>();
        for (Paquete p : paquetes) {
            salida.add(p.getCodigo());
        }
        return salida;
    }

    /** Datos del enunciado para ordenar y buscar (llegan desordenados). */
    private void cargarDesordenados() {
        centro.registrarPaquete(new Paquete("PQ731", "Armenia", 4.5, 5, 45));
        centro.registrarPaquete(new Paquete("PQ105", "Salento", 3.0, 5, 25));
        centro.registrarPaquete(new Paquete("PQ942", "Armenia", 7.2, 4, 20));
        centro.registrarPaquete(new Paquete("PQ318", "Calarcá", 2.3, 2, 60));
        centro.registrarPaquete(new Paquete("PQ567", "Salento", 18.7, 1, 35));
        centro.registrarPaquete(new Paquete("PQ829", "Armenia", 5.1, 3, 15));
    }

    // ---------- registro y orden de llegada ----------

    @Test
    void elRegistroConservaElOrdenDeLlegada() {
        cargarDesordenados();
        assertEquals(List.of("PQ731", "PQ105", "PQ942", "PQ318", "PQ567", "PQ829"),
                codigos(centro.getRegistro()));
    }

    @Test
    void elRegistroNoSePuedeModificarDesdeAfuera() {
        cargarDesordenados();
        assertThrows(UnsupportedOperationException.class,
                () -> centro.getRegistro().add(new Paquete("PQ1", "Armenia", 1.0, 3, 10)));
    }

    @Test
    void alInicioTodosLosPaquetesEstanPendientes() {
        cargarDesordenados();
        assertEquals(6, centro.getPendientes().size());
    }

    // ---------- municipios ----------

    @Test
    void municipiosSinRepetirElementos() {
        cargarDesordenados();
        assertEquals(3, centro.getMunicipios().size());
        assertTrue(centro.getMunicipios().contains("Armenia"));
        assertTrue(centro.getMunicipios().contains("Salento"));
        assertTrue(centro.getMunicipios().contains("Calarcá"));
    }

    @Test
    void municipiosOrdenadosAlfabeticamente() {
        cargarDesordenados();
        Iterator<String> it = centro.getMunicipiosOrdenados().iterator();
        assertEquals("Armenia", it.next());
        assertEquals("Calarcá", it.next());
        assertEquals("Salento", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void sinPaquetesNoHayMunicipios() {
        assertTrue(centro.getMunicipios().isEmpty());
        assertTrue(centro.getMunicipiosOrdenados().isEmpty());
    }

    @Test
    void paquetesAgrupadosPorMunicipio() {
        cargarDesordenados();
        assertEquals(List.of("PQ731", "PQ942", "PQ829"), codigos(centro.paquetesDeMunicipio("Armenia")));
        assertEquals(List.of("PQ105", "PQ567"), codigos(centro.paquetesDeMunicipio("Salento")));
        assertEquals(List.of("PQ318"), codigos(centro.paquetesDeMunicipio("Calarcá")));
    }

    @Test
    void municipioSinPaquetesDevuelveListaVacia() {
        cargarDesordenados();
        assertTrue(centro.paquetesDeMunicipio("Filandia").isEmpty());
    }

    @Test
    void laListaDeUnMunicipioNoSePuedeModificarDesdeAfuera() {
        cargarDesordenados();
        assertThrows(UnsupportedOperationException.class,
                () -> centro.paquetesDeMunicipio("Armenia").clear());
    }

    // ---------- ordenamientos ----------

    @Test
    void ordenNaturalEsPorCodigoAscendente() {
        cargarDesordenados();
        assertEquals(List.of("PQ105", "PQ318", "PQ567", "PQ731", "PQ829", "PQ942"),
                codigos(centro.ordenNatural()));
    }

    @Test
    void ordenarNoCambiaElOrdenDeLlegadaDelRegistro() {
        cargarDesordenados();
        centro.ordenNatural();
        centro.ordenarPor(ComparadoresPaquete.PESO_DESC);
        assertEquals("PQ731", centro.getRegistro().get(0).getCodigo());
    }

    @Test
    void ordenarPorPesoDeMayorAMenor() {
        cargarDesordenados();
        assertEquals(List.of("PQ567", "PQ942", "PQ829", "PQ731", "PQ105", "PQ318"),
                codigos(centro.ordenarPor(ComparadoresPaquete.PESO_DESC)));
    }

    @Test
    void ordenarPorTiempoDeMenorAMayor() {
        cargarDesordenados();
        assertEquals(List.of("PQ829", "PQ942", "PQ105", "PQ567", "PQ731", "PQ318"),
                codigos(centro.ordenarPor(ComparadoresPaquete.TIEMPO_ASC)));
    }

    @Test
    void ordenarPorPrioridadDeMayorAMenor() {
        cargarDesordenados();
        List<Paquete> ordenados = centro.ordenarPor(ComparadoresPaquete.PRIORIDAD_DESC);
        assertEquals(5, ordenados.get(0).getPrioridad());
        assertEquals(1, ordenados.get(ordenados.size() - 1).getPrioridad());
    }

    @Test
    void ordenarCentroVacioDevuelveListaVacia() {
        assertTrue(centro.ordenNatural().isEmpty());
        assertTrue(centro.ordenarPor(ComparadoresPaquete.PESO_DESC).isEmpty());
    }

    // ---------- búsqueda binaria ----------

    @Test
    void buscaBinariaEncuentraUnPaqueteEnLaListaOrdenada() {
        cargarDesordenados();
        Paquete encontrado = centro.buscarBinaria(centro.ordenNatural(), "PQ731");
        assertNotNull(encontrado);
        assertEquals("PQ731", encontrado.getCodigo());
        assertEquals("Armenia", encontrado.getDestino());
    }

    @Test
    void buscaBinariaDevuelveNullSiNoExiste() {
        cargarDesordenados();
        assertNull(centro.buscarBinaria(centro.ordenNatural(), "PQ000"));
    }

    @Test
    void buscaBinariaEnListaVaciaDevuelveNull() {
        assertNull(centro.buscarBinaria(new ArrayList<>(), "PQ731"));
    }

    // ---------- análisis recursivos y divide y vencerás ----------

    @Test
    void pesoTotalDeUnMunicipio() {
        cargarDesordenados();
        // Armenia: 4.5 + 7.2 + 5.1
        assertEquals(16.8, centro.pesoTotalMunicipios("Armenia"), 1e-9);
        assertEquals(21.7, centro.pesoTotalMunicipios("Salento"), 1e-9);
    }

    @Test
    void pesoTotalDeMunicipioSinPaquetesEsCero() {
        cargarDesordenados();
        assertEquals(0.0, centro.pesoTotalMunicipios("Filandia"), 1e-9);
    }

    @Test
    void contarPaquetesConPrioridadMinima() {
        cargarDesordenados();
        // prioridades: 5, 5, 4, 2, 1, 3
        assertEquals(3, centro.contarPrioridadMinima(4));
        assertEquals(6, centro.contarPrioridadMinima(1));
        assertEquals(0, centro.contarPrioridadMinima(6));
    }

    @Test
    void paqueteDeMayorPeso() {
        cargarDesordenados();
        Paquete mayor = centro.paqueteMayorPeso();
        assertEquals("PQ567", mayor.getCodigo());
        assertEquals(18.7, mayor.getPeso(), 1e-9);
    }

    @Test
    void paqueteDeMayorPesoSinPaquetesLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> centro.paqueteMayorPeso());
    }

    // ---------- asignación automática ----------

    @Test
    void asignarAutomaticamenteUsaAlRepartidorDeLaZona() {
        centro.registrarPaquete(new Paquete("PQ731", "Armenia", 4.5, 5, 45));
        centro.registrarRepartidor(new Repartidor("R1", "Ana", "Armenia", true));
        centro.registrarRepartidor(new Repartidor("R2", "Luis", "Salento", true));

        centro.asignarAutomaticamente("PQ731");

        assertFalse(centro.consultarRepartidor("R1").isDisponible());
        assertTrue(centro.consultarRepartidor("R2").isDisponible());
    }

    @Test
    void asignarAutomaticamenteIgnoraAlRepartidorNoDisponible() {
        centro.registrarPaquete(new Paquete("PQ731", "Armenia", 4.5, 5, 45));
        centro.registrarRepartidor(new Repartidor("R1", "Ana", "Armenia", false));
        centro.registrarRepartidor(new Repartidor("R2", "Luis", "Armenia", true));

        centro.asignarAutomaticamente("PQ731");

        assertFalse(centro.consultarRepartidor("R2").isDisponible());
    }

    @Test
    void asignarAutomaticamenteConPaqueteInexistenteLanzaExcepcion() {
        centro.registrarRepartidor(new Repartidor("R1", "Ana", "Armenia", true));
        assertThrows(IllegalArgumentException.class, () -> centro.asignarAutomaticamente("PQ000"));
        assertTrue(centro.consultarRepartidor("R1").isDisponible());
    }

    // ---------- asignar saca el paquete de las colas ----------

    @Test
    void unPaqueteAsignadoYaNoEsElSiguienteEnLaColaPrioritaria() {
        centro.registrarPaquete(new Paquete("PQ105", "Armenia", 3.0, 5, 25));
        centro.registrarPaquete(new Paquete("PQ420", "Salento", 6.0, 3, 30));
        centro.registrarRepartidor(new Repartidor("R1", "Ana", "Armenia", true));

        centro.asignarPaquete("PQ105", "R1");

        assertEquals("PQ420", centro.verSiguientePrioritario().getCodigo());
        assertEquals("PQ420", centro.despacharProritario().getCodigo());
        assertNull(centro.despacharProritario());
    }

    // ---------- registro de entregas e historial ----------

    private void cargarEntregables() {
        centro.registrarPaquete(new Paquete("PQ105", "Armenia", 3.0, 5, 25));
        centro.registrarPaquete(new Paquete("PQ942", "Armenia", 2.0, 4, 20));
        centro.registrarPaquete(new Paquete("PQ731", "Armenia", 5.0, 5, 45));
        centro.registrarRepartidor(new Repartidor("R1", "Ana", "Armenia", true));
    }

    @Test
    void elHistorialEmpiezaVacio() {
        assertTrue(centro.getHistorial().estaVacia());
    }

    @Test
    void entregarLiberaAlRepartidorYSacaElPaqueteDePendientes() {
        cargarEntregables();
        centro.asignarPaquete("PQ105", "R1");
        assertFalse(centro.consultarRepartidor("R1").isDisponible());

        centro.registrarEntrega("PQ105");

        assertTrue(centro.consultarRepartidor("R1").isDisponible());
        assertEquals(2, centro.getPendientes().size());
        assertFalse(centro.getPendientes().contains(centro.consultarPaquete("PQ105")));
    }

    @Test
    void entregarGuardaElPaqueteEnElHistorial() {
        cargarEntregables();
        centro.asignarPaquete("PQ105", "R1");

        centro.registrarEntrega("PQ105");

        assertEquals(1, centro.getHistorial().tamano());
        assertSame(centro.consultarPaquete("PQ105"), centro.getHistorial().obtener(0));
    }

    @Test
    void elHistorialConservaElOrdenRealDeLasEntregas() {
        cargarEntregables();
        String[] ordenDeEntrega = {"PQ105", "PQ942", "PQ731"};
        for (String codigo : ordenDeEntrega) {
            centro.asignarPaquete(codigo, "R1");
            centro.registrarEntrega(codigo);
        }

        List<String> entregados = new ArrayList<>();
        for (Paquete p : centro.getHistorial()) {
            entregados.add(p.getCodigo());
        }
        assertEquals(List.of("PQ105", "PQ942", "PQ731"), entregados);
        assertEquals(0, centro.getPendientes().size());
    }

    @Test
    void unPaqueteEntregadoSigueExistiendoEnElRegistro() {
        cargarEntregables();
        centro.asignarPaquete("PQ105", "R1");
        centro.registrarEntrega("PQ105");

        assertTrue(centro.existePaquete("PQ105"));
        assertEquals(3, centro.getRegistro().size());
    }

    @Test
    void noSePuedeEntregarUnPaqueteSinAsignar() {
        cargarEntregables();
        assertThrows(IllegalArgumentException.class, () -> centro.registrarEntrega("PQ105"));
        assertTrue(centro.getHistorial().estaVacia());
    }

    @Test
    void noSePuedeEntregarUnPaqueteInexistente() {
        assertThrows(IllegalArgumentException.class, () -> centro.registrarEntrega("PQ000"));
    }

    @Test
    void noSePuedeEntregarDosVecesElMismoPaquete() {
        cargarEntregables();
        centro.asignarPaquete("PQ105", "R1");
        centro.registrarEntrega("PQ105");

        assertThrows(IllegalArgumentException.class, () -> centro.registrarEntrega("PQ105"));
        assertEquals(1, centro.getHistorial().tamano());
    }

    @Test
    void unPaqueteEntregadoNoSePuedeVolverAAsignar() {
        cargarEntregables();
        centro.asignarPaquete("PQ105", "R1");
        centro.registrarEntrega("PQ105");

        assertThrows(IllegalArgumentException.class, () -> centro.asignarPaquete("PQ105", "R1"));
    }

    // ---------- repartidores ----------

    @Test
    void listaDeRepartidoresRegistrados() {
        centro.registrarRepartidor(new Repartidor("R1", "Ana", "Armenia", true));
        centro.registrarRepartidor(new Repartidor("R2", "Luis", "Salento", false));
        assertEquals(2, centro.getRepartidores().size());
        assertThrows(UnsupportedOperationException.class, () -> centro.getRepartidores().clear());
    }
}