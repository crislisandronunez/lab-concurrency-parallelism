import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarketInformationProbe {

  private static final List<String> RESOURCES = List.of("stocks", "exchange_rate", "market_news");
  private static final Pattern METRIC_VALUE = Pattern.compile("\\\"value\\\"\\s*:\\s*(\\d+(?:\\.\\d+)?)");
  private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(15);

  private final Scanner scanner = new Scanner(System.in);
  private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
  private final String baseUrl;
  private final String symbol;
  private final String fromCurrency;
  private final String toCurrency;
  private final int requests;

  public MarketInformationProbe(String[] args) {
    baseUrl = (args.length > 0 ? args[0] : "http://localhost:8080").replaceAll("/+$", "");
    symbol = args.length > 1 ? args[1] : "AAPL";
    fromCurrency = args.length > 2 ? args[2] : "USD";
    toCurrency = args.length > 3 ? args[3] : "EUR";
    requests = args.length > 4 ? Integer.parseInt(args[4]) : 10;
  }

  public static void main(String[] args) {
    try {
      new MarketInformationProbe(args).menu();
    } catch (Exception exception) {
      System.err.println("No se pudo iniciar el script: " + exception.getMessage());
    }
  }

  private void menu() {
    System.out.printf("%nMódulo marketInformation | %s | %s | %s -> %s | %d solicitudes%n",
        baseUrl, symbol, fromCurrency, toCurrency, requests);

    while (true) {
      System.out.println("""

          1  Compilación y arranque
          2  Consulta REST
          3  Redis: cache miss y escritura
          4  Redis: cache hit
          5  Redis: fallo y recuperación (detiene Redis con confirmación)
          6  PostgreSQL: reintentos (simulación automática de fallo transitorio)
          7  Timeout de 5 segundos (requiere lentitud provocada)
          8  Concurrencia y métricas
          9  Errores HTTP 400
          10 Error HTTP 503 (simulación MVC automática)
          11 Error HTTP 500 (simulación MVC automática)
          A  Pruebas automáticas no destructivas (1, 2, 3, 4, 8, 9)
          0  Salir
          """);
      System.out.print("Opción: ");
      String option = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
      try {
        switch (option) {
          case "1" -> compilationAndStartup();
          case "2" -> restQuery();
          case "3" -> cacheMiss();
          case "4" -> cacheHit();
          case "5" -> redisFailure();
          case "6" -> retryVerification();
          case "7" -> manualFailure("TIMEOUT", 504, false);
          case "8" -> concurrencyAndMetrics();
          case "9" -> badRequests();
          case "10" -> verifyHttpError(503);
          case "11" -> verifyHttpError(500);
          case "A" -> {
            compilationAndStartup();
            restQuery();
            cacheMiss();
            cacheHit();
            concurrencyAndMetrics();
            badRequests();
          }
          case "0" -> {
            client.close();
            return;
          }
          default -> System.out.println("Opción no válida.");
        }
      } catch (Exception exception) {
        result("ERROR", exception.getMessage());
      }
    }
  }

  private void compilationAndStartup() throws Exception {
    section("1. Compilación y arranque");
    String wrapper = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
        ? "mvnw.cmd" : "mvnw";
    Path path = Path.of(wrapper).toAbsolutePath();
    if (!Files.exists(path)) {
      result("PENDIENTE", "Ejecutá el script desde la raíz del repositorio (no se encontró " + wrapper + ").");
      return;
    }
    ProcessBuilder build = new ProcessBuilder(path.toString(), "-q", "-DskipTests", "compile");
    build.inheritIO();
    int exitCode = build.start().waitFor();
    result(exitCode == 0 ? "OK" : "FALLO", "Compilación Maven: salida " + exitCode);
    try {
      HttpResponse<String> health = get(baseUrl + "/actuator/health");
      result(health.statusCode() == 200 ? "OK" : "FALLO",
          "Aplicación levantada: HTTP " + health.statusCode() + " | " + health.body());
    } catch (Exception exception) {
      result("FALLO", "No responde /actuator/health: " + exception.getMessage());
    }
  }

  private void restQuery() throws Exception {
    section("2. Consulta REST");
    HttpResponse<String> response = get(url(symbol, fromCurrency, toCurrency));
    boolean stock = containsJsonObject(response.body(), "stockResult");
    boolean exchange = containsJsonArray(response.body(), "exchangeRateResult");
    boolean news = containsJsonArray(response.body(), "marketNewsResult");
    result(response.statusCode() == 200 && stock && exchange && news ? "OK" : "FALLO",
        "HTTP " + response.statusCode() + " | acción=" + stock
            + ", tipos de cambio=" + exchange + ", noticias=" + news);
    if (response.statusCode() != 200) {
      System.out.println(response.body());
    }
  }

  private void cacheMiss() throws Exception {
    section("3. Redis: cache miss");
    CommandResult deleted = redis("DEL", stockKey(), exchangeKey(), "market-news:latest-five");
    if (deleted.exitCode() != 0) {
      result("PENDIENTE", "Redis no disponible por docker compose: " + deleted.output());
      return;
    }
    double[] before = cacheCounts("miss");
    HttpResponse<String> response = get(url(symbol, fromCurrency, toCurrency));
    CommandResult persisted = redis("EXISTS", stockKey(), exchangeKey(), "market-news:latest-five");
    double[] after = cacheCounts("miss");
    boolean keysCreated = persisted.exitCode() == 0 && persisted.output().trim().equals("3");
    result(response.statusCode() == 200 && keysCreated ? "OK" : "FALLO",
        "HTTP " + response.statusCode() + " | claves guardadas en Redis: " + persisted.output().trim() + "/3");
    printCounterDifferences("miss", before, after);
  }

  private void cacheHit() throws Exception {
    section("4. Redis: cache hit");
    CommandResult present = redis("EXISTS", stockKey(), exchangeKey(), "market-news:latest-five");
    if (present.exitCode() != 0) {
      result("PENDIENTE", "No se puede consultar Redis: " + present.output());
      return;
    }
    if (!present.output().trim().equals("3")) {
      get(url(symbol, fromCurrency, toCurrency));
    }
    double[] before = cacheCounts("hit");
    HttpResponse<String> response = get(url(symbol, fromCurrency, toCurrency));
    double[] after = cacheCounts("hit");
    boolean allHits = increasedForAll(before, after);
    result(response.statusCode() == 200 && allHits ? "OK" : "FALLO",
        "HTTP " + response.statusCode() + " | las tres métricas de hit aumentaron: " + allHits);
    printCounterDifferences("hit", before, after);
  }

  private void redisFailure() throws Exception {
    section("5. Redis: fallo y recuperación");
    System.out.print("¿Detener temporalmente el servicio Redis de docker compose? (s/N): ");
    if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
      result("PENDIENTE", "Prueba cancelada sin modificar Redis.");
      return;
    }
    CommandResult deleted = redis("DEL", stockKey(), exchangeKey(), "market-news:latest-five");
    if (deleted.exitCode() != 0) {
      result("PENDIENTE", "Redis no estaba disponible: " + deleted.output());
      return;
    }
    CommandResult stopped = runCommand(30, "docker", "compose", "stop", "redis");
    if (stopped.exitCode() != 0) {
      result("FALLO", "No se pudo detener Redis: " + stopped.output());
      return;
    }
    try {
      HttpResponse<String> response = get(url(symbol, fromCurrency, toCurrency));
      result(response.statusCode() == 200 ? "OK" : "FALLO",
          "Redis detenido, consulta a PostgreSQL: HTTP " + response.statusCode());
      if (response.statusCode() != 200) {
        System.out.println(response.body());
      }
    } finally {
      CommandResult started = runCommand(30, "docker", "compose", "start", "redis");
      result(started.exitCode() == 0 ? "OK" : "FALLO",
          "Restauración de Redis: " + started.output().trim());
    }
  }

  private void retryVerification() throws Exception {
    section("6. Reintentos de PostgreSQL: fallo transitorio controlado");
    System.out.println("Se comprueba el @Retryable real de RetryStocksQuery con un repositorio simulado.");
    System.out.println("No se detiene PostgreSQL ni se modifica ningún archivo de la aplicación.");

    String wrapper = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
        ? "mvnw.cmd" : "mvnw";
    Path maven = Path.of(wrapper).toAbsolutePath();
    if (!Files.exists(maven)) {
      result("PENDIENTE", "Ejecutá el script desde la raíz del proyecto (falta " + wrapper + ").");
      return;
    }

    Path temporaryDirectory = Files.createTempDirectory("market-information-retry-");
    Path classpathFile = temporaryDirectory.resolve("classpath.txt");
    Path verificationFile = temporaryDirectory.resolve("RetryVerification.java");

    try {
      Files.writeString(verificationFile, RETRY_VERIFICATION_SOURCE, StandardCharsets.UTF_8);

      CommandResult dependencies = runCommand(180,
          maven.toString(), "-q", "-DskipTests", "compile", "dependency:build-classpath",
          "-Dmdep.outputFile=" + classpathFile.toAbsolutePath());

      if (dependencies.exitCode() != 0 || !Files.exists(classpathFile)) {
        result("PENDIENTE", "No se pudo preparar el classpath de Maven: " + dependencies.output());
        return;
      }

      String classpath = Path.of("target/classes").toAbsolutePath()
          + File.pathSeparator + Files.readString(classpathFile, StandardCharsets.UTF_8).trim();
      String java = Path.of(System.getProperty("java.home"), "bin",
          System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
              ? "java.exe" : "java").toString();

      CommandResult verification = runCommand(60,
          java, "--class-path", classpath, verificationFile.toString());
      if (!verification.output().isBlank()) {
        System.out.println(verification.output());
      }
      result(verification.exitCode() == 0 ? "OK" : "FALLO",
          "Validación de @Retryable (salida " + verification.exitCode() + ")");
    } finally {
      try (var paths = Files.walk(temporaryDirectory)) {
        paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
          try {
            Files.deleteIfExists(path);
          } catch (IOException ignored) {
          }
        });
      }
    }
  }

  private static final String RETRY_VERIFICATION_SOURCE = """
      import com.lab.concurrencypalallelism.marketInformation.infra.retry.RetryStocksQuery;
      import com.lab.concurrencypalallelism.marketInformation.infra.persistence.port.out.postgres.StocksRepository;
      import java.lang.reflect.Proxy;
      import java.util.Optional;
      import java.util.concurrent.atomic.AtomicInteger;
      import org.springframework.context.annotation.AnnotationConfigApplicationContext;
      import org.springframework.context.annotation.Bean;
      import org.springframework.context.annotation.Configuration;
      import org.springframework.dao.QueryTimeoutException;
      import org.springframework.resilience.annotation.EnableResilientMethods;

      public class RetryVerification {
        private static final AtomicInteger attempts = new AtomicInteger();
        private static int failuresBeforeSuccess;

        @Configuration
        @EnableResilientMethods
        static class ConfigurationForRetry {
          @Bean
          StocksRepository stocksRepository() {
            return (StocksRepository) Proxy.newProxyInstance(
                StocksRepository.class.getClassLoader(),
                new Class<?>[]{StocksRepository.class},
                (proxy, method, arguments) -> {
                  if (method.getName().equals("findById")) {
                    int currentAttempt = attempts.incrementAndGet();
                    if (currentAttempt <= failuresBeforeSuccess) {
                      throw new QueryTimeoutException("Error transitorio simulado en PostgreSQL");
                    }
                    return Optional.empty();
                  }
                  if (method.getName().equals("hashCode")) {
                    return System.identityHashCode(proxy);
                  }
                  if (method.getName().equals("equals")) {
                    return proxy == arguments[0];
                  }
                  if (method.getName().equals("toString")) {
                    return "SimulatedStocksRepository";
                  }
                  throw new UnsupportedOperationException(method.getName());
                });
          }

          @Bean
          RetryStocksQuery retryStocksQuery(StocksRepository stocksRepository) {
            return new RetryStocksQuery(stocksRepository);
          }
        }

        public static void main(String[] args) {
          try (var spring = new AnnotationConfigApplicationContext(ConfigurationForRetry.class)) {
            RetryStocksQuery retry = spring.getBean(RetryStocksQuery.class);

            attempts.set(0);
            failuresBeforeSuccess = 2;
            Optional<?> found = retry.findById("AAPL");
            if (attempts.get() != 3 || found.isPresent()) {
              throw new AssertionError("Recuperación incorrecta; intentos: " + attempts.get());
            }
            System.out.println("[OK] 2 fallos transitorios + 1 intento exitoso = 3 ejecuciones");

            attempts.set(0);
            failuresBeforeSuccess = 100;
            boolean exceptionThrown = false;
            try {
              retry.findById("AAPL");
            } catch (QueryTimeoutException exception) {
              exceptionThrown = true;
            }
            if (!exceptionThrown || attempts.get() != 4) {
              throw new AssertionError("Reintentos agotados incorrectamente; intentos: "
                  + attempts.get() + ", excepción propagada: " + exceptionThrown);
            }
            System.out.println("[OK] Error persistente: 1 llamada inicial + 3 reintentos = 4 ejecuciones");
          }
          System.exit(0);
        }
      }
      """;

  private void verifyHttpError(int expectedStatus) throws Exception {
    section((expectedStatus == 503 ? "10. HTTP 503" : "11. HTTP 500")
        + ": fallo simulado con Spring MVC");
    System.out.println("Se prueba el manejo de errores real del módulo en un contexto MVC aislado.");
    System.out.println("No se detienen servicios ni se modifica la aplicación en ejecución.");

    String wrapper = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
        ? "mvnw.cmd" : "mvnw";
    Path maven = Path.of(wrapper).toAbsolutePath();
    if (!Files.exists(maven)) {
      result("PENDIENTE", "Ejecutá el script desde la raíz del proyecto (falta " + wrapper + ").");
      return;
    }

    Path temporaryDirectory = Files.createTempDirectory("market-information-http-errors-");
    Path classpathFile = temporaryDirectory.resolve("classpath.txt");
    Path verificationFile = temporaryDirectory.resolve("HttpErrorVerification.java");

    try {
      Files.writeString(verificationFile, HTTP_ERROR_VERIFICATION_SOURCE, StandardCharsets.UTF_8);

      CommandResult dependencies = runCommand(180,
          maven.toString(), "-q", "-DskipTests", "compile", "dependency:build-classpath",
          "-Dmdep.includeScope=test", "-Dmdep.outputFile=" + classpathFile.toAbsolutePath());

      if (dependencies.exitCode() != 0 || !Files.exists(classpathFile)) {
        result("PENDIENTE", "No se pudo preparar el classpath de Maven: " + dependencies.output());
        return;
      }

      String classpath = Path.of("target/classes").toAbsolutePath()
          + File.pathSeparator + Files.readString(classpathFile, StandardCharsets.UTF_8).trim();
      String java = Path.of(System.getProperty("java.home"), "bin",
          System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
              ? "java.exe" : "java").toString();

      CommandResult verification = runCommand(60,
          java, "--class-path", classpath, verificationFile.toString(),
          Integer.toString(expectedStatus));
      if (!verification.output().isBlank()) {
        System.out.println(verification.output());
      }
      result(verification.exitCode() == 0 ? "OK" : "FALLO",
          "Verificación MVC de HTTP " + expectedStatus + " (salida "
              + verification.exitCode() + ")");
    } finally {
      try (var paths = Files.walk(temporaryDirectory)) {
        paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
          try {
            Files.deleteIfExists(path);
          } catch (IOException ignored) {
          }
        });
      }
    }
  }

  private static final String HTTP_ERROR_VERIFICATION_SOURCE = """
      package com.lab.concurrencypalallelism.marketInformation.infra.rest.controller;

      import java.util.concurrent.CompletionException;
      import org.springframework.dao.QueryTimeoutException;
      import org.springframework.test.web.servlet.MockMvc;
      import org.springframework.test.web.servlet.MvcResult;
      import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
      import org.springframework.test.web.servlet.setup.MockMvcBuilders;
      import org.springframework.web.bind.annotation.GetMapping;
      import org.springframework.web.bind.annotation.RequestMapping;
      import org.springframework.web.bind.annotation.RestController;

      public class HttpErrorVerification {
        @RestController
        @RequestMapping("/probe/market-information")
        public static class FailureController {
          @GetMapping("/database-error")
          public String databaseError() {
            throw new CompletionException(
                new QueryTimeoutException("Error transitorio simulado en PostgreSQL"));
          }

          @GetMapping("/unexpected-error")
          public String unexpectedError() {
            throw new IllegalStateException("Error inesperado simulado");
          }
        }

        public static void main(String[] args) throws Exception {
          int expectedStatus = Integer.parseInt(args[0]);
          String handlerPackage = "com.lab.concurrencypalallelism.marketInformation.infra.exception.";
          Object handler;
          try {
            handler = Class.forName(handlerPackage + "MarketInformationExceptionHandler")
                .getDeclaredConstructor().newInstance();
          } catch (ClassNotFoundException exception) {
            handler = Class.forName(handlerPackage + "GlobalExceptionHandler")
                .getDeclaredConstructor().newInstance();
          }

          MockMvc mvc = MockMvcBuilders.standaloneSetup(new FailureController())
              .setControllerAdvice(handler)
              .build();

          String path = expectedStatus == 503
              ? "/probe/market-information/database-error"
              : "/probe/market-information/unexpected-error";
          MvcResult response = mvc.perform(MockMvcRequestBuilders.get(path)).andReturn();
          int actualStatus = response.getResponse().getStatus();
          String body = response.getResponse().getContentAsString();

          String expectedStatusField = String.format("%cstatus%c:%d", 34, 34, expectedStatus);
          String expectedPathField = String.format("%cpath%c:%c%s%c", 34, 34, 34, path, 34);
          if (actualStatus != expectedStatus
              || !body.contains(expectedStatusField)
              || !body.contains(expectedPathField)) {
            throw new AssertionError("Esperado HTTP " + expectedStatus + ", recibido "
                + actualStatus + "; respuesta: " + body);
          }

          System.out.println("[OK] Spring MVC retornó HTTP " + actualStatus);
          System.out.println("[OK] El ErrorResponse contiene status y path correctos");
          System.exit(0);
        }
      }
      """;

  private void manualFailure(String title, int expectedHttpCode, boolean retryCheck) throws Exception {
    section(title + ": prueba con inyección de fallos");
    if (retryCheck) {
      System.out.println("Prepará un error PostgreSQL que Spring traduzca a TransientDataAccessException.");
      System.out.println("La respuesta HTTP 503 NO demuestra por sí sola los reintentos: verificá 3 reintentos en los logs.");
    } else if (expectedHttpCode == 504) {
      System.out.println("Prepará una consulta que tarde más de 5 segundos en PostgreSQL.");
    } else if (expectedHttpCode == 503) {
      System.out.println("Prepará un fallo transitorio de PostgreSQL que agote los reintentos.");
    } else {
      System.out.println("Prepará un fallo inesperado en el módulo de forma controlada.");
    }
    System.out.println("Asegurate de que la consulta NO se resuelva desde Redis.");
    System.out.print("Enter para ejecutar cuando la falla esté activa; 'n' para cancelar: ");
    if (scanner.nextLine().trim().equalsIgnoreCase("n")) {
      result("PENDIENTE", "Prueba cancelada.");
      return;
    }
    long start = System.nanoTime();
    HttpResponse<String> response = get(url(symbol, fromCurrency, toCurrency));
    long millis = Duration.ofNanos(System.nanoTime() - start).toMillis();
    result(response.statusCode() == expectedHttpCode ? "OK" : "FALLO",
        "Esperado HTTP " + expectedHttpCode + ", obtenido " + response.statusCode() + " en " + millis + " ms");
    System.out.println(response.body());
    if (retryCheck) {
      result("MANUAL", "Comprobar en logs la cantidad de reintentos; el HTTP no la informa.");
    }
  }

  private void concurrencyAndMetrics() throws Exception {
    section("8. Concurrencia y métricas");
    if (requests < 1) {
      result("FALLO", "La cantidad de solicitudes debe ser mayor a cero.");
      return;
    }
    get(url(symbol, fromCurrency, toCurrency));
    Double before = metricCount("lab.execution.duration", "method:getMarketinformation", "task:completable_future");
    CountDownLatch ready = new CountDownLatch(requests);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(requests);
    AtomicInteger ok = new AtomicInteger();
    AtomicInteger errors = new AtomicInteger();
    HttpRequest request = request(url(symbol, fromCurrency, toCurrency));
    for (int i = 0; i < requests; i++) {
      Thread.ofVirtual().start(() -> {
        ready.countDown();
        try {
          start.await();
          HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
          if (response.statusCode() == 200 && containsJsonObject(response.body(), "stockResult")) {
            ok.incrementAndGet();
          } else {
            errors.incrementAndGet();
            System.out.println("Respuesta concurrente: HTTP " + response.statusCode());
          }
        } catch (Exception exception) {
          errors.incrementAndGet();
          System.out.println("Solicitud fallida: " + exception.getMessage());
        } finally {
          finished.countDown();
        }
      });
    }
    ready.await();
    long startTime = System.nanoTime();
    start.countDown();
    finished.await();
    long millis = Duration.ofNanos(System.nanoTime() - startTime).toMillis();
    Double after = metricCount("lab.execution.duration", "method:getMarketinformation", "task:completable_future");
    result(errors.get() == 0 && ok.get() == requests ? "OK" : "FALLO",
        requests + " solicitudes simultáneas | HTTP 200: " + ok.get() + " | errores: "
            + errors.get() + " | total: " + millis + " ms");
    if (before != null && after != null) {
      result(after - before >= requests ? "OK" : "FALLO",
          "Micrometer lab.execution.duration: " + (after - before) + " ejecuciones registradas");
    } else {
      result("PENDIENTE", "No se pudo leer el contador de ejecución en Actuator.");
    }
    result("INFORMATIVO", "La respuesta HTTP no prueba por sí sola qué hilos ejecutaron los CompletableFuture.");
  }

  private void badRequests() throws Exception {
    section("Errores HTTP 400");
    expectStatus("Símbolo vacío", url("", fromCurrency, toCurrency), 400);
    expectStatus("Símbolo de más de 10 caracteres", url("ABCDEFGHIJK", fromCurrency, toCurrency), 400);
    expectStatus("Ambas monedas ausentes", url(symbol, null, null), 400);
    expectStatus("Moneda inválida", url(symbol, "INVALIDA", toCurrency), 400);
  }

  private void expectStatus(String description, String url, int status) throws Exception {
    HttpResponse<String> response = get(url);
    result(response.statusCode() == status ? "OK" : "FALLO",
        description + ": esperado HTTP " + status + ", obtenido " + response.statusCode());
  }

  private void printCounterDifferences(String resultName, double[] before, double[] after) {
    for (int i = 0; i < RESOURCES.size(); i++) {
      String description = RESOURCES.get(i) + "/" + resultName;
      if (Double.isNaN(before[i]) || Double.isNaN(after[i])) {
        result("PENDIENTE", "Métrica " + description + " no disponible (puede no haberse creado aún).");
      } else {
        result(after[i] > before[i] ? "OK" : "FALLO",
            "Métrica " + description + ": +" + (after[i] - before[i]));
      }
    }
  }

  private boolean increasedForAll(double[] before, double[] after) {
    for (int i = 0; i < RESOURCES.size(); i++) {
      if (Double.isNaN(before[i]) || Double.isNaN(after[i]) || after[i] <= before[i]) {
        return false;
      }
    }
    return true;
  }

  private double[] cacheCounts(String resultName) throws Exception {
    double[] values = new double[RESOURCES.size()];
    for (int i = 0; i < RESOURCES.size(); i++) {
      Double count = metricCount("lab.cache.requests", "resource:" + RESOURCES.get(i), "result:" + resultName);
      values[i] = count == null ? Double.NaN : count;
    }
    return values;
  }

  private Double metricCount(String meterName, String... tags) throws Exception {
    StringBuilder url = new StringBuilder(baseUrl).append("/actuator/metrics/").append(meterName);
    for (int i = 0; i < tags.length; i++) {
      url.append(i == 0 ? "?tag=" : "&tag=").append(encode(tags[i]));
    }
    HttpResponse<String> response = get(url.toString());
    if (response.statusCode() == 404) {
      return 0.0;
    }
    if (response.statusCode() != 200) {
      return null;
    }
    Matcher matcher = METRIC_VALUE.matcher(response.body());
    return matcher.find() ? Double.valueOf(matcher.group(1)) : null;
  }

  private CommandResult redis(String... args) throws Exception {
    String[] command = new String[6 + args.length];
    System.arraycopy(new String[]{"docker", "compose", "exec", "-T", "redis", "redis-cli"}, 0,
        command, 0, 6);
    System.arraycopy(args, 0, command, 6, args.length);
    return runCommand(20, command);
  }

  private CommandResult runCommand(int timeoutSeconds, String... command) throws Exception {
    Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
    Thread reader = Thread.ofVirtual().start(() -> {
      try (var input = process.getInputStream()) {
        input.transferTo(output);
      } catch (IOException ignored) {
      }
    });

    boolean completed = process.waitFor(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);
    if (!completed) {
      process.destroyForcibly();
      process.waitFor();
    }
    reader.join(5000);
    String details = output.toString(StandardCharsets.UTF_8).trim();
    if (!completed) {
      return new CommandResult(-1, "Tiempo de espera agotado ejecutando " + command[0]
          + (details.isBlank() ? "" : System.lineSeparator() + details));
    }
    return new CommandResult(process.exitValue(), details);
  }

  private HttpResponse<String> get(String url) throws IOException, InterruptedException {
    return client.send(request(url), HttpResponse.BodyHandlers.ofString());
  }

  private HttpRequest request(String url) {
    return HttpRequest.newBuilder(URI.create(url)).timeout(HTTP_TIMEOUT).GET().build();
  }

  private String url(String requestedSymbol, String requestedFrom, String requestedTo) {
    StringBuilder url = new StringBuilder(baseUrl).append("/api/market-information?symbol=")
        .append(encode(requestedSymbol));
    if (requestedFrom != null) {
      url.append("&fromCurrency=").append(encode(requestedFrom));
    }
    if (requestedTo != null) {
      url.append("&toCurrency=").append(encode(requestedTo));
    }
    return url.toString();
  }

  private String stockKey() {
    return "stock:" + symbol;
  }

  private String exchangeKey() {
    return "exchange-rate:" + fromCurrency + ":" + toCurrency;
  }

  private static boolean containsJsonObject(String json, String property) {
    return Pattern.compile("\\\"" + property + "\\\"\\s*:\\s*\\{").matcher(json).find();
  }

  private static boolean containsJsonArray(String json, String property) {
    return Pattern.compile("\\\"" + property + "\\\"\\s*:\\s*\\[").matcher(json).find();
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private static void section(String title) {
    System.out.println("\n--- " + title + " ---");
  }

  private static void result(String status, String description) {
    System.out.println("[" + status + "] " + description);
  }

  private record CommandResult(int exitCode, String output) {
  }
}
