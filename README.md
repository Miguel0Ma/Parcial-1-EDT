# QuindíoExpress — Parcial 1 de Estructuras de Datos

Sistema en Java para gestionar las operaciones de una jornada en el centro de distribución de
QuindíoExpress: registrar paquetes y repartidores, consultarlos, agruparlos por municipio,
despacharlos (por orden de llegada o por prioridad), asignarlos a repartidores, registrar sus
entregas en un historial y hacer análisis (recursividad, búsqueda binaria, divide y vencerás).

Solo usa los temas vistos en el curso: Java, recursividad, pila de llamadas, búsqueda binaria,
divide y vencerás, análisis básico de complejidad, Java Collections Framework (`List`, `Set`,
`Map`, `Queue`, `PriorityQueue`), clases e interfaces genéricas, `Iterable`/`Iterator`, lista
simplemente enlazada propia, `Comparable` y `Comparator`.

---

## 1. Cómo compilar y ejecutar

Requiere Java 11 o superior. Los archivos están en UTF-8 (hay tildes), por eso se indica la codificación.

```bash
mkdir out
javac -encoding UTF-8 -d out $(find src -name "*.java")

java -Dfile.encoding=UTF-8 -cp out quindioexpress.Main                  # demostración completa
java -Dfile.encoding=UTF-8 -cp out quindioexpress.PruebasListaEnlazada  # casos borde de la lista propia
```

También se puede abrir la carpeta `src` como proyecto en IntelliJ, Eclipse o NetBeans y ejecutar
`Main`. Si la consola muestra caracteres raros en las tildes, configurar la codificación UTF-8.

---

## 2. Estructura del proyecto

```
src/quindioexpress/
├── estructuras/
│   ├── Coleccion.java            interfaz genérica (el contrato)
│   └── ListaEnlazada.java        lista simplemente enlazada propia (implementa el contrato)
├── modelo/
│   ├── Paquete.java              Comparable: orden natural por código
│   └── Repartidor.java
├── comparadores/
│   └── ComparadoresPaquete.java  criterios alternativos de ordenamiento (Comparator)
├── algoritmos/
│   ├── Recursivos.java           peso total por municipio, conteo por prioridad
│   ├── BusquedaBinaria.java      búsqueda por código en una lista ordenada
│   └── DivideYVenceras.java      paquete de mayor peso
├── servicio/
│   └── CentroDistribucion.java   reúne las colecciones y aplica las reglas del negocio
├── Main.java                     demostración con los datos mínimos del enunciado
└── PruebasListaEnlazada.java     pruebas de los casos borde de la lista propia
```

Idea general: las clases de `estructuras`, `modelo`, `comparadores` y `algoritmos` son piezas
independientes y reutilizables. `CentroDistribucion` las combina y es la única que conoce las
reglas del negocio. `Main` solo lo usa para mostrar el funcionamiento.

---

## 3. Cómo funciona una jornada (flujo de un paquete)

```
 registrarPaquete ──► [en cola de despacho] ──► despachar (por llegada o prioridad)
                              │                              │
                              └──────── asignarPaquete ◄─────┘
                                  (repartidor disponible y de la zona del destino)
                                              │
                                              ▼
                                      [asignado, repartidor ocupado]
                                              │
                                       registrarEntrega
                                              │
                                              ▼
                  sale de pendientes ─► historial (lista propia) ─► repartidor disponible
```

1. **Registro** (`registrarPaquete`). Se valida que el código no exista. Luego el paquete se guarda
   en todas las estructuras: `registro` y `pendientes` (en orden de llegada), `paquetesPorCodigo`
   (consulta por código), las dos colas de despacho, los conjuntos de municipios y el grupo de su
   municipio.
2. **Consulta** (`consultarPaquete`, `existePaquete`). Búsqueda directa por código en un `HashMap`,
   sin recorrer la lista.
3. **Despacho**. Hay dos modalidades, cada una con su cola:
   - `despacharPorLlegada()`: primero el que llegó antes (cola FIFO).
   - `despacharPrioritario()`: primero el de mayor prioridad; si empatan, el de menor tiempo
     estimado; si siguen empatados, el de código menor (alfabéticamente).

   Un paquete está en las dos colas a la vez. Cuando se saca de una, se quita también de la otra,
   para que no se despache dos veces.
4. **Asignación** (`asignarPaquete`, `asignarAutomaticamente`). Antes de asignar se valida que el
   paquete exista, esté pendiente y no esté ya asignado, que el repartidor exista, que su zona sea
   el destino del paquete y que esté disponible. Al asignar, el repartidor pasa a ocupado.
5. **Entrega** (`registrarEntrega`). El paquete debe estar asignado. Sale de los pendientes, se agrega
   al final del historial (la lista propia) y el repartidor vuelve a estar disponible.
6. **Análisis**. Peso total por municipio y conteo por prioridad (recursivos), búsqueda binaria por
   código y paquete de mayor peso (divide y vencerás).

---

## 4. Qué hace cada clase

### `estructuras`

**`Coleccion<T>`** es la interfaz genérica con el contrato: `agregar`, `eliminar`, `obtener`,
`tamano` y `estaVacia`. Define *qué* se puede hacer; la lista define *cómo*.

**`ListaEnlazada<T>`** implementa `Coleccion<T>` e `Iterable<T>`. Está formada por nodos privados,
cada uno con un valor y una referencia al siguiente:

```
cabeza ─► [PQ105 | •]──►[PQ942 | •]──►[PQ731 | null] ◄─ cola
```

- Guarda `cabeza` (primer nodo), `cola` (último nodo) y `tamano`.
- `agregar` enlaza al final usando `cola`: cuesta O(1) y no recorre la lista.
- `eliminar` busca el nodo guardando también el anterior, y re-enlaza según el caso:
  - primero o único: `cabeza` pasa al siguiente;
  - intermedio o último: el anterior salta al siguiente;
  - si se elimina el último, `cola` retrocede al anterior;
  - si el elemento no existe (o la lista está vacía), devuelve `false` sin cambiar nada.
- `obtener(i)` recorre `i` nodos (O(n)) y lanza `IndexOutOfBoundsException` si `i` no es válido.
- `iterator()` devuelve un iterador nuevo cada vez (`hasNext`, `next`). El código externo recorre la
  lista con `for-each` sin conocer los nodos, y dos iteradores no se estorban entre sí.
- No usa ninguna colección de Java para guardar elementos y no admite elementos nulos.

### `modelo`

**`Paquete`** (`codigo`, `destino`, `peso`, `prioridad`, `tiempoEstimado`).
El constructor valida las reglas: peso > 0, prioridad entre 1 y 5, tiempo > 0, y código y destino
no nulos ni vacíos; si algo falla lanza `IllegalArgumentException`. Hay un segundo constructor sin
prioridad que usa 3. Implementa `Comparable<Paquete>` (`compareTo` por código ascendente).
`equals` y `hashCode` usan solo el código.

**`Repartidor`** (`id`, `nombre`, `zona`, `disponible`). Valida que los datos no estén vacíos.
`disponible` es lo único que cambia con el tiempo.

### `comparadores`

**`ComparadoresPaquete`** tiene criterios de ordenamiento reutilizables, independientes del orden
natural:

| Criterio | Orden |
|---|---|
| `PRIORIDAD_DESC` | prioridad, de mayor a menor |
| `PESO_DESC` | peso, de mayor a menor |
| `TIEMPO_ASC` | tiempo estimado, de menor a mayor |
| `DESPACHO_PRIORITARIO` | prioridad desc, luego tiempo asc, luego código asc |

`DESPACHO_PRIORITARIO` es el que usa la `PriorityQueue`. Ordenar con cualquiera de ellos no cambia
el orden natural del `Paquete`.

### `algoritmos`

**`Recursivos`** (sin `for`, `while` ni `do-while`):

- `pesoTotalPorMunicipio(lista, destino, indice)`
  - Caso base: `indice >= tamaño` → devuelve 0.
  - Caso recursivo: llama con `indice + 1` y suma el peso del paquete actual si su destino coincide.
  - Se avanza hacia el caso base aumentando el índice en cada llamada.
- `contarPrioridadMinima(lista, minima, indice)`: misma idea, suma 1 cuando `prioridad >= minima`.

**`BusquedaBinaria.buscarPorCodigo(lista, codigo)`**: compara con el elemento del medio y descarta
la mitad donde no puede estar. Devuelve la posición o -1.
*Precondición: la lista debe estar ordenada por código.* Sobre datos desordenados el resultado no
es confiable.

**`DivideYVenceras.mayorPeso(lista)`** muestra las etapas de la estrategia:

1. caso base: un rango con un solo paquete es el mayor;
2. división por la mitad (`medio`);
3. subproblema izquierdo `[inicio..medio]`;
4. subproblema derecho `[medio+1..fin]`;
5. combinación: se queda con el más pesado de los dos resultados;
6. resultado final: el que devuelve la llamada con todo el rango.

`mayorPesoConTraza` hace lo mismo e imprime cada paso. Con los pesos del enunciado
`4.5, 12.0, 3.2, 18.7, 7.4, 5.1` el árbol de llamadas es:

```
[0..5]
├── [0..2]
│   ├── [0..1] ── [0] 4.5   [1] 12.0  → 12.0
│   └── [2] 3.2                       → 12.0
└── [3..5]
    ├── [3..4] ── [3] 18.7  [4] 7.4   → 18.7
    └── [5] 5.1                       → 18.7
                              resultado: 18.7
```

### `servicio`

**`CentroDistribucion`** guarda estas colecciones (elegidas en la Parte A):

| Campo | Estructura | Para qué sirve |
|---|---|---|
| `registro` | `ArrayList<Paquete>` | todos los paquetes en el orden exacto de registro (auditoría); nunca se quita ni se reordena |
| `pendientes` | `ArrayList<Paquete>` | los aún no entregados, en orden de registro |
| `paquetesPorCodigo` | `HashMap<String, Paquete>` | consulta por código y validación de códigos únicos |
| `municipios` | `HashSet<String>` | municipios con paquetes, sin repetir |
| `municipiosOrdenados` | `TreeSet<String>` | los mismos, siempre ordenados alfabéticamente (con `Collator` es-CO para las tildes) |
| `paquetesPorMunicipio` | `HashMap<String, List<Paquete>>` | relación municipio → sus paquetes |
| `colaLlegada` | `Queue` (`ArrayDeque`) | despacho por orden de llegada |
| `colaPrioridad` | `PriorityQueue` | despacho prioritario |
| `repartidores` | `LinkedHashMap<String, Repartidor>` | repartidores por identificación |
| `asignaciones` | `HashMap<String, String>` | código de paquete → id de repartidor |
| `historial` | `ListaEnlazada<Paquete>` | entregas en el orden real de entrega |

Los ordenamientos (`ordenNatural`, `ordenarPor(criterio)`) trabajan siempre sobre una **copia**, así
el orden de registro nunca se altera. Los métodos `get...` devuelven vistas de solo lectura.

---

## 5. Reglas del enunciado y dónde se cumplen

| Regla | Dónde se cumple |
|---|---|
| Código de paquete único | `registrarPaquete` consulta `paquetesPorCodigo` |
| Identificación de repartidor única | `registrarRepartidor` consulta `repartidores` |
| Peso > 0, prioridad 1–5, tiempo > 0, destino no vacío | constructor de `Paquete` |
| Repartidor no disponible no recibe asignaciones | `asignarPaquete` (`IllegalStateException`) |
| La zona del repartidor debe ser el destino del paquete | `asignarPaquete` (`IllegalArgumentException`) |
| Orden de llegada | `registro` y `colaLlegada` |
| Despacho prioritario (prioridad, tiempo, código) | `ComparadoresPaquete.DESPACHO_PRIORITARIO` en `colaPrioridad` |
| El historial conserva el orden de entrega y usa la lista propia | `registrarEntrega` → `historial.agregar` |
| Lista propia genérica, con `Iterable` e `Iterator` | `ListaEnlazada<T>` |
| Operaciones recursivas sin ciclos | `Recursivos` |
| Divide y vencerás con etapas visibles | `DivideYVenceras` |

---

## 6. Complejidad de las operaciones principales

n = paquetes, m = municipios, r = repartidores.

| Operación | Tiempo | Nota |
|---|---|---|
| `registrarPaquete` | O(log n) | la `PriorityQueue` y el `TreeSet` dominan; el resto es O(1) |
| `consultarPaquete` / `existePaquete` | O(1) promedio | `HashMap` |
| `despacharPorLlegada` | O(n) | `poll` es O(1), pero quitarlo de la otra cola es O(n) |
| `despacharPrioritario` | O(n) | `poll` es O(log n), pero quitarlo de la otra cola es O(n) |
| `asignarPaquete` | O(n) | `pendientes.contains` y quitar de las colas |
| `registrarEntrega` | O(n) | quitar de `pendientes`; agregar al historial es O(1) |
| `ordenNatural` / `ordenarPor` | O(n log n) | copia y ordenamiento |
| `pesoTotalMunicipio`, `contarPrioridadMinima` | O(n) tiempo, O(n) espacio | pila de llamadas de n niveles |
| `BusquedaBinaria.buscarPorCodigo` | O(log n) tiempo, O(1) espacio | con la lista ya ordenada |
| `CentroDistribucion.buscarBinaria` | O(n log n) | ordena una copia antes de buscar |
| `DivideYVenceras.mayorPeso` | O(n) tiempo, O(log n) espacio | pila de profundidad log n |
| `ListaEnlazada.agregar` | O(1) | gracias a la referencia `cola` |
| `ListaEnlazada.eliminar` / `obtener` | O(n) | recorre desde la cabeza |

---

## 7. Qué muestra `Main`

El programa carga 32 paquetes (8 destinos, códigos no consecutivos, varios por municipio) y
9 repartidores, y ejecuta seis secciones:

1. **Registro, validaciones y consulta**: consulta por código y rechazo de código duplicado, peso
   inválido, prioridad fuera de rango, tiempo inválido, destino vacío y repartidor duplicado.
2. **Municipios**: conjunto sin repetir, versión ordenada y paquetes agrupados por municipio.
3. **Orden natural y criterios alternativos**: por código, prioridad, peso y tiempo, comprobando que
   el orden de registro no cambia.
4. **Despacho, asignación, entrega e historial**: despacha por llegada y por prioridad, asigna a
   repartidores (con casos de error por zona y por disponibilidad), registra entregas en orden
   distinto al de despacho y recorre el historial con `for-each`.
5. **Algoritmos**: peso total hacia Armenia, conteo de prioridad ≥ 4, búsqueda binaria y mayor peso.
6. **Ejemplos del enunciado**, con el resultado esperado al lado: despacho `PQ105, PQ731, PQ942,
   PQ420`; peso total de Armenia `14.0`; prioridad ≥ 4 sobre `5,2,4,1,5,3,4` da `4`; búsqueda de
   `PQ731` en la posición `5`; mayor peso `18.7` con la traza de divide y vencerás.

`PruebasListaEnlazada` revisa los casos borde de la lista propia: vacía, un elemento, eliminar
primero, intermedio, último e inexistente, agregar tras eliminar el último, iteradores
independientes y uso con otro tipo (`Integer`).

---

## 8. Decisiones y supuestos

- **Prioridad**: valores de 1 a 5 (mayor número = más urgente) y 3 por defecto. El enunciado menciona
  0–5 en un lugar y 1–5 en las reglas; se adoptó 1–5.
- **Código único de la jornada**: `paquetesPorCodigo` no se limpia al entregar, por eso un código
  ya entregado no puede registrarse otra vez.
- **Despachar, asignar y entregar** son tres pasos distintos. Despachar saca el paquete de las colas;
  asignar lo vincula a un repartidor; entregar lo cierra. Asignar sin despachar también es válido
  (lo quita de las colas).
- **Un repartidor, un paquete a la vez**: al asignar queda ocupado y se libera al registrar la entrega.
- **Municipios**: los conjuntos reflejan los paquetes registrados en la jornada (no se descuentan al
  entregar). Los destinos deben escribirse siempre igual (por ejemplo, `Calarcá`); `Armenia` y
  `armenia` se tratarían como municipios distintos.
- **Búsqueda binaria**: su precondición es que la lista esté ordenada por código.
- **Recursividad**: con listas de cientos de miles de elementos la pila de Java podría desbordarse; para
  los 30 a 500 paquetes de la jornada no es un problema.
