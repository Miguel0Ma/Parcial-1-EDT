package co.edu.uniquindio.algoritmos;

import co.edu.uniquindio.modelo.Paquete;

import java.util.List;

/**
 * Paquete de mayor peso con la estrategia divide y vencerás.
 *
 * Etapas:
 *  1. CASO BASE: un rango con un solo paquete; ese es el mayor.
 *  2. DIVISIÓN: se parte el rango [inicio, fin] por la mitad.
 *  3. SUBPROBLEMA IZQUIERDO: mayor de [inicio, medio].
 *  4. SUBPROBLEMA DERECHO: mayor de [medio + 1, fin].
 *  5. COMBINACIÓN: se queda con el más pesado de los dos resultados.
 *  6. RESULTADO FINAL: lo que devuelve la llamada con el rango completo.
 *
 * Complejidad: tiempo O(n) (cada paquete se compara una vez; T(n) = 2T(n/2) + O(1));
 * espacio O(log n) por la profundidad de la pila de llamadas.
 */
public final class DivideYVenceras {
    private DivideYVenceras() {
    }
    public static Paquete mayorPeso(List<Paquete> paquetes) {
        validar(paquetes);
        return mayorPeso(paquetes, 0, paquetes.size() - 1, false, 0);
    }
    /** Igual que mayorPeso, pero imprime cada división, caso base y combinación. */
    public static Paquete mayorPesoConTraza(List<Paquete> paquetes) {
        validar(paquetes);
        return mayorPeso(paquetes, 0, paquetes.size() - 1, true, 0);
    }
    private static void validar(List<Paquete> paquetes) {
        if (paquetes == null || paquetes.isEmpty()) {
            throw new IllegalArgumentException("No hay paquetes para analizar");
        }
    }
    private static Paquete mayorPeso(List<Paquete> p, int inicio, int fin, boolean traza, int nivel) {
        String sangria = "  ".repeat(nivel);
        // 1. CASO BASE
        if (inicio == fin) {
            if (traza) {
                System.out.println(sangria + "caso base [" + inicio + "] -> "
                        + p.get(inicio).getCodigo() + " (" + p.get(inicio).getPeso() + " kg)");
            }
            return p.get(inicio);
        }
        // 2. DIVISIÓN
        int medio = inicio + (fin - inicio) / 2;
        if (traza) {
            System.out.println(sangria + "dividir [" + inicio + ".." + fin + "] -> ["
                    + inicio + ".." + medio + "] y [" + (medio + 1) + ".." + fin + "]");
        }
        // 3. SUBPROBLEMA IZQUIERDO y 4. SUBPROBLEMA DERECHO
        Paquete mayorIzquierda = mayorPeso(p, inicio, medio, traza, nivel + 1);
        Paquete mayorDerecha = mayorPeso(p, medio + 1, fin, traza, nivel + 1);
        // 5. COMBINACIÓN (con if / else explícito)
        Paquete mayor;
        if (mayorIzquierda.getPeso() >= mayorDerecha.getPeso()) {
            mayor = mayorIzquierda;
        } else {
            mayor = mayorDerecha;
        }
        if (traza) {
            System.out.println(sangria + "combinar [" + inicio + ".." + fin + "]: "
                    + mayorIzquierda.getCodigo() + " vs " + mayorDerecha.getCodigo()
                    + " -> " + mayor.getCodigo());
        }
        // 6. RESULTADO de este subproblema
        return mayor;
    }
}
