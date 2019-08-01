package io.alanda.base.dto;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DocuFolderDto {

  private Long id;

  @JsonProperty("label")
  private String name;

  private Integer files;

  @JsonProperty("children")
  private Collection<DocuFolderDto> subFolders;

  @JsonIgnore
  private DocuFolderDto parent;

  @JsonIgnore
  private String path;

  private boolean virtual;

  private String permissions = "r";

  private String mapping;

  public static DocuFolderDto virtual(Long id, String name, List<DocuFolderDto> subFolders) {
    DocuFolderDto dto = new DocuFolderDto();
    dto.setId(id);
    dto.setName(name);
    dto.setVirtual(true);
    dto.subFolders = subFolders;
    return dto;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @JsonProperty("label")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("children")
  public Collection<DocuFolderDto> getSubFolders() {
    return subFolders;
  }

  public void setSubFolders(Collection<DocuFolderDto> subFolders) {
    this.subFolders = subFolders;
  }

  public DocuFolderDto getParent() {
    return parent;
  }

  public void setParent(DocuFolderDto parent) {
    this.parent = parent;
  }

  public Integer getFiles() {
    return files;
  }

  public void setFiles(Integer files) {
    this.files = files;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public String toString() {
    return "DocuFolderDto [id=" + id + ", name=" + name + ", files=" + files + ", path=" + path + ", subFolders=" + subFolders + "]";
  }

  public boolean isVirtual() {
    return virtual;
  }

  public void setVirtual(boolean virtual) {
    this.virtual = virtual;
  }

  public String getPermissions() {
    return permissions;
  }

  public void setPermissions(String permissions) {
    this.permissions = permissions;
  }

  public String getMapping() {
    return mapping;
  }

  public void setMapping(String mapping) {
    this.mapping = mapping;
  }
}
