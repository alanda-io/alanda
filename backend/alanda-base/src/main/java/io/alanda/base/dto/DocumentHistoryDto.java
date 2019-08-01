/**
 * 
 */
package io.alanda.base.dto;

/**
 * @author jlo
 */
public class DocumentHistoryDto {

  private String guid;

  private String name;

  private String path;

  private String mediaType;

  private String lastModified;

  private Long size;

  private boolean folder = false;

  private String versionString;

  private String label;

  private String author;

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
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

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
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

  public boolean isFolder() {
    return folder;
  }

  public void setFolder(boolean folder) {
    this.folder = folder;
  }

  public String getVersionString() {
    return versionString;
  }

  public void setVersionString(String versionString) {
    this.versionString = versionString;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

}
