package com.lfpsys.lfpsys_nfe_upload_services.kafka.consumers;

import static com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaTopicNames.NFE_UPLOAD;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lfpsys.lfpsys_nfe_upload_services.nfe_upload.NfeUploadRequestService;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NfeUploadConsumer {

  private final NfeUploadRequestService nfeUploadRequestService;

  public NfeUploadConsumer(final NfeUploadRequestService nfeUploadRequestService) {
    this.nfeUploadRequestService = nfeUploadRequestService;
  }

  @KafkaListener(topics = NFE_UPLOAD, groupId = "group_id")
  public void consumeMessage(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
    nfeUploadRequestService.processNfeUpload(UUID.fromString(consumerRecord.key()));
  }
}
