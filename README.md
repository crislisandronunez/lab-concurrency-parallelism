# Laboratorio de concurrencia y paralelismo en Java

Laboratorio práctico de Java 21 y Spring Boot orientado a comprender distintas formas de ejecutar
trabajo, identificar cuándo conviene utilizarlas y observar su comportamiento mediante métricas.

El proyecto utiliza escenarios financieros acotados para estudiar cuatro enfoques:

| Enfoque                          | Escenario de estudio                 | Propósito                                                                        |
|----------------------------------|--------------------------------------|----------------------------------------------------------------------------------|
| Ejecución secuencial             | Procesamiento de transacciones       | Respetar dependencias y el orden de las operaciones.                             |
| `CompletableFuture`              | Obtención de datos de mercado        | Coordinar operaciones de entrada/salida independientes.                          |
| Procesamiento paralelo CPU-bound | Análisis de transacciones históricas | Distribuir cálculos intensivos entre recursos de procesamiento.                  |
| Virtual threads                  | Consultas financieras concurrentes   | Atender numerosas tareas de entrada/salida con un modelo de concurrencia ligero. |

La finalidad es estudiar las características y los costos de cada técnica, medir su rendimiento y
comparar sus resultados. Los escenarios son ejercicios de laboratorio, no una implementación de un
sistema bancario de producción.

## Tecnologías

- **Java 21** y **Spring Boot** para la aplicación.
- **Spring Web** y **Jakarta Validation** para la API HTTP y la validación de solicitudes.
- **Spring Data JPA**, **Hibernate** y **PostgreSQL** para la persistencia.
- **Micrometer**, **Spring Boot Actuator** y **Prometheus** para las métricas.
- **HikariCP** para la gestión del pool de conexiones.
- **Docker Compose** para los servicios locales.
- **Maven**, **Postman** y **DBeaver** para construcción, pruebas e inspección de datos.

## Módulo 1 — Gestión de transacciones

### Objetivo

Procesar un débito sobre una cuenta mediante un flujo **secuencial**: cada etapa utiliza o depende
del resultado de una etapa anterior. El escenario permite observar el costo de las operaciones de
persistencia, además de comprobar el resultado funcional del procesamiento.

La ejecución secuencial describe el **orden de las etapas dentro de cada transacción**. No implica,
por sí sola, que el servidor HTTP acepte una única solicitud a la vez.

### Flujo funcional

1. Recibir una solicitud de transacción.
2. Buscar la cuenta en PostgreSQL.
3. Comprobar que la cuenta exista y esté activa.
4. Verificar que la operación solicitada sea un débito.
5. Comprobar que el saldo alcance para cubrir el importe.
6. Debitar el importe de la cuenta.
7. Guardar la cuenta con el saldo actualizado.
8. Crear y completar la transacción.
9. Guardar el registro de la transacción.
10. Devolver el resultado del procesamiento.

Las condiciones de rechazo interrumpen el flujo antes de realizar el débito. La operación utiliza
los modelos de dominio para administrar el saldo y el estado de la transacción.

### Arquitectura

El módulo sigue una **arquitectura hexagonal** con tres responsabilidades diferenciadas:

| Capa            | Responsabilidad                                                          |
|-----------------|--------------------------------------------------------------------------|
| Dominio         | Modelos `Account` y `Transaction`, estados y operaciones de negocio.     |
| Aplicación      | Caso de uso `ProcessTransaction` y contratos de entrada y salida.        |
| Infraestructura | API REST, entidades JPA, adaptadores, mappers, configuración y métricas. |

Los contratos de aplicación separan el caso de uso de los mecanismos de persistencia. La
infraestructura implementa esos contratos y convierte los datos entre las representaciones HTTP,
de dominio y de base de datos.

### Modelo de datos

La información se almacena en dos tablas de PostgreSQL:

**`accounts`**

| Campo      | Tipo en Java    | Significado              |
|------------|-----------------|--------------------------|
| `id`       | `UUID`          | Identificador de cuenta. |
| `holder`   | `String`        | Titular.                 |
| `balance`  | `BigDecimal`    | Saldo.                   |
| `currency` | `Currency`      | Moneda.                  |
| `status`   | `AccountStatus` | Estado de la cuenta.     |

**`transactions`**

| Campo       | Tipo en Java     | Significado                      |
|-------------|------------------|----------------------------------|
| `id`        | `UUID`           | Identificador de la transacción. |
| `accountId` | `UUID`           | Cuenta asociada.                 |
| `type`      | `TransferType`   | Tipo de operación.               |
| `amount`    | `BigDecimal`     | Importe.                         |
| `createdAt` | `Instant`        | Fecha de creación.               |
| `status`    | `TransferStatus` | Estado de procesamiento.         |
| `category`  | `Category`       | Categoría.                       |

Una cuenta puede estar asociada a múltiples transacciones. Los importes utilizan `BigDecimal`,
los identificadores utilizan `UUID` y los estados y categorías se representan mediante enums.

### API REST

**`POST /api/transactions`**

Recibe los siguientes datos:

| Campo       | Tipo           | Descripción                                |
|-------------|----------------|--------------------------------------------|
| `accountId` | UUID           | Cuenta sobre la que se solicita el débito. |
| `type`      | `TransferType` | Tipo de transacción.                       |
| `amount`    | `BigDecimal`   | Importe positivo.                          |
| `category`  | `Category`     | Categoría de la operación.                 |

Ejemplo de solicitud:

```json
{
  "accountId": "10000000-0000-0000-0000-000000000001",
  "type": "DEBIT",
  "amount": 100.00,
  "category": "FOOD"
}
```

Una ejecución exitosa devuelve **HTTP `201 Created`**, con `id`, `status` y `message`:

```json
{
  "id": "599be711-f43c-486d-be63-50836a84cb5a",
  "status": "COMPLETED",
  "message": "La transacción se ha procesado correctamente"
}
```

El controlador valida la solicitud, utiliza un mapper para convertirla en el comando de aplicación
y devuelve el resultado mediante un DTO de respuesta.

### Persistencia

La persistencia utiliza Spring Data JPA y PostgreSQL. Las entidades JPA se mantienen separadas de
los modelos del dominio, y los adaptadores implementan los puertos definidos por la aplicación.

Se utilizan índices para consultar transacciones por cuenta, fecha y categoría. El procesamiento
del módulo no utiliza caché Redis.

### Observabilidad

El laboratorio utiliza **Micrometer** y **Spring Boot Actuator** para consultar métricas de
rendimiento. En el caso de uso se miden individualmente tres operaciones de persistencia:

| Operación           | Identificador de tarea |
|---------------------|------------------------|
| Buscar cuenta       | `find_account`         |
| Guardar cuenta      | `save_account`         |
| Guardar transacción | `save_transaction`     |

La métrica `lab.execution.duration` utiliza etiquetas para distinguir el proceso y la tarea. Sus
estadísticas permiten conocer la cantidad de mediciones (`COUNT`), el tiempo acumulado
(`TOTAL_TIME`) y la duración máxima (`MAX`).

Los endpoints de Actuator permiten consultar las métricas disponibles y las series individuales:

- `/actuator/metrics`
- `/actuator/metrics/lab.execution.duration`
- `/actuator/prometheus`

También se observan las métricas automáticas de JDBC, HikariCP, JVM y HTTP. Las duraciones de
persistencia medidas alrededor de los adaptadores no equivalen necesariamente al tiempo de una
sentencia SQL aislada ni al tiempo total de respuesta HTTP.

### Prueba del escenario

La API se probó desde Postman, con verificación del saldo de la cuenta y del registro de la
transacción en PostgreSQL mediante DBeaver.

Se ejecutaron **20 solicitudes consecutivas** desde Postman Collection Runner y las 20 recibieron
**HTTP `201 Created`**. El procesamiento conservó el orden interno de sus operaciones.

Micrometer registró las mediciones de lectura y escritura, lo que permite analizar el costo de
la persistencia a partir de ejecuciones repetidas. Esas mediciones sirven como referencia de
comportamiento secuencial, no como una afirmación de capacidad máxima o de rendimiento bajo carga
concurrente.
