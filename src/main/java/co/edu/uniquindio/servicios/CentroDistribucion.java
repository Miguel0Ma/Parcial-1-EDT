package co.edu.uniquindio.servicios;

import co.edu.uniquindio.comparadores.ComparadoresPaquete;
import co.edu.uniquindio.modelo.Paquete;

import java.util.*;

public class CentroDistribucion {
    private List<Paquete> registro = new ArrayList<>();
    private List<Paquete> pendientes = new ArrayList<>();
    private Map<String, Paquete> paquetesPorCodigo = new HashMap<>();
    private Set<String> municipios = new HashSet<>();
    private Set<String> municipiosOrdenados = new TreeSet<>();
    private Map<String, List<Paquete>> paquetesPorMunicipio = new HashMap<>();
    private Queue<Paquete> colaLlegada = new ArrayDeque<>();
    private PriorityQueue<Paquete> colaPrioridad = new PriorityQueue<>(ComparadoresPaquete.DESPACHO_PRIORITARIO);

}
