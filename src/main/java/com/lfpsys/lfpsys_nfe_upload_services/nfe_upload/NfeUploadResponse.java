package com.lfpsys.lfpsys_nfe_upload_services.nfe_upload;

import java.util.UUID;

public class NfeUploadResponse {

  private UUID protocolId;

  public NfeUploadResponse(UUID protocolId) {
    this.protocolId = protocolId;
  }

  public UUID getProtocolId() {
    return protocolId;
  }

  public void setProtocolId(UUID protocolId) {
    this.protocolId = protocolId;
  }
}
