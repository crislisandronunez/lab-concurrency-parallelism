import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyProbe {

  public static void main(String[] args) throws InterruptedException {
    int requests = args.length > 0 ? Integer.parseInt(args[0]) : 30;
    String accountId = args.length > 1 ? args[1] : "10000000-0000-0000-0000-000000000002";
    String url = args.length > 2 ? args[2] : "http://localhost:8080/api/transactions";
    String body = """
        {"accountId":"%s","type":"DEBIT","amount":1000,"category":"FOOD"}
        """.formatted(accountId);

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(30))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();

    CountDownLatch ready = new CountDownLatch(requests);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(requests);
    AtomicInteger created = new AtomicInteger();
    AtomicInteger conflicts = new AtomicInteger();
    AtomicInteger errors = new AtomicInteger();

    System.out.printf("Iniciando %d debitos simultaneos de $1.000 a %s%n", requests, accountId);
    try (HttpClient client = HttpClient.newHttpClient()) {
      for (int i = 0; i < requests; i++) {
        Thread.ofVirtual().start(() -> {
          ready.countDown();
          try {
            start.await();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            switch (response.statusCode()) {
              case 201 -> created.incrementAndGet();
              case 409 -> conflicts.incrementAndGet();
              default -> {
                errors.incrementAndGet();
                System.out.printf("HTTP %d: %s%n", response.statusCode(), response.body());
              }
            }
          } catch (Exception e) {
            errors.incrementAndGet();
            System.out.println("Error de solicitud: " + e.getMessage());
          } finally {
            finished.countDown();
          }
        });
      }
      ready.await();
      long startTime = System.nanoTime();
      start.countDown();
      finished.await();
      System.out.printf("%nFinalizado en %d ms%n", (System.nanoTime() - startTime) / 1_000_000);
      System.out.printf("201 Created: %d%n409 Conflict: %d%nOtros errores: %d%n",
          created.get(), conflicts.get(), errors.get());
      System.out.printf("Debito total esperado en BD: $%,d%n", created.get() * 1000L);
    }
  }
}
