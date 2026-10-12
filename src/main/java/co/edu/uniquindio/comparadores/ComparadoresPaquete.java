package co.edu.uniquindio.comparadores;
import co.edu.uniquindio.modelo.Paquete;

import java.util.Comparator;

public class ComparadoresPaquete {

    private ComparadoresPaquete() {
    }

    /** Prioridad de mayor a menor. */
    public static final Comparator<Paquete> PRIORIDAD_DESC =
            Comparator.comparingInt(Paquete::getPrioridad).reversed();

    /** Peso de mayor a menor. */
    public static final Comparator<Paquete> PESO_DESC =
            Comparator.comparingDouble(Paquete::getPeso).reversed();

    /** Tiempo estimado de menor a mayor. */
    public static final Comparator<Paquete> TIEMPO_ASC =
            Comparator.comparingInt(Paquete::getTiempoEstimado);

    /**
     * Despacho prioritario:
     * 1) mayor prioridad, 2) menor tiempo estimado, 3) código alfabéticamente.
     */
    public static final Comparator<Paquete> DESPACHO_PRIORITARIO =
            PRIORIDAD_DESC
                    .thenComparing(TIEMPO_ASC)
                    .thenComparing(Comparator.<Paquete>naturalOrder());
}

