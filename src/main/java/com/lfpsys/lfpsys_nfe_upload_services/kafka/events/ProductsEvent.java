package com.lfpsys.lfpsys_nfe_upload_services.kafka.events;

import java.math.BigDecimal;
import java.util.List;

public class ProductsEvent {

  private List<ProductEvent> products;

  public ProductsEvent() {}

  public ProductsEvent(List<ProductEvent> products) {
    this.products = products;
  }

  public List<ProductEvent> getProducts() {
    return products;
  }

  public void setProducts(List<ProductEvent> products) {
    this.products = products;
  }

  public ProductsEvent builder(final String name, final BigDecimal value) {
    final var product = new ProductEvent(name, value);

    return new ProductsEvent(List.of(product));
  }

  private static class ProductEvent {

    private String name;
    private BigDecimal value;

    public ProductEvent(String name, BigDecimal value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public BigDecimal getValue() {
      return value;
    }

    public void setValue(BigDecimal value) {
      this.value = value;
    }
  }
}
