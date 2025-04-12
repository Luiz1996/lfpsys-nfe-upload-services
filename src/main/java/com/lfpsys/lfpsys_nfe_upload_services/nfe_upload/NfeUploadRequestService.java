package com.lfpsys.lfpsys_nfe_upload_services.nfe_upload;

import static com.lfpsys.lfpsys_nfe_upload_services.nfe_upload.NfeUploadProcessStatus.ERROR;
import static java.lang.String.format;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NfeUploadRequestService {

  private static final String REDIS_KEY_PREFIX = "LFPSys:NFe_Upload:%s";

  private final ObjectMapper objectMapper;
  private final RedisTemplate<String, String> redisTemplate;

  public NfeUploadRequestService(
      final ObjectMapper objectMapper, final RedisTemplate<String, String> redisTemplate) {
    this.objectMapper = objectMapper;
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
      return buildDefaultNfeUploadStatusDto();
    } catch (JsonProcessingException ex) {
      return buildDefaultNfeUploadStatusDto();
    }
  }

  private NfeUploadStatusDto buildDefaultNfeUploadStatusDto() {
    final var processes = Stream
        .of(NfeUploadProcessType.values())
        .map(type -> new NfeUploadProcessDto(type, ERROR))
        .toList();

    return new NfeUploadStatusDto(processes);
  }
}
