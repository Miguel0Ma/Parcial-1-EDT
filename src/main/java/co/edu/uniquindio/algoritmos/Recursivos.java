package co.edu.uniquindio.algoritmos;
import java.util.List;
import co.edu.uniquindio.modelo.Paquete;

/**
 * Operaciones solicitadas mediante recursividad.
 * Por el enunciado NO se usan for, while ni do-while en estos métodos.
 *
 * Complejidad (ambas): tiempo O(n); espacio O(n) por la pila de llamadas
 * (n llamadas pendientes). Con listas muy grandes (cientos de miles) la pila
 * de Java podría desbordarse (StackOverflowError); para la jornada del parcial
 * no es un problema.
 */
public final class Recursivos {

    private Recursivos() {
    }

    /** Peso total de los paquetes destinados a un municipio. Llamar con indice = 0. */
    public static double pesoTotalPorMunicipio(List<Paquete> paquetes, String destino, int indice) {
        // CASO BASE: ya no quedan paquetes por revisar
        if (indice >= paquetes.size()) {
            return 0.0;
        }
        // CASO RECURSIVO: el avance es indice + 1, que se acerca al caso base
        Paquete actual = paquetes.get(indice);
        double restoDeLaLista = pesoTotalPorMunicipio(paquetes, destino, indice + 1);
        if (actual.getDestino().equalsIgnoreCase(destino)) {
            return actual.getPeso() + restoDeLaLista;
        }
        return restoDeLaLista;
    }

    /** Cantidad de paquetes con prioridad >= minima. Llamar con indice = 0. */
    public static int contarPrioridadMinima(List<Paquete> paquetes, int minima, int indice) {
        // CASO BASE
        if (indice >= paquetes.size()) {
            return 0;
        }
        // CASO RECURSIVO
        int restoDeLaLista = contarPrioridadMinima(paquetes, minima, indice + 1);
        if (paquetes.get(indice).getPrioridad() >= minima) {
            return 1 + restoDeLaLista;
        }
        return restoDeLaLista;
    }
}