# Prompt para Proyecto: Dashboard Financiero con Técnicas de Programación Paralela

## Descripción del Proyecto

Desarrollar un **Dashboard Financiero** en Java que aplique las 4 técnicas de programación paralela aprendidas en los ejercicios (secuencial, CompletableFuture paralelo, procesamiento CPU-bound paralelo, y virtual threads). El objetivo es demostrar comprensión de cada enfoque mediante módulos de análisis de datos financieros.

---

## Objetivos de Aprendizaje

- Aplicar ejecución secuencial donde el orden es crítico
- Implementar paralelismo con CompletableFuture para operaciones I/O independientes
- Utilizar procesamiento paralelo CPU-bound para cálculos intensivos
- Implementar virtual threads para alta concurrencia en operaciones I/O
- Comparar rendimiento entre diferentes enfoques
- Construir un dashboard financiero funcional

---

## Estructuras de Datos y Campos (Simplificados)

El proyecto no requiere un modelo de negocio complejo. Usa estas estructuras simples para enfocarte en el procesamiento paralelo/secuencial.

### Tabla/Entidad: Cuenta
**Propósito**: Representar cuentas bancarias para procesamiento de transacciones.

**Campos**:
- `id` (String): ID único de cuenta
- `titular` (String): Nombre del titular
- `saldo` (double): Saldo actual
- `moneda` (String): Código de moneda (USD, EUR, ARS)
- `activa` (boolean): Estado de la cuenta

**Ejemplo**:
```java
Cuenta cuenta = new Cuenta("ACC-001", "Juan Perez", 5000.0, "USD", true);
```

---

### Tabla/Entidad: Transaccion
**Propósito**: Registrar movimientos financieros para análisis histórico.

**Campos**:
- `id` (String): ID único de transacción
- `cuentaId` (String): ID de la cuenta origen
- `tipo` (String): Tipo (DEBITO, CREDITO, TRANSFERENCIA)
- `monto` (double): Monto de la transacción
- `fecha` (long): Timestamp en milisegundos
- `estado` (String): Estado (PENDIENTE, COMPLETADA, RECHAZADA)
- `categoria` (String): Categoría (COMIDA, TRANSPORTE, SERVICIOS, ENTRETENIMIENTO)

**Ejemplo**:
```java
Transaccion t = new Transaccion("TXN-001", "ACC-001", "DEBITO", 150.0, System.currentTimeMillis(), "COMPLETADA", "COMIDA");
```

---

### Tabla/Entidad: Accion
**Propósito**: Datos de mercado para dashboard financiero.

**Campos**:
- `simbolo` (String): Símbolo de acción (AAPL, GOOGL, TSLA)
- `precio` (double): Precio actual
- `cambio` (double): Cambio porcentual
- `volumen` (long): Volumen de operaciones
- `timestamp` (long): Timestamp de la última actualización

**Ejemplo**:
```java
Accion accion = new Accion("AAPL", 175.50, 2.3, 10_000_000, System.currentTimeMillis());
```

---

### Tabla/Entidad: TipoCambio
**Propósito**: Tasas de cambio entre monedas.

**Campos**:
- `monedaOrigen` (String): Moneda origen (USD)
- `monedaDestino` (String): Moneda destino (EUR, ARS)
- `tasa` (double): Tasa de cambio
- `timestamp` (long): Timestamp de la última actualización

**Ejemplo**:
```java
TipoCambio tc = new TipoCambio("USD", "EUR", 0.92, System.currentTimeMillis());
```

---

### Tabla/Entidad: NoticiaMercado
**Propósito**: Noticias financieras para el dashboard.

**Campos**:
- `titulo` (String): Título de la noticia
- `fuente` (String): Fuente (Reuters, Bloomberg, CNBC)
- `timestamp` (long): Timestamp de publicación
- `sentimiento` (String): Sentimiento (POSITIVO, NEGATIVO, NEUTRAL)

**Ejemplo**:
```java
NoticiaMercado noticia = new NoticiaMercado("Fed anuncia tasas", "Reuters", System.currentTimeMillis(), "NEUTRAL");
```

---

### Datos de Prueba (Mock Data)

Para facilitar el desarrollo, puedes generar datos simulados:

```java
// Generar array de 1M transacciones para análisis CPU-bound
Transaccion[] transacciones = new Transaccion[1_000_000];
for (int i = 0; i < 1_000_000; i++) {
    transacciones[i] = new Transaccion(
        "TXN-" + i,
        "ACC-" + (i % 100),  // 100 cuentas diferentes
        i % 2 == 0 ? "DEBITO" : "CREDITO",
        10.0 + Math.random() * 1000.0,
        System.currentTimeMillis() - (i * 1000),
        "COMPLETADA",
        getRandomCategoria()
    );
}

// Generar lista de acciones para paralelismo I/O
Accion[] acciones = {
    new Accion("AAPL", 175.50, 2.3, 10_000_000, System.currentTimeMillis()),
    new Accion("GOOGL", 140.20, -1.5, 8_000_000, System.currentTimeMillis()),
    new Accion("TSLA", 250.80, 5.2, 15_000_000, System.currentTimeMillis())
};
```

---

## Modelo de Respuesta del Dashboard Financiero

**Propósito**: Estructura consolidada que representa todos los datos que el dashboard debe mostrar al usuario, integrando los 4 módulos.

### Clase: DashboardResponse
**Campos**:
- `timestamp` (long): Timestamp de generación del dashboard
- `resumenCuentas` (ResumenCuentas): Resumen de cuentas bancarias
- `datosMercado` (DatosMercado): Datos de mercado en tiempo real
- `analisisTransacciones` (AnalisisTransacciones): Métricas de transacciones históricas
- `metricasRendimiento` (MetricasRendimiento): Tiempos de ejecución por módulo

---

### ResumenCuentas
**Campos**:
- `totalCuentas` (int): Cantidad total de cuentas
- `saldoTotal` (double): Suma de saldos de todas las cuentas
- `saldoPromedio` (double): Saldo promedio por cuenta
- `cuentasActivas` (int): Cantidad de cuentas activas
- `monedaPredominante` (String): Moneda con mayor saldo

---

### DatosMercado
**Campos**:
- `acciones` (Accion[]): Lista de acciones con precios
- `tiposCambio` (TipoCambio[]): Tasas de cambio disponibles
- `noticias` (NoticiaMercado[]): Noticias recientes del mercado
- `indiceMercado` (double): Índice compuesto del mercado

---

### AnalisisTransacciones
**Campos**:
- `totalTransacciones` (long): Cantidad total de transacciones procesadas
- `montoTotal` (double): Suma de todos los montos
- `montoPromedio` (double): Monto promedio por transacción
- `montoMaximo` (double): Transacción de mayor monto
- `montoMinimo` (double): Transacción de menor monto
- `transaccionesPorCategoria` (Map<String, Long>): Cantidad por categoría
- `montoPorCategoria` (Map<String, Double>): Suma de montos por categoría

---

### MetricasRendimiento
**Campos**:
- `tiempoProcesamientoTransacciones` (long): Tiempo módulo secuencial (ms)
- `tiempoObtencionDatosMercado` (long): Tiempo módulo CompletableFuture (ms)
- `tiempoAnalisisHistorico` (long): Tiempo módulo CPU-bound (ms)
- `tiempoConsultasConcurrentes` (long): Tiempo módulo virtual threads (ms)
- `tiempoTotalDashboard` (long): Tiempo total de generación (ms)

---

### Ejemplo de JSON Response
```json
{
  "timestamp": 1730497200000,
  "resumenCuentas": {
    "totalCuentas": 100,
    "saldoTotal": 500000.0,
    "saldoPromedio": 5000.0,
    "cuentasActivas": 95,
    "monedaPredominante": "USD"
  },
  "datosMercado": {
    "acciones": [
      {"simbolo": "AAPL", "precio": 175.50, "cambio": 2.3, "volumen": 10000000},
      {"simbolo": "GOOGL", "precio": 140.20, "cambio": -1.5, "volumen": 8000000}
    ],
    "tiposCambio": [
      {"monedaOrigen": "USD", "monedaDestino": "EUR", "tasa": 0.92}
    ],
    "noticias": [
      {"titulo": "Fed anuncia tasas", "fuente": "Reuters", "sentimiento": "NEUTRAL"}
    ],
    "indiceMercado": 4500.25
  },
  "analisisTransacciones": {
    "totalTransacciones": 1000000,
    "montoTotal": 500000000.0,
    "montoPromedio": 500.0,
    "montoMaximo": 10000.0,
    "montoMinimo": 10.0,
    "transaccionesPorCategoria": {
      "COMIDA": 250000,
      "TRANSPORTE": 200000,
      "SERVICIOS": 300000,
      "ENTRETENIMIENTO": 250000
    },
    "montoPorCategoria": {
      "COMIDA": 125000000.0,
      "TRANSPORTE": 100000000.0,
      "SERVICIOS": 150000000.0,
      "ENTRETENIMIENTO": 125000000.0
    }
  },
  "metricasRendimiento": {
    "tiempoProcesamientoTransacciones": 150,
    "tiempoObtencionDatosMercado": 3000,
    "tiempoAnalisisHistorico": 250,
    "tiempoConsultasConcurrentes": 1000,
    "tiempoTotalDashboard": 4400
  }
}
```

---

## Infraestructura de Datos: PostgreSQL + Redis con Docker

### Docker Compose Configuration

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16
    container_name: financial-db
    environment:
      POSTGRES_DB: financial_dashboard
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U admin"]
      interval: 5s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: financial-cache
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
  redis_data:
```

---

### PostgreSQL Schema

**Tablas**:

```sql
CREATE TABLE cuentas (
    id VARCHAR(50) PRIMARY KEY,
    titular VARCHAR(100),
    saldo DOUBLE,
    moneda VARCHAR(10),
    activa BOOLEAN
);

CREATE TABLE transacciones (
    id VARCHAR(50) PRIMARY KEY,
    cuenta_id VARCHAR(50),
    tipo VARCHAR(20),
    monto DOUBLE,
    fecha BIGINT,
    estado VARCHAR(20),
    categoria VARCHAR(50),
    FOREIGN KEY (cuenta_id) REFERENCES cuentas(id)
);

CREATE TABLE acciones (
    simbolo VARCHAR(10) PRIMARY KEY,
    precio DOUBLE,
    cambio DOUBLE,
    volumen BIGINT,
    timestamp BIGINT
);

CREATE TABLE tipos_cambio (
    moneda_origen VARCHAR(10),
    moneda_destino VARCHAR(10),
    tasa DOUBLE,
    timestamp BIGINT,
    PRIMARY KEY (moneda_origen, moneda_destino)
);

CREATE TABLE noticias_mercado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200),
    fuente VARCHAR(50),
    timestamp BIGINT,
    sentimiento VARCHAR(20)
);
```

**Índices para rendimiento**:
```sql
CREATE INDEX idx_transacciones_cuenta ON transacciones(cuenta_id);
CREATE INDEX idx_transacciones_fecha ON transacciones(fecha);
CREATE INDEX idx_transacciones_categoria ON transacciones(categoria);
CREATE INDEX idx_acciones_timestamp ON acciones(timestamp);
```

---

### Redis Data Structures

**Propósito**: Caching de datos frecuentemente accedidos para reducir carga en PostgreSQL.

**Keys Propuestas**:
- `cuenta:{id}` → Hash con datos de cuenta (TTL: 1 hora)
- `accion:{simbolo}` → Hash con datos de acción (TTL: 30 segundos)
- `tipo_cambio:{origen}:{destino}` → String con tasa (TTL: 5 minutos)
- `dashboard:summary` → Hash con resumen del dashboard (TTL: 1 minuto)
- `transacciones:categoria:{categoria}` → Sorted set con transacciones por categoría

**Ejemplo de Cache-Aside Pattern**:
```java
// Caching de cuenta
redis.setex("cuenta:ACC-001", 3600, cuentaJson);

// Caching de acción (más corto por volatilidad)
redis.setex("accion:AAPL", 30, accionJson);

// Cache-aside pattern
String cached = redis.get("cuenta:ACC-001");
if (cached == null) {
    Cuenta cuenta = postgres.query("SELECT * FROM cuentas WHERE id = ?", "ACC-001");
    redis.setex("cuenta:ACC-001", 3600, toJson(cuenta));
    return cuenta;
}
return fromJson(cached);
```

---

### Métricas de Consumo PostgreSQL

**Connection Pool Metrics (HikariCP)**:
- `activeConnections`: Conexiones activas
- `idleConnections`: Conexiones inactivas
- `totalConnections`: Total creadas
- `pendingThreads`: Threads esperando conexión
- `maxConnections`: Máximo configurado
- `connectionTimeout`: Timeout al obtener conexión

**Query Performance**:
- `queryExecutionTime`: Tiempo por query
- `slowQueriesCount`: Queries > 100ms
- `queriesPerSecond`: QPS
- `rowsProcessed`: Filas procesadas

**Database Metrics** (vía pg_stat):
- `dbConnections`: Conexiones activas en DB
- `dbCacheHitRatio`: Ratio de cache hit
- `dbTransactionRate`: Transacciones por segundo
- `dbLockWaits`: Esperas por locks

---

### Métricas de Consumo Redis

**Connection Metrics**:
- `activeConnections`: Conexiones activas
- `rejectedConnections`: Conexiones rechazadas
- `connectionLatency`: Latencia de conexión

**Memory Metrics**:
- `usedMemory`: Memoria usada (bytes)
- `usedMemoryPeak`: Pico de memoria
- `usedMemoryPercentage`: Porcentaje de memoria usada
- `maxMemory`: Memoria máxima configurada

**Performance Metrics**:
- `cacheHitRatio`: Ratio de cache hit
- `cacheMissRatio`: Ratio de cache miss
- `commandsPerSecond`: Comandos por segundo
- `keyCount`: Cantidad de keys almacenadas
- `expiredKeys`: Keys expiradas
- `evictedKeys`: Keys evictadas por memoria

---

### Integración de Infraestructura con Módulos

**Módulo 1 (Secuencial)**:
- PostgreSQL: Una query a la vez, sin cache
- Redis: No utilizado (baseline sin caché)
- Propósito: Medir rendimiento base sin optimizaciones

**Módulo 2 (CompletableFuture)**:
- PostgreSQL: Múltiples queries en paralelo, connection pool
- Redis: Caching de acciones y tipos de cambio
- Propósito: Medir beneficio de paralelismo + caching

**Módulo 3 (CPU-bound)**:
- PostgreSQL: Batch query de 1M transacciones
- Redis: No aplicable (procesamiento histórico)
- Propósito: Medir rendimiento de procesamiento masivo de datos

**Módulo 4 (Virtual Threads)**:
- PostgreSQL: 100+ consultas concurrentes, stress test del pool
- Redis: Caching de cuentas para reducir carga en DB
- Propósito: Medir escalabilidad con alta concurrencia

---

### Dependencias Maven

```xml
<dependencies>
    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.1</version>
    </dependency>
    
    <!-- HikariCP Connection Pool -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>5.1.0</version>
    </dependency>
    
    <!-- Redis Client -->
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>5.1.0</version>
    </dependency>
</dependencies>
```

---

### Comparación de Rendimiento Esperada

| Escenario | Sin Cache | Con Redis | Mejora |
|-----------|-----------|-----------|--------|
| Consulta cuenta | ~50ms | ~2ms | 96% |
| Consulta acción | ~30ms | ~1ms | 97% |
| Dashboard completo | ~5000ms | ~200ms | 96% |
| 100 consultas concurrentes | ~5000ms | ~500ms | 90% |

---

## Módulos Sugeridos (Cada uno aplica una técnica)

### Módulo 1: Procesamiento de Transacciones (EjercicioUno - Secuencial)
**Técnica**: Ejecución secuencial

**Escenario de negocio**: Procesamiento de una transacción financiera donde cada paso depende del anterior.

**Sugerencia de implementación**:
- Validar cuenta (PostgreSQL: `SELECT * FROM cuentas WHERE id = ? AND activa = true`)
- Verificar saldo suficiente (PostgreSQL: `SELECT saldo FROM cuentas WHERE id = ?`)
- Procesar débito (PostgreSQL: `UPDATE cuentas SET saldo = saldo - ? WHERE id = ?`)
- Registrar auditoría (PostgreSQL: `INSERT INTO transacciones (...) VALUES (...)`)
- Cada operación debe esperar a la anterior
- Medir tiempo total de ejecución
- Medir métricas de PostgreSQL: connection pool, query time

**No es obligatorio**: Puedes aplicar esta técnica a otro flujo donde el orden sea crítico.

---

### Módulo 2: Obtención de Datos de Mercado (EjercicioDos - CompletableFuture Paralelo)
**Técnica**: Paralelismo con `CompletableFuture.runAsync()` / `supplyAsync()`

**Escenario de negocio**: Dashboard que necesita datos de múltiples fuentes externas simultáneamente.

**Sugerencia de implementación**:
- Obtener precio de acción (PostgreSQL: `SELECT * FROM acciones WHERE simbolo = ?` + Redis cache)
- Obtener tipo de cambio (PostgreSQL: `SELECT * FROM tipos_cambio WHERE ...` + Redis cache)
- Obtener noticias del mercado (PostgreSQL: `SELECT * FROM noticias_mercado ORDER BY timestamp DESC LIMIT 5`)
- Todas las queries en paralelo con `CompletableFuture.supplyAsync()`
- Usar `CompletableFuture.allOf()` para esperar todos
- Implementar cache-aside pattern con Redis
- Medir métricas: PostgreSQL pool, Redis cache hit/miss ratio

**No es obligatorio**: Puedes aplicar esta técnica a cualquier escenario con múltiples llamadas I/O independientes.

---

### Módulo 3: Análisis de Histórico de Transacciones (EjercicioTres - CPU-bound Paralelo)
**Técnica**: Procesamiento paralelo CPU-bound con `CompletableFuture.supplyAsync()`

**Escenario de negocio**: Análisis de grandes volúmenes de datos históricos (cálculos estadísticos intensivos).

**Sugerencia de implementación**:
- Cargar 1M transacciones de PostgreSQL (batch query)
- Calcular métricas: suma total de `monto`, promedio, máximo, mínimo por `categoria`
- Dividir el dataset en partes y procesar en paralelo con `CompletableFuture.supplyAsync()`
- Combinar resultados parciales
- Medir tiempo de query PostgreSQL + tiempo de procesamiento CPU
- Comparar vs procesamiento secuencial del mismo dataset

**No es obligatorio**: Puedes aplicar esta técnica a cualquier cálculo intensivo que se pueda dividir.

---

### Módulo 4: API REST de Consultas (EjercicioCuatro - Virtual Threads)
**Técnica**: Virtual threads con `Executors.newVirtualThreadPerTaskExecutor()`

**Escenario de negocio**: Servidor que maneja miles de consultas financieras simultáneas.

**Sugerencia de implementación**:
- Crear servicio que recibe 100+ consultas de `Cuenta` por `id`
- Cada consulta: buscar cuenta en PostgreSQL (con Redis cache) + calcular saldo proyectado
- Usar virtual threads con `Executors.newVirtualThreadPerTaskExecutor()`
- Medir escalabilidad vs platform threads (mismo test con platform threads)
- Medir métricas: PostgreSQL pool bajo carga, Redis cache hit ratio, memory usage
- Comparar rendimiento: virtual threads vs platform threads con misma carga

**No es obligatorio**: Puedes aplicar esta técnica a cualquier escenario de alta concurrencia I/O.

---

## Requisitos Técnicos

- **Lenguaje**: Java (21+ recomendado para virtual threads)
- **Build tool**: Maven (ya configurado en el proyecto)
- **Infraestructura**: PostgreSQL 16 + Redis 7 con Docker
- **Connection Pool**: HikariCP para PostgreSQL
- **Estructura**: Paquetes separados por cada módulo
- **Medición de tiempo**: Cada módulo debe medir y reportar su tiempo de ejecución
- **Medición de recursos**: PostgreSQL pool metrics, Redis metrics, JVM metrics
- **Comparación**: Incluir análisis comparativo de rendimiento entre enfoques
- **Caching**: Implementar Redis cache-aside pattern donde aplique

---

## Entregables

1. **Código fuente** de cada módulo aplicando la técnica correspondiente
2. **Documentación** breve explicando qué técnica se usó y por qué
3. **Mediciones de rendimiento** de cada módulo (tiempo de ejecución)
4. **Mediciones de consumo** de PostgreSQL y Redis (connection pool, cache hit ratio, memory)
5. **Análisis comparativo**: ventajas/desventajas de cada enfoque en el contexto financiero
6. **Dashboard funcional** que integre los módulos (puede ser CLI o simple UI)
7. **Docker Compose** configuración para PostgreSQL y Redis
8. **Scripts SQL** para inicialización de tablas y datos de prueba

---

## Flexibilidad

- Los módulos sugeridos son **orientativos**, no obligatorios
- Puedes aplicar cada técnica a diferentes escenarios de negocio financiero
- Lo importante es demostrar comprensión de **cuándo y por qué** usar cada técnica
- El dashboard puede ser tan simple o complejo como desees

---

## Preguntas Guía para el Diseño

- ¿Qué operaciones son inherentemente secuenciales en tu dominio financiero?
- ¿Qué operaciones pueden ejecutarse en paralelo sin dependencias?
- ¿Qué cálculos son CPU-bound y se beneficiarían de procesamiento paralelo?
- ¿Dónde tendrías alta concurrencia de operaciones I/O?
- ¿Cómo medirías y compararías el rendimiento de cada enfoque?
