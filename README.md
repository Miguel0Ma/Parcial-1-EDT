# Parcial 1 – Gestión de operaciones de un centro de distribución (QuindíoExpress)

Aplicación en Java que organiza la información de una jornada de trabajo de un centro de distribución:
registro y consulta de paquetes y repartidores, clasificación por municipio, despacho por orden de llegada y por
prioridad, asignación a repartidores, registro de entregas e historial, y varios análisis (recursividad,
búsqueda binaria y divide y vencerás).

Curso: Estructuras de Datos – Universidad del Quindío.

## Integrantes

| Nombre | Aportes |
|---|---|
| Santiago Cifuentes Vega | Pruebas unitarias y clases |
| Miguel Ángel Maya Colorado | Clases y métodos |

## Requisitos

- Java 25 (configurado en `pom.xml`)
- Maven 3.9 o superior
- JUnit 5.11.4 (se descarga automáticamente con Maven)

## Cómo ejecutar

Desde IntelliJ IDEA: abrir el proyecto como proyecto Maven y ejecutar `co.edu.uniquindio.Main`.

El programa abre un menú con `JOptionPane` y carga datos de prueba definidos directamente en Java
(32 paquetes, 9 destinos y 10 repartidores). La opción **20. Demostración completa** ejecuta todas las operaciones
sobre un centro independiente, sin alterar el estado del menú.

Para ejecutar las pruebas:

```bash
mvn test
```

## Estructura del proyecto

```
src/main/java/co/edu/uniquindio/
├── Main.java                         Menú con JOptionPane y datos iniciales
├── modelo/
│   ├── Paquete.java                  Comparable<Paquete>: orden natural por código
│   └── Repartidor.java
├── comparadores/
│   └── ComparadoresPaquete.java      Comparator: prioridad, peso, tiempo y despacho prioritario
├── estructuras/
│   ├── Coleccion.java                Interfaz genérica con el contrato común
│   └── ListaEnlazada.java            Lista simplemente enlazada propia, genérica e Iterable
├── algoritmos/
│   ├── Recursivos.java               Peso total por municipio y conteo por prioridad mínima
│   ├── BusquedaBinaria.java          Búsqueda por código en una lista ordenada
│   └── DivideYVenceras.java          Paquete de mayor peso
└── servicios/
    └── CentroDistribucion.java       Lógica del sistema

src/test/java/co/edu/uniquindio/      Pruebas unitarias con JUnit 5
```

## Decisiones de diseño: estructura elegida para cada necesidad

| Necesidad | Estructura | Operación principal | Complejidad |
|---|---|---|---|
| Paquetes en orden de registro | `ArrayList<Paquete>` | agregar al final / recorrer | O(1) amortizado / O(n) |
| Consulta por código | `HashMap<String, Paquete>` | `get` | O(1) esperado |
| Municipios sin duplicados | `HashSet<String>` | `add` | O(1) esperado |
| Municipios ordenados y sin duplicados | `TreeSet<String>` | `add` | O(log n) |
| Paquetes de cada municipio | `HashMap<String, List<Paquete>>` | `get` | O(1) esperado |
| Atención por orden de llegada | `ArrayDeque` como `Queue` | `offer` / `poll` | O(1) |
| Despacho por prioridad | `PriorityQueue` con `ComparadorDespachoPrioritario` | `offer` / `poll` | O(log n) |
| Historial de entregas | `ListaEnlazada<Paquete>` propia | agregar al final | O(1) (mantiene referencia a la cola) |

Una misma estructura no sirve para todas las operaciones: `HashMap` evita recorrer todos los paquetes para
consultar un código, pero no conserva el orden de llegada; por eso se conserva además una lista con el orden de registro.
El costo de esta decisión es memoria adicional, ya que cada paquete se referencia desde varias estructuras.

### Lista enlazada propia

- No utiliza ninguna colección del Java Collections Framework para almacenar elementos: solo nodos con
  un valor y una referencia al siguiente.
- Es genérica (`ListaEnlazada<T>`) y no depende de `Paquete`.
- Implementa `Coleccion<T>` (agregar, eliminar, obtener, tamaño y vacía) e `Iterable<T>`.
- El recorrido se hace con un `Iterator` interno: el código externo nunca accede a los nodos.
- Casos de borde cubiertos: lista vacía, un solo elemento, eliminar primero, intermedio, último e inexistente.

Costos: agregar O(1), eliminar O(n), obtener O(n), tamaño y vacía O(1).

## Algoritmos

| Algoritmo | Clase | Tiempo | Espacio |
|---|---|---|---|
| Peso total de un municipio (recursivo) | `Recursivos.pesoTotalMunicipio` | O(n) | O(n) por la pila de llamadas |
| Conteo por prioridad mínima (recursivo) | `Recursivos.contarPrioridadMinina` | O(n) | O(n) por la pila de llamadas |
| Búsqueda binaria por código | `BusquedaBinaria.buscarPorCodigo` | O(log n) | O(1) |
| Paquete de mayor peso (divide y vencerás) | `DivideYVenceras.mayorPeso` | O(n), con T(n) = 2T(n/2) + O(1) | O(log n) |

- **Recursividad:** sin ciclos `for`, `while` ni `do-while`. Caso base: el índice llega al final de la lista.
- **Búsqueda binaria:** la lista debe estar ordenada por código. Esa es la precondición del algoritmo.
  Ordenar una lista cuesta O(n log n), por lo que solo conviene cuando los datos ya vienen ordenados
  o cuando se hacen muchas búsquedas sobre la misma lista.
- **Divide y vencerás:** caso base de un solo elemento, división del rango por la mitad, subproblema izquierdo y derecho,
  combinación quedándose con el más pesado. `mayorPesoConTraza` imprime cada etapa.

## Reglas de negocio implementadas

- No existen dos paquetes con el mismo código ni dos repartidores con la misma identificación.
- Peso mayor que cero, prioridad entre 1 y 5 (3 por defecto), tiempo estimado mayor que cero y destino no vacío.
  El enunciado menciona el rango 0–5 en un apartado y 1–5 en otro; se implementó 1–5.
- Un repartidor no disponible no recibe asignaciones, y su zona debe coincidir con el destino del paquete.
- Despacho prioritario: mayor prioridad, luego menor tiempo estimado y por último código alfabético.
- El historial conserva el orden real de las entregas y usa la lista enlazada propia.
- Orden natural de `Paquete` por código (`Comparable`) y criterios alternativos por prioridad, peso y tiempo (`Comparator`).

## Pruebas

Las pruebas unitarias (JUnit 5) cubren el modelo, la lista enlazada, los comparadores, los tres algoritmos
y el servicio `CentroDistribucion`. Se ejecutan con `mvn test`.

## Uso de inteligencia artificial

El uso de herramientas de IA está registrado en la Parte D del documento PDF de entrega
(bitácora y caso de respuesta cuestionable).
