package io.alanda.base.dto;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DMS offensive adapted DTO
 *
 * @author jlo
 */

public class DmsDocumentDto {

  private String guid;

  private String name;

  private String authorName;

  private String mediaType;

  private String lastModified;

  private Long size;

  private String versionString;

  private List<DMSTagDto> tags;

  @JsonIgnore
  private InputStream intputStream;

  protected static Log logger = LogFactory.getLog(DmsDocumentDto.class);

  public DmsDocumentDto() {
  }

  public DmsDocumentDto(String name, String mediaType, Long size) {
    this.name = name;
    this.mediaType = mediaType;
    this.size = size; // in bytes
  }

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

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
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

  public String getVersionString() {
    return versionString;
  }

  public void setVersionString(String versionString) {
    this.versionString = versionString;
  }

  public List<DMSTagDto> getTags() {
    return tags;
  }

  public void setTags(List<DMSTagDto> tags) {
    this.tags = tags;
  }

  public InputStream getIntputStream() {
    return intputStream;
  }

  public void setIntputStream(InputStream intputStream) {
    this.intputStream = intputStream;
  }

  public boolean hasTag(DMSTagDto tag) {
    return this.getTags().stream()
        .anyMatch(x -> x.getIdentifier().equals(tag.getIdentifier()));
  }

  public boolean hasTag(String tagId) {
    return this.getTags().stream()
        .anyMatch(x -> x.getIdentifier().equals(tagId));
  }
  /*
   * will return documents which match one of the tags
   */
  public boolean anyMatch(List<String> filters) {
    return this.getTags().stream()
        .anyMatch(tag -> {
          for (String tagId: filters) {
            if (tag.getIdentifier().equals(tagId)) {
              return true;
            }
          }
          return false;
        });
  }

  public boolean hasInName(String fileMask) {
    return (this.getName().toLowerCase().indexOf(fileMask.toLowerCase()) > -1);
  }

  @Override
  public String toString() {
    return "DocumentDto [guid=" +
      guid +
      ", name=" +
      name +
      ", authorName=" +
      authorName +
      ", mediaType=" +
      mediaType +
      ", lastModified=" +
      lastModified +
      ", size=" +
      size +
      ", versionString=" +
      versionString +
      ", tags=" +
      tags +
      ", intputStream=" +
      intputStream +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((guid == null) ? 0 : guid.hashCode());
    result = prime * result + ((versionString == null) ? 0 : versionString.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DmsDocumentDto other = (DmsDocumentDto) obj;
    if (guid == null) {
      if (other.guid != null)
        return false;
    } else if ( !guid.equals(other.guid))
      return false;
    if (versionString == null) {
      if (other.versionString != null)
        return false;
    } else if ( !versionString.equals(other.versionString))
      return false;
    return true;
  }

}
