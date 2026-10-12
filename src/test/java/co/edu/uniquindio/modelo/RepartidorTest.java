package co.edu.uniquindio.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepartidorTest {

    @Test
    void creaRepartidorValido() {
        Repartidor r = new Repartidor("R1", "Ana Gómez", "Armenia", true);
        assertEquals("R1", r.getId());
        assertEquals("Ana Gómez", r.getNombre());
        assertEquals("Armenia", r.getZona());
        assertTrue(r.isDisponible());
    }

    @Test
    void recortaEspacios() {
        Repartidor r = new Repartidor(" R1 ", " Ana ", " Armenia ", true);
        assertEquals("R1", r.getId());
        assertEquals("Ana", r.getNombre());
        assertEquals("Armenia", r.getZona());
    }

    @Test
    void rechazaIdNuloOVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Repartidor(null, "Ana", "Armenia", true));
        assertThrows(IllegalArgumentException.class, () -> new Repartidor("  ", "Ana", "Armenia", true));
    }

    @Test
    void rechazaNombreNuloOVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Repartidor("R1", null, "Armenia", true));
        assertThrows(IllegalArgumentException.class, () -> new Repartidor("R1", "", "Armenia", true));
    }

    @Test
    void rechazaZonaNulaOVacia() {
        assertThrows(IllegalArgumentException.class, () -> new Repartidor("R1", "Ana", null, true));
        assertThrows(IllegalArgumentException.class, () -> new Repartidor("R1", "Ana", " ", true));
    }

    @Test
    void cambiaDisponibilidad() {
        Repartidor r = new Repartidor("R1", "Ana", "Armenia", true);
        r.setDisponible(false);
        assertFalse(r.isDisponible());
        r.setDisponible(true);
        assertTrue(r.isDisponible());
    }

    @Test
    void igualdadYHashSeBasanEnLaIdentificacion() {
        Repartidor a = new Repartidor("R1", "Ana", "Armenia", true);
        Repartidor b = new Repartidor("R1", "Otro", "Salento", false);
        Repartidor c = new Repartidor("R2", "Ana", "Armenia", true);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
    }

    @Test
    void toStringIndicaElEstado() {
        Repartidor r = new Repartidor("R1", "Ana", "Armenia", true);
        assertTrue(r.toString().contains("disponible"));
        r.setDisponible(false);
        assertTrue(r.toString().contains("ocupado"));
    }
}
