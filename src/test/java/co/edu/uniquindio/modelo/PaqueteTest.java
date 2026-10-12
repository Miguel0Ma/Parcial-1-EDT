package co.edu.uniquindio.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaqueteTest {

    @Test
    void creaPaqueteValido() {
        Paquete p = new Paquete("PQ731", "Armenia", 4.5, 5, 45);
        assertEquals("PQ731", p.getCodigo());
        assertEquals("Armenia", p.getDestino());
        assertEquals(4.5, p.getPeso());
        assertEquals(5, p.getPrioridad());
        assertEquals(45, p.getTiempoEstimado());
    }

    @Test
    void prioridadPorDefectoEsTres() {
        Paquete p = new Paquete("PQ105", "Salento", 2.0, 30);
        assertEquals(Paquete.PRIORIDAD_POR_DEFECTO, p.getPrioridad());
        assertEquals(3, p.getPrioridad());
    }

    @Test
    void recortaEspaciosDeCodigoYDestino() {
        Paquete p = new Paquete("  PQ1 ", "  Armenia  ", 1.0, 3, 10);
        assertEquals("PQ1", p.getCodigo());
        assertEquals("Armenia", p.getDestino());
    }

    @Test
    void rechazaCodigoNuloOVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Paquete(null, "Armenia", 1.0, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> new Paquete("", "Armenia", 1.0, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> new Paquete("   ", "Armenia", 1.0, 3, 10));
    }

    @Test
    void rechazaDestinoNuloOVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", null, 1.0, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "", 1.0, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "  ", 1.0, 3, 10));
    }

    @Test
    void rechazaPesoNoPositivo() {
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 0, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", -2.5, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", Double.NaN, 3, 10));
    }

    @Test
    void rechazaPrioridadFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 1.0, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 1.0, 6, 10));
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 1.0, -1, 10));
    }

    @Test
    void aceptaLosLimitesDePrioridad() {
        assertEquals(1, new Paquete("PQ1", "Armenia", 1.0, 1, 10).getPrioridad());
        assertEquals(5, new Paquete("PQ2", "Armenia", 1.0, 5, 10).getPrioridad());
    }

    @Test
    void rechazaTiempoEstimadoNoPositivo() {
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 1.0, 3, 0));
        assertThrows(IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 1.0, 3, -5));
    }

    @Test
    void ordenNaturalEsPorCodigoAscendente() {
        Paquete a = new Paquete("PQ105", "Salento", 1.0, 3, 10);
        Paquete b = new Paquete("PQ731", "Armenia", 9.0, 5, 5);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(new Paquete("PQ105", "Calarcá", 7.0, 1, 99)));
    }

    @Test
    void igualdadYHashSeBasanEnElCodigo() {
        Paquete a = new Paquete("PQ105", "Salento", 1.0, 3, 10);
        Paquete b = new Paquete("PQ105", "Armenia", 8.0, 5, 20);
        Paquete c = new Paquete("PQ999", "Salento", 1.0, 3, 10);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "PQ105");
    }

    @Test
    void toStringContieneLosDatos() {
        String s = new Paquete("PQ731", "Armenia", 4.5, 5, 45).toString();
        assertTrue(s.contains("PQ731"));
        assertTrue(s.contains("Armenia"));
        assertTrue(s.contains("4.5"));
    }
}
