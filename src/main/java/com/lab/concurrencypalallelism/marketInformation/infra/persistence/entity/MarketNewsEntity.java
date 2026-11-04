package com.lab.concurrencypalallelism.marketInformation.infra.persistence.entity;

import com.lab.concurrencypalallelism.marketInformation.domain.enums.Sentiment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "market_news")
public class MarketNewsEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "source", nullable = false, length = 50)
  private String source;

  @Column(name = "timestamp", nullable = false, updatable = false)
  private Instant timeStamp;

  @Column(name = "sentiment", nullable = false)
  @Enumerated(EnumType.STRING)
  private Sentiment sentiment;

  protected MarketNewsEntity() {
  }

  public MarketNewsEntity(UUID id, String title, String source,
                          Instant timeStamp, Sentiment sentiment) {
    this.id = id;
    this.title = title;
    this.source = source;
    this.timeStamp = timeStamp;
    this.sentiment = sentiment;
  }

  public UUID getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getSource() {
    return source;
  }

  public Instant getTimeStamp() {
    return timeStamp;
  }

  public Sentiment getSentiment() {
    return sentiment;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof MarketNewsEntity that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
