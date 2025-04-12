package com.lfpsys.lfpsys_nfe_upload_services.nfe_upload;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
