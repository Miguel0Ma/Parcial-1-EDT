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

Solo se usan los temas vistos en el curso: Java, recursividad, pila de llamadas, búsqueda binaria, divide y vencerás,
análisis básico de complejidad, Java Collections Framework (`List`, `Set`, `Map`, `Queue`, `PriorityQueue`),
clases e interfaces genéricas, `Iterable`/`Iterator`, lista simplemente enlazada propia, `Comparable` y `Comparator`.

---

## 1. Requisitos y cómo ejecutar

- Java 25 (configurado en `pom.xml`)
- Maven 3.9 o superior
- JUnit 5.11.4 (se descarga automáticamente con Maven)
- Entorno gráfico: `Main` usa ventanas de Swing (`JOptionPane`)



---

## 2. Estructura del proyecto

```
src/main/java/co/edu/uniquindio/
├── Main.java                         Menú con JOptionPane, demostración y datos iniciales
├── estructuras/
│   ├── Coleccion.java                Interfaz genérica con el contrato común
│   └── ListaEnlazada.java            Lista simplemente enlazada propia, genérica e Iterable
├── modelo/
│   ├── Paquete.java                  Comparable<Paquete>: orden natural por código
│   └── Repartidor.java
├── comparadores/
│   └── ComparadoresPaquete.java      Comparator: prioridad, peso, tiempo y despacho prioritario
├── algoritmos/
│   ├── Recursivos.java               Peso total por municipio y conteo por prioridad mínima
│   ├── BusquedaBinaria.java          Búsqueda por código en una lista ordenada
│   └── DivideYVenceras.java          Paquete de mayor peso
└── servicios/
    └── CentroDistribucion.java       Reúne las colecciones y aplica las reglas del negocio

src/test/java/co/edu/uniquindio/      Pruebas unitarias con JUnit 5 (una clase de prueba por cada clase)

Parcial1EDT.MAMC.SVC.pdf              Documento de entrega
pila_contarPrioridadMinina1.xlsx      Pila de llamadas de contarPrioridadMinina(paquetes, 4, 0)
```

Idea general: las clases de `estructuras`, `modelo`, `comparadores` y `algoritmos` son piezas independientes y
reutilizables. `CentroDistribucion` las combina y es la única que conoce las reglas del negocio. `Main` solo la usa
para pedir datos y mostrar resultados.

---

## 3. Cómo funciona una jornada (flujo de un paquete)

```
 registrarPaquete ──► [en las dos colas de despacho] ──► despacharProritario  /  atenderSiguientePorLlegada
                                  │                                   │
                                  └────────── asignarPaquete ◄────────┘
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

1. **Registro** (`registrarPaquete`). Valida que el paquete no sea nulo y que el código no exista. Luego lo guarda
   en `paquetesPorCodigo`, `registro`, `pendientes`, las dos colas de despacho, los dos conjuntos de municipios y
   el grupo de su municipio (`paquetesPorMunicipio`).
2. **Consulta** (`consultarPaquete`, `existePaquete`, `consultarRepartidor`). Búsqueda directa por clave en un
   `HashMap`, sin recorrer listas.
3. **Despacho**. Hay dos modalidades, cada una con su cola:
   - `atenderSiguientePorLlegada()`: sale primero el que llegó antes (cola FIFO). `verSiguientePorLlegada()` solo lo consulta.
   - `despacharProritario()`: sale primero el de mayor prioridad; si empatan, el de menor tiempo estimado; si siguen
     empatados, el de código menor. `verSiguientePrioritario()` solo lo consulta.

   Un paquete está en las dos colas a la vez. Cuando se saca de una, se quita también de la otra, para que no se
   despache dos veces.
4. **Asignación** (`asignarPaquete`, `asignarAutomaticamente`). Antes de asignar valida, en este orden: que el paquete
   exista, que siga pendiente (no entregado), que no esté ya asignado, que el repartidor exista, que su zona sea el
   destino del paquete y que esté disponible. Al asignar, el repartidor pasa a ocupado y el paquete se quita de las
   colas (si no había sido despachado antes). `asignarAutomaticamente` elige el primer repartidor disponible de la zona
   y devuelve `null` si no hay ninguno.
5. **Entrega** (`registrarEntrega`). El paquete debe estar asignado. Sale de `pendientes`, se agrega al final del
   historial (la lista propia) y el repartidor vuelve a estar disponible.
6. **Análisis**. Peso total por municipio y conteo por prioridad mínima (recursivos), búsqueda binaria por código
   y paquete de mayor peso (divide y vencerás).

---

## 4. Qué hace cada clase

### `estructuras`

**`Coleccion<T>`** es la interfaz genérica con el contrato: `agregar`, `eliminar`, `obtener`, `tamano` y `estaVacia`.
Define *qué* se puede hacer; la lista define *cómo*.

**`ListaEnlazada<T>`** implementa `Coleccion<T>` e `Iterable<T>`. Está formada por nodos privados, cada uno con un
valor y una referencia al siguiente:

```
cabeza ─► [PQ105 | •]──►[PQ942 | •]──►[PQ731 | null] ◄─ cola
```

- Guarda `cabeza` (primer nodo), `cola` (último nodo) y `tamano`.
- `agregar` enlaza al final usando `cola`: cuesta O(1) y no recorre la lista. No admite elementos nulos
  (lanza `IllegalArgumentException`).
- `eliminar` busca el nodo guardando también el anterior, y re-enlaza según el caso:
  - primero o único: `cabeza` pasa al siguiente;
  - intermedio o último: el anterior salta al siguiente;
  - si se elimina el último, `cola` retrocede al anterior;
  - si el elemento no existe (o la lista está vacía), devuelve `false` sin cambiar nada.
- `obtener(i)` recorre `i` nodos (O(n)) y lanza `IndexOutOfBoundsException` si `i` no es válido.
- `iterator()` devuelve un iterador nuevo cada vez (`hasNext`, `next`). El código externo recorre la lista con
  `for-each` sin conocer los nodos, y dos iteradores no se estorban entre sí.
- `toString()` muestra la cadena con flechas (`PQ105 → PQ942 → PQ731`) o `(vacía)`.
- No usa ninguna colección de Java para guardar elementos.

### `modelo`

**`Paquete`** (`codigo`, `destino`, `peso`, `prioridad`, `tiempoEstimado`). El constructor valida las reglas: código y
destino no nulos ni vacíos, peso > 0 (también rechaza `NaN`), prioridad entre 1 y 5 y tiempo > 0; si algo falla lanza
`IllegalArgumentException`. Guarda código y destino sin espacios sobrantes (`trim`). Hay un segundo constructor sin
prioridad que usa 3. Implementa `Comparable<Paquete>` (`compareTo` por código ascendente). `equals` y `hashCode`
usan solo el código.

**`Repartidor`** (`id`, `nombre`, `zona`, `disponible`). Valida que id, nombre y zona no estén vacíos.
`disponible` es lo único que cambia con el tiempo (`setDisponible`). `equals` y `hashCode` usan solo el id.

### `comparadores`

**`ComparadoresPaquete`** (constructor privado) tiene criterios de ordenamiento reutilizables, independientes del
orden natural. Cada criterio es una clase `Comparator` interna con una instancia estática:

| Criterio | Clase interna | Orden |
|---|---|---|
| `PRIORIDAD_DESC` | `ComparadorPrioridadDesc` | prioridad, de mayor a menor |
| `PESO_DESC` | `ComparadorPesoDesc` | peso, de mayor a menor |
| `TIEMPO_ASC` | `ComparadorTiempoAsc` | tiempo estimado, de menor a mayor |
| `DESPACHO_PRIORITARIO` | `ComparadorDespachoPrioritario` | prioridad desc, luego tiempo asc, luego código asc |

`DESPACHO_PRIORITARIO` es el que usa la `PriorityQueue`. Ordenar con cualquiera de ellos no cambia el orden natural
del `Paquete`.

### `algoritmos`

**`Recursivos`** (sin `for`, `while` ni `do-while`):

- `pesoTotalMunicipio(paquetes, destino, indice)`
  - Caso base: `indice >= size()` → devuelve 0.
  - Caso recursivo: llama con `indice + 1` y suma el peso del paquete actual si su destino coincide
    (sin distinguir mayúsculas).
  - Se avanza hacia el caso base aumentando el índice en cada llamada.
- `contarPrioridadMinina(paquetes, minima, indice)`: misma idea, suma 1 cuando `prioridad >= minima`.
  (El nombre conserva ese error de ortografía en el código.)

**`BusquedaBinaria.buscarPorCodigo(lista, codigo)`**: compara con el elemento del medio y descarta la mitad donde no
puede estar. El punto medio se calcula como `bajo + (alto - bajo) / 2` para evitar desbordamiento. Devuelve la
posición o -1. *Precondición: la lista debe estar ordenada por código.* Sobre datos desordenados el resultado no es
confiable.

**`DivideYVenceras.mayorPeso(lista)`** muestra las etapas de la estrategia:

1. caso base: un rango con un solo paquete es el mayor;
2. división por la mitad (`medio`);
3. subproblema izquierdo `[inicio..medio]`;
4. subproblema derecho `[medio+1..fin]`;
5. combinación: se queda con el más pesado de los dos resultados (si empatan, gana el de la izquierda);
6. resultado final: el que devuelve la llamada con todo el rango.

Lanza `IllegalArgumentException` si la lista es nula o vacía. `mayorPesoConTraza` hace lo mismo e imprime cada
división, caso base y combinación con sangría por nivel. Con los datos cargados en `Main`, el mayor es
`PQ584` (Montenegro, 18.7 kg).

### `servicios`

**`CentroDistribucion`** guarda estas colecciones (elegidas en la Parte A):

| Campo | Estructura | Para qué sirve |
|---|---|---|
| `registro` | `ArrayList<Paquete>` | todos los paquetes en el orden exacto de registro; nunca se quita ni se reordena |
| `pendientes` | `ArrayList<Paquete>` | los aún no entregados, en orden de registro |
| `paquetesPorCodigo` | `HashMap<String, Paquete>` | consulta por código y validación de códigos únicos |
| `municipios` | `HashSet<String>` | municipios con paquetes, sin repetir |
| `municipiosOrdenados` | `TreeSet<String>` | los mismos, siempre ordenados alfabéticamente |
| `paquetesPorMunicipio` | `HashMap<String, List<Paquete>>` | relación municipio → sus paquetes |
| `colaLlegada` | `Queue` (`ArrayDeque`) | atención por orden de llegada |
| `colaPrioridad` | `PriorityQueue` con `DESPACHO_PRIORITARIO` | despacho prioritario |
| `repartidores` | `LinkedHashMap<String, Repartidor>` | repartidores por identificación, en orden de registro |
| `asignaciones` | `HashMap<String, String>` | código de paquete → id de repartidor |
| `historial` | `ListaEnlazada<Paquete>` | entregas en el orden real de entrega |

Los ordenamientos (`ordenNatural`, `ordenarPor(criterio)`) trabajan siempre sobre una **copia**, así el orden de
registro nunca se altera. Los métodos `get...` devuelven vistas de solo lectura, salvo `getHistorial()`, que devuelve
la lista propia para poder recorrerla con `for-each`. `buscarBinaria(listaOrdenada, codigo)` recibe una lista ya
ordenada por código; quien la llama es responsable de ordenarla (por ejemplo con `ordenNatural()`).

### `Main`

Menú con `JOptionPane` y 21 operaciones más "0. Salir":

| Opciones | Operación |
|---|---|
| 1–5 | registrar y consultar paquetes y repartidores, ver repartidores |
| 6–7 | municipios (sin orden y ordenados) y paquetes de un municipio |
| 8 | pendientes en orden de llegada |
| 9, 10, 21 | ver el siguiente por prioridad, despachar por prioridad, atender el siguiente por llegada |
| 11–13 | asignar (manual o automático) y registrar entrega |
| 14 | historial de entregas |
| 15 | ordenar (natural, prioridad, peso o tiempo) |
| 16–19 | búsqueda binaria, peso total de un municipio, conteo por prioridad mínima, mayor peso con traza |
| 20 | demostración completa |

Los errores de validación (`IllegalArgumentException`, `IndexOutOfBoundsException`) se muestran en un diálogo y el
menú sigue funcionando. Los métodos que arman los textos (`enumerar`, `textoHistorial`, `textoMunicipios`, etc.) no
abren ventanas.

La **demostración completa** carga datos nuevos y recorre: validaciones al registrar (código repetido, peso negativo,
prioridad fuera de rango, destino vacío, tiempo cero), consulta por código, municipios, atención por llegada, los
cuatro ordenamientos, despacho prioritario, reglas de asignación (repartidor ocupado, no disponible, zona distinta,
paquete ya asignado), asignación automática, entregas con el historial recorrido con `for-each`, búsqueda binaria,
recursividad y divide y vencerás con traza.

---

## 5. Reglas de negocio y dónde se cumplen

| Regla | Dónde se cumple |
|---|---|
| Código de paquete único | `registrarPaquete` consulta `paquetesPorCodigo` |
| Identificación de repartidor única | `registrarRepartidor` consulta `repartidores` |
| Peso > 0, prioridad 1–5, tiempo > 0, destino no vacío | constructor de `Paquete` |
| Repartidor no disponible no recibe asignaciones | `asignarPaquete` (`IllegalArgumentException`) |
| La zona del repartidor debe ser el destino del paquete | `asignarPaquete` (`IllegalArgumentException`) |
| Orden de llegada | `registro`, `pendientes` y `colaLlegada` |
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
| `registrarPaquete` | O(log n) | la `PriorityQueue` y el `TreeSet` dominan; el resto es O(1) esperado |
| `consultarPaquete` / `existePaquete` | O(1) esperado | `HashMap` |
| `atenderSiguientePorLlegada` | O(n) | `poll` es O(1), pero quitarlo de la otra cola es O(n) |
| `despacharProritario` | O(n) | `poll` es O(log n), pero quitarlo de la otra cola es O(n) |
| `asignarPaquete` | O(n) | `pendientes.contains` y quitar de las colas son O(n) |
| `asignarAutomaticamente` | O(n + r) | recorre los repartidores y luego asigna |
| `registrarEntrega` | O(n) | quitar de `pendientes`; agregar al historial es O(1) |
| `ordenNatural` / `ordenarPor` | O(n log n) | copia y ordenamiento |
| `pesoTotalMunicipios`, `contarPrioridadMinima` | O(n) tiempo, O(n) espacio | pila de llamadas de n niveles |
| `BusquedaBinaria.buscarPorCodigo` | O(log n) tiempo, O(1) espacio | con la lista ya ordenada y acceso por índice O(1) (`ArrayList`) |
| `DivideYVenceras.mayorPeso` | O(n) tiempo, O(log n) espacio | T(n) = 2T(n/2) + O(1); pila de profundidad log n |
| `ListaEnlazada.agregar` | O(1) | gracias a la referencia `cola` |
| `ListaEnlazada.eliminar` / `obtener` | O(n) | recorre desde la cabeza |

Ordenar una lista para buscar en ella cuesta O(n log n), por lo que la búsqueda binaria solo conviene cuando los
datos ya vienen ordenados o cuando se hacen muchas búsquedas sobre la misma lista.

---

## 7. Decisiones y supuestos

- **Prioridad**: valores de 1 a 5 (mayor número = más urgente) y 3 por defecto. El enunciado menciona 0–5 en un lugar
  y 1–5 en las reglas; se adoptó 1–5.
- **Código único de la jornada**: `paquetesPorCodigo` no se limpia al entregar, por eso un código ya entregado no
  puede registrarse otra vez.
- **Despachar, asignar y entregar** son tres pasos distintos. Despachar (o atender) saca el paquete de las colas;
  asignar lo vincula a un repartidor; entregar lo cierra. Asignar sin despachar también es válido (lo quita de
  las colas).
- **Un repartidor, un paquete a la vez**: al asignar queda ocupado y se libera al registrar la entrega.
- **Municipios**: los conjuntos reflejan los paquetes registrados en la jornada (no se descuentan al entregar).
  Los conjuntos y el mapa por municipio distinguen mayúsculas (`Armenia` y `armenia` serían municipios distintos),
  mientras que la validación de zona en la asignación y los cálculos recursivos por destino no las distinguen.
  Conviene escribir siempre igual cada destino (por ejemplo, `Calarcá`).
- **Orden de los municipios**: el `TreeSet<String>` usa el orden natural de `String` (por código Unicode), así que un
  nombre con tilde al inicio de la comparación podría quedar fuera del orden alfabético esperado en español.
  Con los municipios cargados esto no ocurre.
- **Búsqueda binaria**: su precondición es que la lista esté ordenada por código.
- **Recursividad**: con listas de cientos de miles de elementos la pila de Java podría desbordarse; para los
  30 a 500 paquetes de la jornada no es un problema.
- **Nombres**: dos métodos conservan errores de ortografía (`despacharProritario` y `contarPrioridadMinina`); las
  pruebas y `Main` los usan con ese nombre.

---

## 8. Pruebas

Las pruebas unitarias (JUnit 5, 134 métodos `@Test`) cubren el modelo, la lista enlazada (vacía, un elemento, eliminar
primero, intermedio, último e inexistente), los comparadores, los tres algoritmos y el servicio `CentroDistribucion`.
Se ejecutan con `mvn test`.

## 9. Uso de inteligencia artificial

El uso de herramientas de IA está registrado en la Parte D del documento PDF de entrega (bitácora y caso de
respuesta cuestionable).
