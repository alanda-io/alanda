/**
 * 
 */
package io.alanda.base.dto;

import java.util.List;

/**
 * @author jlo
 */
public class DirectoryInfoDto {

  private DocuConfigDto config;

  private List<DocuFolderDto> hierarchy;

  private String folderPath;

  private String subFolder;

  private Long projectId;

  private Long refObjectId;

  private String refObjectType;

  public DirectoryInfoDto(
      DocuConfigDto config,
      List<DocuFolderDto> hierarchy,
      String folderPath,
      String subFolder,
      Long projectId,
      Long refObjectId,
      String refObjectType) {
    super();
    this.config = config;
    this.hierarchy = hierarchy;
    this.folderPath = folderPath;
    this.subFolder = subFolder;
    this.projectId = projectId;
    this.refObjectId = refObjectId;
    this.refObjectType = refObjectType;

  }

  //  public String getDatabaseFolderPath() {
  //    String sFolderPath = this.getFolderPath();
  //    if ( !sFolderPath.startsWith("../"))
  //      sFolderPath = "../" + sFolderPath;
  //    return sFolderPath;
  //  }

  public boolean hasId(Long folderId) {
    if (config.getSourceFolder().getId().equals(folderId))
      return true;
    return hasId(config.getSourceFolder(), folderId);
  }
  
  public boolean hasName(String folderName) {
    if (config.getSourceFolder().getName().equals(folderName))
      return true;
    return hasName(config.getSourceFolder(), folderName);
  }

  public boolean hasId(DocuFolderDto dfd, Long folderId) {
    for (DocuFolderDto child : dfd.getSubFolders()) {
      if (child.getId().equals(folderId)) {
        return true;
      }
    }
    for (DocuFolderDto child : dfd.getSubFolders()) {
      if (hasId(child, folderId)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean hasName(DocuFolderDto dfd, String folderName) {
    for (DocuFolderDto child : dfd.getSubFolders()) {
      if (child.getName().equals(folderName)) {
        return true;
      }
    }
    for (DocuFolderDto child : dfd.getSubFolders()) {
      if (hasName(child, folderName)) {
        return true;
      }
    }
    return false;
  }

  public DocuConfigDto getConfig() {
    return config;
  }

  public void setConfig(DocuConfigDto config) {
    this.config = config;
  }

  public String getFolderPath() {
    return folderPath;
  }

  public void setFolderPath(String folderPath) {
    this.folderPath = folderPath;
  }

  public String getSubFolder() {
    return subFolder;
  }

  public void setSubFolder(String subFolder) {
    this.subFolder = subFolder;
  }

  public List<DocuFolderDto> getHierarchy() {
    return hierarchy;
  }

  public void setHierarchy(List<DocuFolderDto> hierarchy) {
    this.hierarchy = hierarchy;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Long getRefObjectId() {
    return refObjectId;
  }

  public void setRefObjectId(Long refObjectId) {
    this.refObjectId = refObjectId;
  }

  public String getRefObjectType() {
    return refObjectType;
  }

  public void setRefObjectType(String refObjectType) {
    this.refObjectType = refObjectType;
  }

  @Override
  public String toString() {
    return "DirectoryInfoDto [config=" +
      config +
      ", hierarchy=" +
      hierarchy +
      ", folderPath=" +
      folderPath +
      ", subFolder=" +
      subFolder +
      ", projectId=" +
      projectId +
      ", refObjectId=" +
      refObjectId +
      ", refObjectType=" +
      refObjectType +
      "]";
  }

}
