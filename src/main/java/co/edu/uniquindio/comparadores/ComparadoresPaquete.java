package co.edu.uniquindio.comparadores;
import co.edu.uniquindio.modelo.Paquete;

import java.util.Comparator;

public class ComparadoresPaquete  {

    private ComparadoresPaquete() {
    }
    // 1. Clase que implementa explícitamente Comparator
    public static class ComparadorPrioridadDesc implements Comparator<Paquete> {
        @Override
        public int compare(Paquete a, Paquete b) {
            return Integer.compare(b.getPrioridad(), a.getPrioridad());
        }
    }
    // 2. Clase que implementa explícitamente Comparator
    public static class ComparadorPesoDesc implements Comparator<Paquete> {
        @Override
        public int compare(Paquete a, Paquete b) {
            return Double.compare(b.getPeso(), a.getPeso());
        }
    }
    // 3. Clase que implementa explícitamente Comparator
    public static class ComparadorTiempoAsc implements Comparator<Paquete> {
        @Override
        public int compare(Paquete a, Paquete b) {
            return Integer.compare(a.getTiempoEstimado(), b.getTiempoEstimado());
        }
    }
    // 4. Clase que implementa explícitamente Comparator
    public static class ComparadorDespachoPrioritario implements Comparator<Paquete> {
        @Override
        public int compare(Paquete a, Paquete b) {
            int compPrioridad = Integer.compare(b.getPrioridad(), a.getPrioridad());
            if (compPrioridad != 0) {
                return compPrioridad;
            }
            int compTiempo = Integer.compare(a.getTiempoEstimado(), b.getTiempoEstimado());
            if (compTiempo != 0) {
                return compTiempo;
            }
            return a.getCodigo().compareTo(b.getCodigo());
        }
    }
    // Instancias para que el resto del proyecto las use directamente sin romper nada:
    public static final Comparator<Paquete> PRIORIDAD_DESC = new ComparadorPrioridadDesc();
    public static final Comparator<Paquete> PESO_DESC = new ComparadorPesoDesc();
    public static final Comparator<Paquete> TIEMPO_ASC = new ComparadorTiempoAsc();
    public static final Comparator<Paquete> DESPACHO_PRIORITARIO = new ComparadorDespachoPrioritario();
}

