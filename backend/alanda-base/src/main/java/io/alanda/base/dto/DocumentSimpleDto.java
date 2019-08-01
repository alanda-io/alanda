package io.alanda.base.dto;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author jlo
 */

public class DocumentSimpleDto {

  private String authorName;

  private String guid;

  private String name;

  private String path;

  private String mediaType;

  private String lastModified;

  private Long size;

  private boolean folder = false;

  private String versionString;

  private List<String> label;

  private boolean checkedOut;

  @JsonIgnore
  private InputStream intputStream;

  public DocumentSimpleDto() {

  }

  public DocumentSimpleDto(String name, String mediaType, Long size) {
    this.name = name;
    this.mediaType = mediaType;
    this.size = size;
  }

  public DocumentSimpleDto(String guid, String name, String path, String lastModified, String mediaType, Long size) {
    this.guid = guid;
    this.name = name;
    this.path = path;
    this.lastModified = lastModified;
    this.mediaType = mediaType;
    this.size = size;
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public InputStream getIntputStream() {
    return intputStream;
  }

  public void setIntputStream(InputStream intputStream) {
    this.intputStream = intputStream;
  }

  public boolean isFolder() {
    return folder;
  }

  public void setFolder(boolean folder) {
    this.folder = folder;
  }

  @Override
  public String toString() {
    return "DocumentSimpleDto [" +
      (guid != null ? "guid=" + guid + ", " : "") +
      (name != null ? "name=" + name + ", " : "") +
      (path != null ? "path=" + path + ", " : "") +
      (versionString != null ? "versionString=" + versionString + ", " : "") +
      (label != null ? "label=" + label + ", " : "") +
      (mediaType != null ? "mediaType=" + mediaType + ", " : "") +
      (lastModified != null ? "lastModified=" + lastModified + ", " : "") +
      (size != null ? "size=" + size : "") +
      "]";
  }

  public String getVersionString() {
    return versionString;
  }

  public void setVersionString(String versionString) {
    this.versionString = versionString;
  }

  public List<String> getLabel() {
    return label;
  }

  public void setLabel(List<String> label) {
    this.label = label;
  }

  public boolean isCheckedOut() {
    return checkedOut;
  }

  public void setCheckedOut(boolean checkedOut) {
    this.checkedOut = checkedOut;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

}
