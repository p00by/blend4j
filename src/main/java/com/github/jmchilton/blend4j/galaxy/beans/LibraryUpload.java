package com.github.jmchilton.blend4j.galaxy.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class LibraryUpload extends GalaxyObject {
  private String folderId;
  private String fileType = "auto";
  private String dbkey = "?";
  private String content;
  private String name;
  private final String uploadOption;
  private CreateType createType = CreateType.FILE;

  public static enum CreateType {
    FILE("file");
    private String value;

    private CreateType(final String value) {
      this.value = value;
    }

    public String toJson() {
      return value;
    }
  }

  protected LibraryUpload(final String uploadOption) {
    this.uploadOption = uploadOption;
  }

  @JsonProperty("upload_option")
  public String getUploadOption() {
    return uploadOption;
  }

  @JsonIgnore
  public String getContent() {
    return content;
  }

  @JsonIgnore
  public void setContent(final String content) {
    this.content = content;
  }

  @JsonProperty("folder_id")
  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  @JsonProperty("file_type")
  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getDbkey() {
    return dbkey;
  }

  public void setDbkey(String dbkey) {
    this.dbkey = dbkey;
  }

  public void setCreateType(final CreateType createType) {
    this.createType = createType;
  }

  @JsonProperty("create_type")
  public String getCreateType() {
    return createType.toJson();
  }

  @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
  @JsonProperty("NAME")
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }
}