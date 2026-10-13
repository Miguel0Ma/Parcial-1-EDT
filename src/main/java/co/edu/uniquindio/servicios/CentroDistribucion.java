package co.edu.uniquindio.servicios;

import co.edu.uniquindio.algoritmos.BusquedaBinaria;
import co.edu.uniquindio.algoritmos.DivideYVenceras;
import co.edu.uniquindio.algoritmos.Recursivos;
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
    private Map<String, String> asignaciones = new HashMap<>();

    private ListaEnlazada<Paquete> historial = new ListaEnlazada<>();

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
        if(paquete != null){
            colaLlegada.remove(paquete);
        }
        return paquete;
    }

    public Paquete verSiguientePrioritario(){
        return colaPrioridad.peek();
    }

    // Atención por orden de llegada

    /**
     * Atiende al paquete que lleva más tiempo esperando (el primero en registrarse
     * entre los que siguen en espera). Devuelve null si no hay ninguno.
     * Costo: poll O(1) + quitar de la cola prioritaria O(n).
     */
    public Paquete atenderSiguientePorLlegada(){
        Paquete paquete = colaLlegada.poll();
        if(paquete != null){
            colaPrioridad.remove(paquete);      // ya fue atendido: no puede despacharse otra vez
        }
        return paquete;
    }

    /** Consulta quién sería el siguiente por orden de llegada, sin sacarlo. */
    public Paquete verSiguientePorLlegada(){
        return colaLlegada.peek();
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
        Repartidor repartidor = repartidores.get(idRepartidor);
        if(repartidor == null){
            throw new IllegalArgumentException("no existe el repartidor " + idRepartidor);
        }
        if(!repartidor.getZona().equalsIgnoreCase(paquete.getDestino())){
            throw new IllegalArgumentException("la zona del repartidor no corresponde al destino del paquete"+paquete.getDestino());
        }
        if(!repartidor.isDisponible()){
            throw new IllegalArgumentException("El repartidor"+idRepartidor+" no disponible");
        }
        repartidor.setDisponible(false);
        asignaciones.put(codigo, idRepartidor);
        colaLlegada.remove(paquete);            // por si no había sido despachado antes
        colaPrioridad.remove(paquete);
    }

    /**
     * Asigna el paquete al primer repartidor disponible cuya zona coincide con el destino.
     *
     * @return el repartidor asignado, o null si no hay ninguno disponible en esa zona
     */
    public Repartidor asignarAutomaticamente(String codigo){
        Paquete paquete = paquetesPorCodigo.get(codigo);

        if(paquete == null){
            throw new IllegalArgumentException("Paquete no existe en el registro."+ codigo);
        }
        for (Repartidor r:repartidores.values()){
            if (r.isDisponible()&&r.getZona().equalsIgnoreCase(paquete.getDestino())){
                asignarPaquete(codigo,r.getId());
                return r;                       // ya se asignó: no seguir buscando
            }
        }
        return null;                            // ningún repartidor disponible en la zona
    }

    public void registrarEntrega(String codigo){
        Paquete paquete = paquetesPorCodigo.get(codigo);
        if(paquete == null){
            throw new IllegalArgumentException("Paquete no existe en el registro."+ codigo);
        }
        String idRepartidor = asignaciones.get(codigo);
        if(idRepartidor == null){
            throw new IllegalArgumentException("El paquete"+codigo+ "no esta asignado a ningun repartirdor");
        }
        pendientes.remove(paquete);
        asignaciones.remove(codigo);
        repartidores.get(idRepartidor).setDisponible(true);
        historial.agregar(paquete);
    }

    public List<Paquete> ordenNatural(){
        List<Paquete> lista = new ArrayList<>(registro);
        Collections.sort(lista);
        return lista;
    }
    public List<Paquete> ordenarPor(Comparator<Paquete> criterio){
        List<Paquete> copia = new ArrayList<>(registro);
        copia.sort(criterio);
        return copia;
    }

    public double pesoTotalMunicipios(String municipio){
        return Recursivos.pesoTotalMunicipio(registro,municipio,0);

    }
    public int contarPrioridadMinima(int minima){
        return Recursivos.contarPrioridadMinina(registro,minima,0);

    }

    public Paquete buscarBinaria(List<Paquete> listaOrdenada, String codigo){
        int posicion= BusquedaBinaria.buscarPorCodigo(listaOrdenada, codigo);
        if(posicion >=0){
            return listaOrdenada.get(posicion);
        }else {
            return null;
        }
    }

    public Paquete paqueteMayorPeso(){
        return DivideYVenceras.mayorPeso(registro);
    }
    public List<Paquete> getRegistro() {
        return Collections.unmodifiableList(registro);
    }

    public List<Paquete> getPendientes() {
        return Collections.unmodifiableList(pendientes);
    }

    public Set<String> getMunicipios() {
        return Collections.unmodifiableSet(municipios);
    }

    public Set<String> getMunicipiosOrdenados() {
        return Collections.unmodifiableSet(municipiosOrdenados);
    }

    public List<Paquete> paquetesDeMunicipio(String municipio) {
        List<Paquete> lista = paquetesPorMunicipio.get(municipio);
        return lista == null ? Collections.<Paquete>emptyList() : Collections.unmodifiableList(lista);
    }

    public Collection<Repartidor> getRepartidores() {
        return Collections.unmodifiableCollection(repartidores.values());
    }

    public ListaEnlazada<Paquete> getHistorial() {
        return historial;
    }



}