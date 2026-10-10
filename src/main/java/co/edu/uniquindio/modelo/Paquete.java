package co.edu.uniquindio.modelo;

import java.util.Locale;

public class Paquete implements Comparable<Paquete>{
    public static final int PRIORIDAD_MINIMA = 1;
    public static final int PRIORIDAD_MAXIMA = 5;
    public static final int PRIORIDAD_POR_DEFECTO = 3;

    private final String codigo;
    private final String destino;
    private final double peso;          // kg
    private final int prioridad;        // 1..5, mayor = más urgente
    private final int tiempoEstimado;   // minutos

    public Paquete(String codigo, String destino, double peso, int prioridad, int tiempoEstimado) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del paquete no puede ser nulo ni vacío");
        }
        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("El destino no puede ser nulo ni vacío");
        }
        if (!(peso > 0)) {   // también rechaza NaN
            throw new IllegalArgumentException("El peso debe ser mayor que cero: " + peso);
        }
        if (prioridad < PRIORIDAD_MINIMA || prioridad > PRIORIDAD_MAXIMA) {
            throw new IllegalArgumentException("La prioridad debe estar entre "
                    + PRIORIDAD_MINIMA + " y " + PRIORIDAD_MAXIMA + ": " + prioridad);
        }
        if (tiempoEstimado <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser mayor que cero: " + tiempoEstimado);
        }
        this.codigo = codigo.trim();
        this.destino = destino.trim();
        this.peso = peso;
        this.prioridad = prioridad;
        this.tiempoEstimado = tiempoEstimado;
    }

    public Paquete(String codigo, String destino, double peso, int tiempoEstimado) {
        this(codigo, destino, peso, PRIORIDAD_POR_DEFECTO, tiempoEstimado);
    }

    public String getCodigo() {
        return codigo;
    }
    public String getDestino() {
        return destino;
    }
    public double getPeso() {
        return peso;
    }
    public int getPrioridad() {
        return prioridad;
    }
    public int getTiempoEstimado() {
        return tiempoEstimado;
    }
    @Override
    public int compareTo(Paquete otro) {
        return this.codigo.compareTo(otro.codigo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Paquete)) {
            return false;
        }
        return codigo.equals(((Paquete) o).codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
    @Override
    public String toString() {
        return String.format(Locale.US, "%s[%s, %.1f kg, prio %d, %d min]",
                codigo, destino, peso, prioridad, tiempoEstimado);
    }

}
