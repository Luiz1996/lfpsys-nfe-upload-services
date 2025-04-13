package com.lfpsys.lfpsys_nfe_upload_services.kafka;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  public KafkaProducerService(final KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(final String protocolId, final String topic, final String message, final UUID clientId) {
    final List<Header> headers = List.of(new RecordHeader("client_id", clientId.toString().getBytes(UTF_8)));
    final ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, 0, protocolId, message, headers);

    kafkaTemplate.send(producerRecord);
  }
}