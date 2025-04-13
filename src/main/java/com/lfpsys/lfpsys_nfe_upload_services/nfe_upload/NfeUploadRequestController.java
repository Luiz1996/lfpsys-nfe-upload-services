package com.lfpsys.lfpsys_nfe_upload_services.nfe_upload;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/nfe-upload")
public class NfeUploadRequestController {

  private final NfeUploadRequestService service;

  public NfeUploadRequestController(NfeUploadRequestService service) {
    this.service = service;
  }

  @GetMapping("/{protocolId}/status")
  public ResponseEntity<NfeUploadStatusDto> recoveryNfeUploadStatus(final @PathVariable("protocolId") UUID protocolId) {
    return ResponseEntity.ok(service.recoveryNfeUploadStatus(protocolId));
  }

  @PostMapping
  public ResponseEntity<Object> uploadNfe(
      final @RequestParam("clientId") UUID clientId,
      final @RequestParam("file") MultipartFile file) {
    try {
      final var fileName = file.getOriginalFilename();
      final var content = new String(file.getBytes());

      return ResponseEntity.accepted().body(service.uploadNfe(clientId, fileName, content));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Error uploading NFe.");
    }
  }
}
