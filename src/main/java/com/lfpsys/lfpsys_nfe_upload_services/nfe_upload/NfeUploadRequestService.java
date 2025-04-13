package com.lfpsys.lfpsys_nfe_upload_services.nfe_upload;

import static com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaTopicNames.FINANCIAL_CONTROL;
import static com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaTopicNames.PRODUCTS;
import static com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaTopicNames.STOCKS;
import static com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaTopicNames.TAX_CALCULATION;
import static com.lfpsys.lfpsys_nfe_upload_services.nfe_upload.NfeUploadProcessStatus.COMPLETED;
import static com.lfpsys.lfpsys_nfe_upload_services.nfe_upload.NfeUploadProcessStatus.ERROR;
import static com.lfpsys.lfpsys_nfe_upload_services.nfe_upload.NfeUploadProcessStatus.IN_PROGRESS;
import static java.lang.String.format;
import static java.math.BigDecimal.TEN;
import static java.time.Duration.ofDays;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaProducerService;
import com.lfpsys.lfpsys_nfe_upload_services.kafka.KafkaTopicNames;
import com.lfpsys.lfpsys_nfe_upload_services.kafka.events.FinancialControlEvent;
import com.lfpsys.lfpsys_nfe_upload_services.kafka.events.NfeUploadEvent;
import com.lfpsys.lfpsys_nfe_upload_services.kafka.events.ProductsEvent;
import com.lfpsys.lfpsys_nfe_upload_services.kafka.events.StocksEvent;
import com.lfpsys.lfpsys_nfe_upload_services.kafka.events.TaxCalculationsEvent;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NfeUploadRequestService {

  private static final String REDIS_KEY_PREFIX = "LFPSys:NFe_Upload:%s";
  private static final Logger log = LoggerFactory.getLogger(NfeUploadRequestService.class);

  private final ObjectMapper objectMapper;
  private final KafkaProducerService kafkaProducerService;
  private final RedisTemplate<String, String> redisTemplate;

  public NfeUploadRequestService(
      final ObjectMapper objectMapper,
      final KafkaProducerService kafkaProducerService,
      final RedisTemplate<String, String> redisTemplate) {
    this.objectMapper = objectMapper;
    this.kafkaProducerService = kafkaProducerService;
    this.redisTemplate = redisTemplate;
  }

  public NfeUploadStatusDto recoveryNfeUploadStatus(final UUID protocolId) {
    try {
      final var redisKey = format(REDIS_KEY_PREFIX, protocolId);

      final var redisDataInString = redisTemplate
          .opsForValue()
          .get(redisKey);

      if (hasText(redisDataInString)) {
        return objectMapper.readValue(redisDataInString, NfeUploadStatusDto.class);
      }
      return buildDefaultNfeUploadStatusDto(ERROR);
    } catch (JsonProcessingException ex) {
      return buildDefaultNfeUploadStatusDto(ERROR);
    }
  }

  @Transactional
  public NfeUploadResponse uploadNfe(final UUID clientId, final String fileName, final String xmlContent) {
    final var response = new NfeUploadResponse(UUID.randomUUID());

    try {
      final var defaultNfeUploadStatus = buildDefaultNfeUploadStatusDto(IN_PROGRESS);

      final var defaultNfeUploadStatusString = objectMapper.writeValueAsString(defaultNfeUploadStatus);

      final var redisKey = format(REDIS_KEY_PREFIX, response.getProtocolId());

      redisTemplate.opsForValue().set(redisKey, defaultNfeUploadStatusString, ofDays(30L));

      kafkaProducerService
          .sendMessage(
              response.getProtocolId().toString(),
              KafkaTopicNames.NFE_UPLOAD,
              objectMapper.writeValueAsString(new NfeUploadEvent(xmlContent)),
              clientId);

      kafkaProducerService
          .sendMessage(
              response.getProtocolId().toString(),
              PRODUCTS,
              objectMapper.writeValueAsString(new ProductsEvent().builder("Any Product Name", TEN)),
              clientId);

      kafkaProducerService
          .sendMessage(
              response.getProtocolId().toString(),
              STOCKS,
              objectMapper.writeValueAsString(new StocksEvent().builder("ADD", TEN)),
              clientId);

      kafkaProducerService
          .sendMessage(
              response.getProtocolId().toString(),
              FINANCIAL_CONTROL,
              objectMapper.writeValueAsString(new FinancialControlEvent().builder(response.getProtocolId(), "ACCOUNTS_PAYABLE ", TEN, clientId)),
              clientId);

      kafkaProducerService
          .sendMessage(
              response.getProtocolId().toString(),
              TAX_CALCULATION,
              objectMapper.writeValueAsString(new TaxCalculationsEvent().builder(response.getProtocolId(), UUID.randomUUID(), "SUBTRACT ", TEN)),
              clientId);
    } catch (JsonProcessingException ex) {
      log.error("[{}] - Error processing NFe for clientId: {} and fileName: {}.",
                this.getClass().getSimpleName(), clientId, fileName);
    }
    return response;
  }

  private NfeUploadStatusDto buildDefaultNfeUploadStatusDto(final NfeUploadProcessStatus status) {
    final var processes = Stream
        .of(NfeUploadProcessType.values())
        .map(type -> new NfeUploadProcessDto(type, status))
        .toList();

    return new NfeUploadStatusDto(processes);
  }

  public void processNfeUpload(final UUID protocolId) throws JsonProcessingException {
    final var redisKey = format(REDIS_KEY_PREFIX, protocolId);
    final var status = objectMapper.readValue(redisTemplate.opsForValue().get(redisKey), NfeUploadStatusDto.class);

    status
        .getProcesses()
        .forEach(nfeUploadProcess -> {
          if (NfeUploadProcessType.NFE_UPLOAD.equals(nfeUploadProcess.getProcess())) {
            nfeUploadProcess.setStatus(COMPLETED);
          }
        });

    redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(status));
  }
}
