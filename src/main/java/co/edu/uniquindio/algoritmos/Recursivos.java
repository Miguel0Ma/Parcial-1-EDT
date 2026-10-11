package co.edu.uniquindio.algoritmos;

import co.edu.uniquindio.modelo.Paquete;

import java.util.List;

public class Recursivos {
    private Recursivos() {}

    public static double pesoTotalMunicipio(List<Paquete> paquetes,String destino, int indice) {
        //caso base
        if(indice >= paquetes.size()) {
            return 0.0;
        }
        //caso recuersivo
        Paquete actual=paquetes.get(indice);
        double restoDeLaLista = pesoTotalMunicipio(paquetes, destino, indice +1);
        if(actual.getDestino().equalsIgnoreCase(destino)) {
            return actual.getPeso() + restoDeLaLista;

        }
        return restoDeLaLista;
    }
}
