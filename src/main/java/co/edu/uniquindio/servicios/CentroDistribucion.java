package co.edu.uniquindio.servicios;

import co.edu.uniquindio.comparadores.ComparadoresPaquete;
import co.edu.uniquindio.estructuras.ListaEnlazada;
import co.edu.uniquindio.modelo.Paquete;
import co.edu.uniquindio.modelo.Repartidor;

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

    private Map<String, Repartidor> repartidores = new LinkedHashMap<>();
    private Map<String, Repartidor> asignaciones = new HashMap<>();

    private ListaEnlazada<Paquete> listaEnlazada = new ListaEnlazada<>();

    public void registrarPaquete(Paquete paquete) {
        if(paquete ==null){
            throw new IllegalArgumentException("paquete no puede ser nulo.");
        }
        if(paquetesPorCodigo.containsKey(paquete.getCodigo())){
            throw new IllegalArgumentException("Paquete ya existe en el registro."+paquete.getCodigo());
        }
        paquetesPorCodigo.put(paquete.getCodigo(), paquete);
        registro.add(paquete);
        pendientes.add(paquete);
        colaLlegada.offer(paquete);
        colaPrioridad.offer(paquete);
        String destino = paquete.getDestino();
        municipios.add(destino);
        municipiosOrdenados.add(destino);
        List<Paquete> lista = paquetesPorMunicipio.get(destino);
        if (lista == null) {
            lista = new ArrayList<>();
            paquetesPorMunicipio.put(destino, lista);
        }
        lista.add(paquete);

    }

    public Paquete consultarPaquete(String codigo){
        return paquetesPorCodigo.get(codigo);
    }

    public boolean existePaquete(String codigo){
        return paquetesPorCodigo.containsKey(codigo);
    }

    public void registrarRepartidor(Repartidor repartidor) {
        if(repartidor == null){
            throw new IllegalArgumentException("repartidor no puede ser nulo.");
        }
        if (repartidores.containsKey(repartidor.getId())){
            throw new IllegalArgumentException("Repartidor ya existe en el registro."+repartidor.getId());
        }
        repartidores.put(repartidor.getId(), repartidor);
    }

    public Repartidor consultarRepartidor(String repartidorId){
        return repartidores.get(repartidorId);
    }


    public Paquete despacharProritario(){
        Paquete paquete = colaPrioridad.poll();
        if(paquete == null){
            colaLlegada.remove(paquete);
        }
        return paquete;
    }

    public Paquete verSiguientePrioritario(){
        return colaPrioridad.poll();
    }

    //Asignacion y entrega

    public void asignarPaquete(String codigo,String idRepartidor){
        Paquete paquete = paquetesPorCodigo.get(codigo);
        if(paquete == null){
            throw new IllegalArgumentException("Paquete no existe en el registro."+ codigo);
        }
        if(!pendientes.contains(paquete)){
            throw new IllegalArgumentException("El paquete " + codigo + " ya fue entregado");
        }
        if(asignaciones.containsKey(codigo)){
            throw new IllegalArgumentException("El paquete " + codigo + " ya está asignado");
        }
        Repartidor repartidor = asignaciones.get(idRepartidor);
        if(repartidor == null){
            throw new IllegalArgumentException("no existe el repartidor " + idRepartidor);
        }
        if(!repartidor.getZona().equalsIgnoreCase(paquete.getDestino())){
            throw new IllegalArgumentException("la zona del repartidor no corresponde al destino del paquete"+paquete.getDestino());
        }
        if(!repartidor.isDisponible()){
            throw new IllegalArgumentException("El repartidor"+idRepartidor+" no disponible");
        }
    }




}
