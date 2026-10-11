package co.edu.uniquindio.algoritmos;
import co.edu.uniquindio.modelo.Paquete;
import java.util.List;

public class BusquedaBinaria {
    private BusquedaBinaria() {}

    /** @return posición del paquete en la lista, o -1 si no está */
    public static int buscarPorCodigo(List<Paquete> ordenada, String codigo) {
        int bajo = 0;
        int alto = ordenada.size() - 1;

        while (bajo <= alto) {
            int medio = bajo + (alto - bajo) / 2;          // evita desbordamiento de int
            int comparacion = ordenada.get(medio).getCodigo().compareTo(codigo);

            if (comparacion == 0) {
                return medio;                               // encontrado
            } else if (comparacion < 0) {
                bajo = medio + 1;                           // descartar mitad izquierda
            } else {
                alto = medio - 1;                           // descartar mitad derecha
            }
        }
        return -1;
    }
}
