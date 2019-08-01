package io.alanda.base.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "Document.getByPathAndFileName", query = "SELECT s FROM Document s WHERE s.path = :path AND s.fileName = :fileName"),
  @NamedQuery(name = "Document.getByPath", query = "SELECT s FROM Document s WHERE s.path = :path")
})
@Table(name = "PMC_DOCUMENT")
public class Document extends AbstractAuditEntity {

  @Column(name = "DIRECTORY")
  private String path;
  
  @Column(name = "BASENAME")
  private String name;
  
  @Column(name = "FILENAME")
  private String fileName;
  
  @Column(name = "FILETYPE")
  private String mediaType;

  @Column(name = "FILESIZE")
  private Long fileSize;

  @Column(name = "PROJECTID")
  private Long projectId;
  
  
  
  public Document() {
    super();
  }

  
  public String getPath() {
    return path;
  }


  
  public void setPath(String path) {
    this.path = path;
  }


  
  public String getName() {
    return name;
  }


  
  public void setName(String name) {
    this.name = name;
  }


  
  public String getFileName() {
    return fileName;
  }


  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  
  public String getMediaType() {
    return mediaType;
  }


  
  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  
  public Long getFileSize() {
    return fileSize;
  }


  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }


  
  public Long getProjectId() {
    return projectId;
  }


  
  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Date lastMod() {
    return this.getUpdateDate() != null ? this.getUpdateDate() : this.getCreateDate();
  }
  
}
