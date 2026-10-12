package co.edu.uniquindio.modelo;

public class Repartidor {
    private final String id;
    private final String nombre;
    private final String zona;
    private boolean disponible;

    public Repartidor(String id, String nombre, String zona, boolean disponible) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("La identificación del repartidor no puede ser nula ni vacía");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del repartidor no puede ser nulo ni vacío");
        }
        if (zona == null || zona.trim().isEmpty()) {
            throw new IllegalArgumentException("La zona del repartidor no puede ser nula ni vacía");
        }
        this.id = id.trim();
        this.nombre = nombre.trim();
        this.zona = zona.trim();
        this.disponible = disponible;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getZona() {
        return zona;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Repartidor)) {
            return false;
        }
        return id.equals(((Repartidor) o).id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        String estado;
        if (disponible) {
            estado = "disponible";
        } else {
            estado = "ocupado";
        }
        return id + "[" + nombre + ", zona " + zona + ", " + estado + "]";
    }
}
