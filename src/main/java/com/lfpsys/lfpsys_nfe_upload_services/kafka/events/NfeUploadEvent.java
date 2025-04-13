package com.lfpsys.lfpsys_nfe_upload_services.kafka.events;

public class NfeUploadEvent {

  private String xml;

  public NfeUploadEvent(String xml) {
    this.xml = xml;
  }

  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }
}
