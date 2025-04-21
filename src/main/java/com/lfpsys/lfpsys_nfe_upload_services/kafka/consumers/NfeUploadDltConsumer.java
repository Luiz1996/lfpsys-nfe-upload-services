package com.lfpsys.lfpsys_nfe_upload_services.kafka.consumers;

import static com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaTopicNames.NFE_UPLOAD_DLT;

import com.lfpsys.lfpsys_nfe_upload_services.nfe_upload.NfeUploadRequestService;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NfeUploadDltConsumer {

  private static final Logger logger = LoggerFactory.getLogger(NfeUploadDltConsumer.class);
  private final NfeUploadRequestService nfeUploadRequestService;

  public NfeUploadDltConsumer(final NfeUploadRequestService nfeUploadRequestService) {
    this.nfeUploadRequestService = nfeUploadRequestService;
  }

  @KafkaListener(topics = NFE_UPLOAD_DLT, groupId = "group_id")
  public void consumeMessage(ConsumerRecord<String, String> consumerRecord) {
    try {
      nfeUploadRequestService.processDltNfeUpload(UUID.fromString(consumerRecord.key()));
    } catch (Exception ex) {
      logger.error("Error: {}", ex.getMessage());
    }
  }
}
