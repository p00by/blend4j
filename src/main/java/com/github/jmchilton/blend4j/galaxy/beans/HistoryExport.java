package com.github.jmchilton.blend4j.galaxy.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class HistoryExport {
  private String downloadUrl;
  
  public HistoryExport() {
    this.downloadUrl = null;
  }
  
  public HistoryExport(final String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }
  
  public boolean isReady() {
    return this.downloadUrl != null;
  }
  
  public String getDownloadUrl() {
    return this.downloadUrl;
  }

  @JsonProperty("download_url")
  public void setDownloadUrl(final String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }
  
}
