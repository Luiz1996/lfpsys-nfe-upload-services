package com.lfpsys.lfpsys_nfe_upload_services.kafka.events;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TaxCalculationsEvent {

  private UUID nfeId;
  private List<TaxCalculationEvent> taxCalculations;

  public TaxCalculationsEvent() {}

  public TaxCalculationsEvent(UUID nfeId, List<TaxCalculationEvent> taxCalculations) {
    this.nfeId = nfeId;
    this.taxCalculations = taxCalculations;
  }

  public UUID getNfeId() {
    return nfeId;
  }

  public void setNfeId(UUID nfeId) {
    this.nfeId = nfeId;
  }

  public List<TaxCalculationEvent> getTaxCalculations() {
    return taxCalculations;
  }

  public void setTaxCalculation(List<TaxCalculationEvent> taxCalculations) {
    this.taxCalculations = taxCalculations;
  }

  public TaxCalculationsEvent builder(final UUID protocolId, final UUID taxId, final String operation, final BigDecimal value) {
    final var taxCalculation = new TaxCalculationEvent(taxId, operation, value);

    return new TaxCalculationsEvent(protocolId, List.of(taxCalculation));
  }

  private static class TaxCalculationEvent {

    private UUID taxId;
    private String operation;
    private BigDecimal value;

    public TaxCalculationEvent(UUID taxId, String operation, BigDecimal value) {
      this.taxId = taxId;
      this.operation = operation;
      this.value = value;
    }

    public UUID getTaxId() {
      return taxId;
    }

    public void setTaxId(UUID taxId) {
      this.taxId = taxId;
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
