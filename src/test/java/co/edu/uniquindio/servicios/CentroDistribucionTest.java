package co.edu.uniquindio.servicios;

import co.edu.uniquindio.modelo.Paquete;
import co.edu.uniquindio.modelo.Repartidor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CentroDistribucionTest {

    private CentroDistribucion centro;

    @BeforeEach
    void preparar() {
        centro = new CentroDistribucion();
    }

    // ---------- paquetes ----------

    @Test
    void registraYConsultaUnPaquete() {
        Paquete p = new Paquete("PQ731", "Armenia", 4.5, 5, 45);
        centro.registrarPaquete(p);
        assertTrue(centro.existePaquete("PQ731"));
        assertSame(p, centro.consultarPaquete("PQ731"));
    }

    @Test
    void consultarUnCodigoInexistenteDevuelveNull() {
        assertNull(centro.consultarPaquete("PQ000"));
        assertFalse(centro.existePaquete("PQ000"));
    }

    @Test
    void noPermiteRegistrarUnPaqueteNulo() {
        assertThrows(IllegalArgumentException.class, () -> centro.registrarPaquete(null));
    }

    @Test
    void noPermiteCodigosDuplicados() {
        centro.registrarPaquete(new Paquete("PQ731", "Armenia", 4.5, 5, 45));
        Paquete repetido = new Paquete("PQ731", "Salento", 1.0, 2, 10);
        assertThrows(IllegalArgumentException.class, () -> centro.registrarPaquete(repetido));
    }

    @Test
    void elDuplicadoRechazadoNoReemplazaAlOriginal() {
        Paquete original = new Paquete("PQ731", "Armenia", 4.5, 5, 45);
        centro.registrarPaquete(original);
        try {
            centro.registrarPaquete(new Paquete("PQ731", "Salento", 1.0, 2, 10));
            fail("Debió lanzar IllegalArgumentException");
        } catch (IllegalArgumentException esperado) {
            assertSame(original, centro.consultarPaquete("PQ731"));
        }
    }

    @Test
    void registraVariosPaquetesYLosConsultaPorCodigo() {
        centro.registrarPaquete(new Paquete("PQ731", "Armenia", 4.5, 5, 45));
        centro.registrarPaquete(new Paquete("PQ105", "Salento", 3.0, 5, 25));
        centro.registrarPaquete(new Paquete("PQ942", "Armenia", 2.0, 4, 20));
        assertEquals("Salento", centro.consultarPaquete("PQ105").getDestino());
        assertEquals(2.0, centro.consultarPaquete("PQ942").getPeso(), 1e-9);
        assertEquals(45, centro.consultarPaquete("PQ731").getTiempoEstimado());
    }

    // ---------- repartidores ----------

    @Test
    void registraYConsultaUnRepartidor() {
        Repartidor r = new Repartidor("R1", "Ana", "Armenia", true);
        centro.registrarRepartidor(r);
        assertSame(r, centro.consultarRepartidor("R1"));
    }

    @Test
    void consultarRepartidorInexistenteDevuelveNull() {
        assertNull(centro.consultarRepartidor("R99"));
    }

    @Test
    void noPermiteRegistrarUnRepartidorNulo() {
        assertThrows(IllegalArgumentException.class, () -> centro.registrarRepartidor(null));
    }

    @Test
    void noPermiteIdentificacionesDuplicadas() {
        centro.registrarRepartidor(new Repartidor("R1", "Ana", "Armenia", true));
        Repartidor repetido = new Repartidor("R1", "Luis", "Salento", true);
        assertThrows(IllegalArgumentException.class, () -> centro.registrarRepartidor(repetido));
    }

    // ---------- despacho prioritario ----------

    @Test
    void despachaSegunPrioridadTiempoYCodigo() {
        centro.registrarPaquete(new Paquete("PQ420", "Armenia", 6.0, 3, 30));
        centro.registrarPaquete(new Paquete("PQ731", "Armenia", 12.0, 5, 45));
        centro.registrarPaquete(new Paquete("PQ105", "Salento", 3.0, 5, 25));
        centro.registrarPaquete(new Paquete("PQ942", "Calarcá", 8.0, 4, 20));

        assertEquals("PQ105", centro.despacharProritario().getCodigo());
        assertEquals("PQ731", centro.despacharProritario().getCodigo());
        assertEquals("PQ942", centro.despacharProritario().getCodigo());
        assertEquals("PQ420", centro.despacharProritario().getCodigo());
    }

    @Test
    void despachaPrimeroElDeMenorCodigoSiTodoEmpata() {
        centro.registrarPaquete(new Paquete("PQ900", "Armenia", 1.0, 4, 20));
        centro.registrarPaquete(new Paquete("PQ100", "Salento", 1.0, 4, 20));
        assertEquals("PQ100", centro.despacharProritario().getCodigo());
        assertEquals("PQ900", centro.despacharProritario().getCodigo());
    }

    @Test
    void despacharSinPaquetesDevuelveNull() {
        assertNull(centro.despacharProritario());
    }

    @Test
    void despacharMasVecesQuePaquetesDevuelveNullAlFinal() {
        centro.registrarPaquete(new Paquete("PQ1", "Armenia", 1.0, 3, 10));
        assertNotNull(centro.despacharProritario());
        assertNull(centro.despacharProritario());
    }

    @Test
    void despacharNoEliminaElPaqueteDelRegistroPorCodigo() {
        centro.registrarPaquete(new Paquete("PQ1", "Armenia", 1.0, 3, 10));
        centro.despacharProritario();
        assertTrue(centro.existePaquete("PQ1"));
    }
}
