
package com.lab.concurrencypalallelism.marketInformation.model;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Sentiment;
import java.time.Instant;
import java.util.Objects;

public class MarketNews {

  private final String title;
  private final String source;
  private final Instant timestamp;
  private Sentiment sentiment;

  private MarketNews(String title, String source, Instant timestamp, Sentiment sentiment) {
    this.title = title;
    this.source = source;
    this.timestamp = timestamp;
    this.sentiment = sentiment;
  }

  public static MarketNews create(String title, String source, Sentiment sentiment) {
    validateData(title, source, sentiment);
    return new MarketNews(title.trim(), source.trim(), Instant.now(), sentiment);
  }

  public static MarketNews restore(
      String title, String source, Instant timestamp, Sentiment sentiment) {
    return new MarketNews(title, source, timestamp, sentiment);
  }

  private static void validateData(String title, String source, Sentiment sentiment) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("El título no puede ser nulo o vacío");
    }
    if (title.length() > 200) {
      throw new IllegalArgumentException("El título no puede superar los 200 caracteres");
    }
    if (source == null || source.isBlank()) {
      throw new IllegalArgumentException("La fuente no puede ser nula o vacía");
    }
    if (source.length() > 50) {
      throw new IllegalArgumentException("La fuente no puede superar los 50 caracteres");
    }
    if (sentiment == null) {
      throw new IllegalArgumentException("El sentimiento no puede ser nulo");
    }
  }

  public void updateSentiment(Sentiment sentiment) {
    if (sentiment == null) {
      throw new IllegalArgumentException("El sentimiento no puede ser nulo");
    }
    this.sentiment = sentiment;
  }

  public String getTitle() {
    return title;
  }

  public String getSource() {
    return source;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public Sentiment getSentiment() {
    return sentiment;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof MarketNews that)) return false;
    return Objects.equals(title, that.title);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(title);
  }
}
