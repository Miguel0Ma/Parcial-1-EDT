package co.edu.uniquindio;

import co.edu.uniquindio.comparadores.ComparadoresPaquete;
import co.edu.uniquindio.algoritmos.DivideYVenceras;
import co.edu.uniquindio.modelo.Paquete;
import co.edu.uniquindio.modelo.Repartidor;
import co.edu.uniquindio.servicios.CentroDistribucion;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Programa principal de QuindíoExpress: menú con JOptionPane para usar y demostrar
 * cada operación del centro de distribución.
 */
public class Main {

    private static final String TITULO = "QuindíoExpress - Centro de distribución";

    private static final String[] OPCIONES = {
            "1. Registrar paquete",
            "2. Consultar paquete por código",
            "3. Registrar repartidor",
            "4. Consultar repartidor",
            "5. Ver repartidores",
            "6. Municipios (sin orden y ordenados)",
            "7. Paquetes de un municipio",
            "8. Pendientes por orden de llegada",
            "9. Ver siguiente por prioridad",
            "10. Despachar por prioridad",
            "11. Asignar paquete a repartidor (manual)",
            "12. Asignar paquete automáticamente",
            "13. Registrar entrega",
            "14. Ver historial de entregas",
            "15. Ordenar paquetes (natural / prioridad / peso / tiempo)",
            "16. Búsqueda binaria por código",
            "17. Peso total de un municipio (recursivo)",
            "18. Contar prioridad mínima (recursivo)",
            "19. Paquete de mayor peso (divide y vencerás)",
            "20. Demostración completa",
            "0. Salir"
    };

    public static void main(String[] args) {
        CentroDistribucion centro = new CentroDistribucion();
        cargarDatosIniciales(centro);
        menu(centro);
    }

    // =====================================================================
    //  MENÚ
    // =====================================================================

    private static void menu(CentroDistribucion centro) {
        boolean seguir = true;
        while (seguir) {
            Object elegida = JOptionPane.showInputDialog(null, "Elija una operación:", TITULO,
                    JOptionPane.PLAIN_MESSAGE, null, OPCIONES, OPCIONES[0]);
            if (elegida == null) {
                seguir = false;
            } else {
                int opcion = numeroDeOpcion(elegida.toString());
                if (opcion == 0) {
                    seguir = false;
                } else {
                    try {
                        ejecutar(centro, opcion);
                    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "No se pudo realizar la operación",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private static int numeroDeOpcion(String texto) {
        int punto = texto.indexOf('.');
        return Integer.parseInt(texto.substring(0, punto));
    }

    private static void ejecutar(CentroDistribucion centro, int opcion) {
        switch (opcion) {
            case 1:
                registrarPaquete(centro);
                break;
            case 2:
                consultarPaquete(centro);
                break;
            case 3:
                registrarRepartidor(centro);
                break;
            case 4:
                consultarRepartidor(centro);
                break;
            case 5:
                mostrar("Repartidores", textoRepartidores(centro));
                break;
            case 6:
                mostrar("Municipios", textoMunicipios(centro));
                break;
            case 7:
                paquetesDeMunicipio(centro);
                break;
            case 8:
                mostrar("Pendientes por orden de llegada", enumerar(centro.getPendientes()));
                break;
            case 9:
                verSiguiente(centro);
                break;
            case 10:
                despachar(centro);
                break;
            case 11:
                asignarManual(centro);
                break;
            case 12:
                asignarAutomatico(centro);
                break;
            case 13:
                registrarEntrega(centro);
                break;
            case 14:
                mostrar("Historial de entregas", textoHistorial(centro));
                break;
            case 15:
                ordenar(centro);
                break;
            case 16:
                busquedaBinaria(centro);
                break;
            case 17:
                pesoTotal(centro);
                break;
            case 18:
                contarPrioridad(centro);
                break;
            case 19:
                mayorPeso(centro);
                break;
            case 20:
                mostrar("Demostración completa", demostracion());
                break;
            default:
                break;
        }
    }

    // =====================================================================
    //  OPERACIONES DEL MENÚ (piden datos y muestran el resultado)
    // =====================================================================

    private static void registrarPaquete(CentroDistribucion centro) {
        String codigo = pedir("Código del paquete (ej. PQ731):");
        if (codigo == null) {
            return;
        }
        String destino = pedir("Destino (municipio):");
        if (destino == null) {
            return;
        }
        String peso = pedir("Peso en kg (mayor que 0):");
        if (peso == null) {
            return;
        }
        String prioridad = pedir("Prioridad (1 a 5). Déjela vacía para usar 3:");
        if (prioridad == null) {
            return;
        }
        String tiempo = pedir("Tiempo estimado en minutos (entero positivo):");
        if (tiempo == null) {
            return;
        }
        try {
            double pesoNumero = Double.parseDouble(peso.trim());
            int tiempoNumero = Integer.parseInt(tiempo.trim());
            Paquete nuevo;
            if (prioridad.trim().isEmpty()) {
                nuevo = new Paquete(codigo, destino, pesoNumero, tiempoNumero);
            } else {
                nuevo = new Paquete(codigo, destino, pesoNumero, Integer.parseInt(prioridad.trim()), tiempoNumero);
            }
            centro.registrarPaquete(nuevo);
            informar("Paquete registrado:\n" + nuevo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Peso, prioridad y tiempo deben ser números válidos.");
        }
    }

    private static void consultarPaquete(CentroDistribucion centro) {
        String codigo = pedir("Código del paquete a consultar:");
        if (codigo == null) {
            return;
        }
        Paquete p = centro.consultarPaquete(codigo.trim());
        if (p == null) {
            informar("El paquete " + codigo.trim() + " no existe.");
        } else {
            informar("Destino: " + p.getDestino() + "\nPeso: " + p.getPeso() + " kg\nPrioridad: "
                    + p.getPrioridad() + "\nTiempo estimado: " + p.getTiempoEstimado() + " min");
        }
    }

    private static void registrarRepartidor(CentroDistribucion centro) {
        String id = pedir("Identificación del repartidor:");
        if (id == null) {
            return;
        }
        String nombre = pedir("Nombre completo:");
        if (nombre == null) {
            return;
        }
        String zona = pedir("Zona (municipio):");
        if (zona == null) {
            return;
        }
        int disponible = JOptionPane.showConfirmDialog(null, "¿Está disponible?", TITULO, JOptionPane.YES_NO_OPTION);
        Repartidor r = new Repartidor(id, nombre, zona, disponible == JOptionPane.YES_OPTION);
        centro.registrarRepartidor(r);
        informar("Repartidor registrado:\n" + r);
    }

    private static void consultarRepartidor(CentroDistribucion centro) {
        String id = pedir("Identificación del repartidor:");
        if (id == null) {
            return;
        }
        Repartidor r = centro.consultarRepartidor(id.trim());
        if (r == null) {
            informar("El repartidor " + id.trim() + " no existe.");
        } else {
            informar(r.toString());
        }
    }

    private static void paquetesDeMunicipio(CentroDistribucion centro) {
        String municipio = elegirMunicipio(centro);
        if (municipio != null) {
            mostrar("Paquetes de " + municipio, enumerar(centro.paquetesDeMunicipio(municipio)));
        }
    }

    private static void verSiguiente(CentroDistribucion centro) {
        Paquete siguiente = centro.verSiguientePrioritario();
        if (siguiente == null) {
            informar("No hay paquetes en espera de despacho.");
        } else {
            informar("Siguiente a despachar (sin sacarlo):\n" + siguiente);
        }
    }

    private static void despachar(CentroDistribucion centro) {
        Paquete p = centro.despacharProritario();
        if (p == null) {
            informar("No hay paquetes en espera de despacho.");
        } else {
            informar("Paquete despachado:\n" + p);
        }
    }

    private static void asignarManual(CentroDistribucion centro) {
        String codigo = pedir("Código del paquete:");
        if (codigo == null) {
            return;
        }
        String id = pedir("Identificación del repartidor:");
        if (id == null) {
            return;
        }
        centro.asignarPaquete(codigo.trim(), id.trim());
        informar("Paquete " + codigo.trim() + " asignado al repartidor " + id.trim() + ".");
    }

    private static void asignarAutomatico(CentroDistribucion centro) {
        String codigo = pedir("Código del paquete a asignar:");
        if (codigo == null) {
            return;
        }
        informar(asignarAutomaticamenteYDescribir(centro, codigo.trim()));
    }

    private static void registrarEntrega(CentroDistribucion centro) {
        String codigo = pedir("Código del paquete entregado:");
        if (codigo == null) {
            return;
        }
        centro.registrarEntrega(codigo.trim());
        informar("Entrega registrada. Historial:\n" + centro.getHistorial());
    }

    private static void ordenar(CentroDistribucion centro) {
        String[] criterios = {"Orden natural (código)", "Prioridad (mayor a menor)",
                "Peso (mayor a menor)", "Tiempo estimado (menor a mayor)"};
        Object elegido = JOptionPane.showInputDialog(null, "Criterio de orden:", TITULO,
                JOptionPane.PLAIN_MESSAGE, null, criterios, criterios[0]);
        if (elegido == null) {
            return;
        }
        String criterio = elegido.toString();
        List<Paquete> lista;
        if (criterio.equals(criterios[0])) {
            lista = centro.ordenNatural();
        } else if (criterio.equals(criterios[1])) {
            lista = centro.ordenarPor(ComparadoresPaquete.PRIORIDAD_DESC);
        } else if (criterio.equals(criterios[2])) {
            lista = centro.ordenarPor(ComparadoresPaquete.PESO_DESC);
        } else {
            lista = centro.ordenarPor(ComparadoresPaquete.TIEMPO_ASC);
        }
        mostrar(criterio, enumerar(lista));
    }

    private static void busquedaBinaria(CentroDistribucion centro) {
        String codigo = pedir("Código a buscar (se busca en la lista ordenada por código):");
        if (codigo == null) {
            return;
        }
        List<Paquete> ordenada = centro.ordenNatural();
        Paquete p = centro.buscarBinaria(ordenada, codigo.trim());
        if (p == null) {
            informar("El paquete " + codigo.trim() + " no está en la lista ordenada.");
        } else {
            informar("Encontrado por búsqueda binaria:\n" + p);
        }
    }

    private static void pesoTotal(CentroDistribucion centro) {
        String municipio = elegirMunicipio(centro);
        if (municipio != null) {
            informar("Peso total de " + municipio + ": " + centro.pesoTotalMunicipios(municipio) + " kg");
        }
    }

    private static void contarPrioridad(CentroDistribucion centro) {
        String minima = pedir("Prioridad mínima (1 a 5):");
        if (minima == null) {
            return;
        }
        try {
            int valor = Integer.parseInt(minima.trim());
            informar("Paquetes con prioridad " + valor + " o superior: " + centro.contarPrioridadMinima(valor));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La prioridad mínima debe ser un número entero.");
        }
    }

    private static void mayorPeso(CentroDistribucion centro) {
        mostrar("Paquete de mayor peso (divide y vencerás)", textoMayorPesoConTraza(centro));
    }

    // =====================================================================
    //  LÓGICA QUE ARMA TEXTOS (sin ventanas, se puede probar sola)
    // =====================================================================

    static String enumerar(List<Paquete> paquetes) {
        if (paquetes.isEmpty()) {
            return "(sin paquetes)";
        }
        StringBuilder sb = new StringBuilder();
        int n = 1;
        for (Paquete p : paquetes) {
            sb.append(n).append(". ").append(p).append("\n");
            n++;
        }
        return sb.toString();
    }

    static String textoRepartidores(CentroDistribucion centro) {
        StringBuilder sb = new StringBuilder();
        for (Repartidor r : centro.getRepartidores()) {
            sb.append(r).append("\n");
        }
        return sb.toString();
    }

    static String textoMunicipios(CentroDistribucion centro) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sin repetir: ").append(centro.getMunicipios()).append("\n");
        sb.append("Ordenados:   ").append(centro.getMunicipiosOrdenados()).append("\n\n");
        for (String municipio : centro.getMunicipiosOrdenados()) {
            sb.append(municipio).append(" -> ").append(centro.paquetesDeMunicipio(municipio).size())
                    .append(" paquetes\n");
        }
        return sb.toString();
    }

    /** Recorre el historial con for-each: usa Iterable/Iterator sin tocar los nodos. */
    static String textoHistorial(CentroDistribucion centro) {
        if (centro.getHistorial().estaVacia()) {
            return "Todavía no hay entregas registradas.";
        }
        StringBuilder sb = new StringBuilder();
        int n = 1;
        for (Paquete p : centro.getHistorial()) {
            sb.append(n).append(". ").append(p).append("\n");
            n++;
        }
        sb.append("\nCadena: ").append(centro.getHistorial());
        return sb.toString();
    }

    /** Ejecuta divide y vencerás con traza y devuelve la traza más el resultado. */
    static String textoMayorPesoConTraza(CentroDistribucion centro) {
        PrintStream original = System.out;
        ByteArrayOutputStream captura = new ByteArrayOutputStream();
        Paquete mayor;
        try {
            System.setOut(new PrintStream(captura, true));
            mayor = DivideYVenceras.mayorPesoConTraza(centro.getRegistro());
        } finally {
            System.setOut(original);
        }
        return captura + "\nRESULTADO: " + mayor;
    }

    /**
     * Usa la asignación automática del centro y averigua a quién se le asignó,
     * comparando la disponibilidad de los repartidores antes y después.
     */
    static String asignarAutomaticamenteYDescribir(CentroDistribucion centro, String codigo) {
        List<String> disponiblesAntes = new ArrayList<>();
        for (Repartidor r : centro.getRepartidores()) {
            if (r.isDisponible()) {
                disponiblesAntes.add(r.getId());
            }
        }
        centro.asignarAutomaticamente(codigo);
        for (String id : disponiblesAntes) {
            Repartidor r = centro.consultarRepartidor(id);
            if (!r.isDisponible()) {
                return "Paquete " + codigo + " asignado automáticamente a:\n" + r;
            }
        }
        return "No había un repartidor disponible en la zona del paquete " + codigo + ".";
    }

    // =====================================================================
    //  DEMOSTRACIÓN COMPLETA (trabaja sobre su propio centro, con datos nuevos)
    // =====================================================================

    static String demostracion() {
        CentroDistribucion centro = new CentroDistribucion();
        cargarDatosIniciales(centro);
        StringBuilder sb = new StringBuilder();

        titulo(sb, "1. Datos cargados");
        sb.append("Paquetes: ").append(centro.getRegistro().size()).append("\n");
        sb.append("Municipios distintos: ").append(centro.getMunicipios().size()).append("\n");
        sb.append("Repartidores: ").append(centro.getRepartidores().size()).append("\n");

        titulo(sb, "2. Validaciones al registrar");
        sb.append(intentarRegistrar(centro, new Paquete("PQ731", "Armenia", 1.0, 3, 10), "código repetido")).append("\n");
        sb.append(intentarCrearPaquete("PQ999", "Armenia", -2.0, 3, 10, "peso negativo")).append("\n");
        sb.append(intentarCrearPaquete("PQ998", "Armenia", 2.0, 9, 10, "prioridad 9")).append("\n");
        sb.append(intentarCrearPaquete("PQ997", "  ", 2.0, 3, 10, "destino vacío")).append("\n");
        sb.append(intentarCrearPaquete("PQ996", "Armenia", 2.0, 3, 0, "tiempo cero")).append("\n");

        titulo(sb, "3. Consulta por código (HashMap)");
        sb.append("PQ731 -> ").append(centro.consultarPaquete("PQ731")).append("\n");
        sb.append("PQ000 -> ").append(centro.consultarPaquete("PQ000")).append("\n");

        titulo(sb, "4. Municipios");
        sb.append(textoMunicipios(centro));

        titulo(sb, "5. Paquetes de Armenia");
        sb.append(enumerar(centro.paquetesDeMunicipio("Armenia")));

        titulo(sb, "6. Primeros 5 pendientes por orden de llegada");
        sb.append(enumerar(centro.getPendientes().subList(0, 5)));

        titulo(sb, "7. Orden natural por código (primeros 5)");
        sb.append(enumerar(centro.ordenNatural().subList(0, 5)));
        titulo(sb, "8. Por prioridad, mayor a menor (primeros 5)");
        sb.append(enumerar(centro.ordenarPor(ComparadoresPaquete.PRIORIDAD_DESC).subList(0, 5)));
        titulo(sb, "9. Por peso, mayor a menor (primeros 5)");
        sb.append(enumerar(centro.ordenarPor(ComparadoresPaquete.PESO_DESC).subList(0, 5)));
        titulo(sb, "10. Por tiempo estimado, menor a mayor (primeros 5)");
        sb.append(enumerar(centro.ordenarPor(ComparadoresPaquete.TIEMPO_ASC).subList(0, 5)));

        titulo(sb, "11. Despacho prioritario (prioridad, luego tiempo, luego código)");
        sb.append("Siguiente sin sacar: ").append(centro.verSiguientePrioritario()).append("\n");
        for (int i = 1; i <= 3; i++) {
            sb.append("Despachado ").append(i).append(": ").append(centro.despacharProritario()).append("\n");
        }

        titulo(sb, "12. Asignación manual y reglas");
        centro.asignarPaquete("PQ731", "R001");
        sb.append("PQ731 asignado a R001 -> ").append(centro.consultarRepartidor("R001")).append("\n");
        sb.append(intentarAsignar(centro, "PQ942", "R001", "repartidor ocupado")).append("\n");
        sb.append(intentarAsignar(centro, "PQ942", "R002", "repartidor no disponible")).append("\n");
        sb.append(intentarAsignar(centro, "PQ942", "R003", "zona distinta al destino")).append("\n");
        sb.append(intentarAsignar(centro, "PQ731", "R002", "paquete ya asignado")).append("\n");

        titulo(sb, "13. Asignación automática");
        sb.append(asignarAutomaticamenteYDescribir(centro, "PQ105")).append("\n");

        titulo(sb, "14. Entregas e historial (lista enlazada propia)");
        centro.registrarEntrega("PQ105");
        centro.registrarEntrega("PQ731");
        sb.append(textoHistorial(centro)).append("\n");
        sb.append("Pendientes ahora: ").append(centro.getPendientes().size()).append("\n");
        sb.append("R001 vuelve a estar disponible: ").append(centro.consultarRepartidor("R001")).append("\n");

        titulo(sb, "15. Búsqueda binaria (lista ordenada por código)");
        sb.append("PQ731 -> ").append(centro.buscarBinaria(centro.ordenNatural(), "PQ731")).append("\n");
        sb.append("PQ000 -> ").append(centro.buscarBinaria(centro.ordenNatural(), "PQ000")).append("\n");

        titulo(sb, "16. Recursividad");
        sb.append("Peso total de Armenia: ").append(centro.pesoTotalMunicipios("Armenia")).append(" kg\n");
        sb.append("Paquetes con prioridad >= 4: ").append(centro.contarPrioridadMinima(4)).append("\n");

        titulo(sb, "17. Divide y vencerás: paquete de mayor peso");
        sb.append(textoMayorPesoConTraza(centro)).append("\n");

        return sb.toString();
    }

    private static void titulo(StringBuilder sb, String texto) {
        sb.append("\n=== ").append(texto).append(" ===\n");
    }

    private static String intentarRegistrar(CentroDistribucion centro, Paquete paquete, String motivo) {
        try {
            centro.registrarPaquete(paquete);
            return "[" + motivo + "] se aceptó (inesperado)";
        } catch (IllegalArgumentException e) {
            return "[" + motivo + "] rechazado: " + e.getMessage();
        }
    }

    private static String intentarCrearPaquete(String codigo, String destino, double peso, int prioridad,
                                               int tiempo, String motivo) {
        try {
            new Paquete(codigo, destino, peso, prioridad, tiempo);
            return "[" + motivo + "] se aceptó (inesperado)";
        } catch (IllegalArgumentException e) {
            return "[" + motivo + "] rechazado: " + e.getMessage();
        }
    }

    private static String intentarAsignar(CentroDistribucion centro, String codigo, String id, String motivo) {
        try {
            centro.asignarPaquete(codigo, id);
            return "[" + motivo + "] se aceptó (inesperado)";
        } catch (IllegalArgumentException e) {
            return "[" + motivo + "] rechazado: " + e.getMessage();
        }
    }

    // =====================================================================
    //  DATOS INICIALES (32 paquetes, 9 destinos, 10 repartidores)
    // =====================================================================

    static void cargarDatosIniciales(CentroDistribucion centro) {
        // código, destino, peso (kg), prioridad, tiempo (min): llegan mezclados y con códigos no consecutivos
        Object[][] paquetes = {
                {"PQ731", "Armenia", 4.5, 5, 45},
                {"PQ105", "Salento", 3.0, 5, 25},
                {"PQ942", "Armenia", 7.2, 4, 20},
                {"PQ318", "Calarcá", 8.2, 4, 30},
                {"PQ567", "Salento", 6.4, 2, 40},
                {"PQ829", "Armenia", 2.3, 3, 25},
                {"PQ584", "Montenegro", 18.7, 1, 65},
                {"PQ409", "Quimbaya", 5.1, 5, 42},
                {"PQ677", "Circasia", 6.0, 4, 22},
                {"PQ222", "Filandia", 4.0, 3, 52},
                {"PQ513", "La Tebaida", 13.2, 4, 33},
                {"PQ346", "Génova", 15.6, 2, 90},
                {"PQ204", "Armenia", 9.8, 2, 50},
                {"PQ472", "Calarcá", 14.5, 3, 45},
                {"PQ248", "Salento", 1.8, 4, 35},
                {"PQ720", "Montenegro", 7.7, 3, 35},
                {"PQ836", "Quimbaya", 10.3, 2, 70},
                {"PQ391", "Circasia", 11.4, 1, 48},
                {"PQ780", "Filandia", 8.8, 2, 58},
                {"PQ068", "La Tebaida", 2.9, 5, 27},
                {"PQ789", "Génova", 3.7, 4, 80},
                {"PQ615", "Armenia", 3.1, 5, 30},
                {"PQ956", "Calarcá", 2.6, 5, 20},
                {"PQ893", "Salento", 5.5, 3, 60},
                {"PQ263", "Montenegro", 3.4, 4, 28},
                {"PQ152", "Quimbaya", 2.2, 3, 38},
                {"PQ945", "Circasia", 1.5, 5, 15},
                {"PQ901", "La Tebaida", 7.1, 3, 44},
                {"PQ377", "Armenia", 12.0, 1, 55},
                {"PQ131", "Calarcá", 4.9, 2, 50},
                {"PQ420", "Armenia", 6.0, 3, 30},
                {"PQ850", "Armenia", 9.1, 5, 18}
        };
        for (Object[] f : paquetes) {
            centro.registrarPaquete(new Paquete((String) f[0], (String) f[1], (Double) f[2],
                    (Integer) f[3], (Integer) f[4]));
        }

        // id, nombre, zona, disponible: dos en Armenia (uno no disponible) para mostrar la regla
        Object[][] repartidores = {
                {"R001", "Ana Gómez", "Armenia", true},
                {"R002", "Luis Pérez", "Armenia", false},
                {"R003", "Marta Ríos", "Salento", true},
                {"R004", "Carlos Díaz", "Calarcá", true},
                {"R005", "Sofía Mejía", "Montenegro", true},
                {"R006", "Julián Ortiz", "Quimbaya", true},
                {"R007", "Paula Vélez", "Circasia", true},
                {"R008", "Andrés Cano", "Filandia", false},
                {"R009", "Diana Torres", "La Tebaida", true},
                {"R010", "Mateo Rojas", "Génova", true}
        };
        for (Object[] f : repartidores) {
            centro.registrarRepartidor(new Repartidor((String) f[0], (String) f[1], (String) f[2], (Boolean) f[3]));
        }
    }

    // =====================================================================
    //  AYUDAS DE INTERFAZ
    // =====================================================================

    private static String pedir(String mensaje) {
        return JOptionPane.showInputDialog(null, mensaje, TITULO, JOptionPane.QUESTION_MESSAGE);
    }

    private static void informar(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, TITULO, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mostrar(String titulo, String texto) {
        JTextArea area = new JTextArea(texto, 22, 78);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setCaretPosition(0);
        JOptionPane.showMessageDialog(null, new JScrollPane(area), titulo, JOptionPane.PLAIN_MESSAGE);
    }

    private static String elegirMunicipio(CentroDistribucion centro) {
        Set<String> municipios = centro.getMunicipiosOrdenados();
        if (municipios.isEmpty()) {
            informar("Todavía no hay municipios registrados.");
            return null;
        }
        Object[] opciones = municipios.toArray();
        Object elegido = JOptionPane.showInputDialog(null, "Municipio:", TITULO,
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (elegido == null) {
            return null;
        }
        return elegido.toString();
    }
}