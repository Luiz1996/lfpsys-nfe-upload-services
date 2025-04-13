package com.lfpsys.lfpsys_nfe_upload_services.kafka.events;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class StocksEvent {

  private List<ProductEvent> products;

  public StocksEvent() {}

  public StocksEvent(List<ProductEvent> products) {
    this.products = products;
  }

  public List<ProductEvent> getProducts() {
    return products;
  }

  public void setProducts(List<ProductEvent> products) {
    this.products = products;
  }

  public StocksEvent builder(final String name, final BigDecimal value) {
    final var product = new ProductEvent(UUID.randomUUID(), name, value);

    return new StocksEvent(List.of(product));
  }

  private static class ProductEvent {

    private UUID productId;
    private String operation;
    private BigDecimal value;

    public ProductEvent(UUID productId, String operation, BigDecimal value) {
      this.productId = productId;
      this.operation = operation;
      this.value = value;
    }

    public UUID getProductId() {
      return productId;
    }

    public void setProductId(UUID productId) {
      this.productId = productId;
    }

    public String getOperation() {
      return operation;
    }

    public void setOperation(String operation) {
      this.operation = operation;
    }

    public BigDecimal getValue() {
      return value;
    }

    public void setValue(BigDecimal value) {
      this.value = value;
    }
  }
}
