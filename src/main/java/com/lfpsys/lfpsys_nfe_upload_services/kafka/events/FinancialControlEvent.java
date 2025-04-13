package com.lfpsys.lfpsys_nfe_upload_services.kafka.events;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class FinancialControlEvent {

  private List<PaymentEvent> payments;

  public FinancialControlEvent() {}

  public FinancialControlEvent(List<PaymentEvent> payments) {
    this.payments = payments;
  }

  public List<PaymentEvent> getPayments() {
    return payments;
  }

  public void setPayments(List<PaymentEvent> payments) {
    this.payments = payments;
  }

  public FinancialControlEvent builder(final UUID protocolId, final String operation, final BigDecimal value, final UUID clientOrSupplierId) {
    final var payment = new PaymentEvent(protocolId, operation, value, clientOrSupplierId);

    return new FinancialControlEvent(List.of(payment));
  }

  private static class PaymentEvent {

    private UUID nfeId;
    private String operation;
    private BigDecimal value;
    private UUID clientOrSupplierId;

    public PaymentEvent(UUID nfeId, String operation, BigDecimal value, UUID clientOrSupplierId) {
      this.nfeId = nfeId;
      this.operation = operation;
      this.value = value;
      this.clientOrSupplierId = clientOrSupplierId;
    }

    public UUID getNfeId() {
      return nfeId;
    }

    public void setNfeId(UUID nfeId) {
      this.nfeId = nfeId;
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

    public UUID getClientOrSupplierId() {
      return clientOrSupplierId;
    }

    public void setClientOrSupplierId(UUID clientOrSupplierId) {
      this.clientOrSupplierId = clientOrSupplierId;
    }
  }
}
