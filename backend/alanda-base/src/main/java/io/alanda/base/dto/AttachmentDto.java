package io.alanda.base.dto;

import java.io.Serializable;

public class AttachmentDto implements Serializable {

  private static final long serialVersionUID = 6324487997671018697L;

  private String name;

  private String contentType;

  private byte[] content;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

}
