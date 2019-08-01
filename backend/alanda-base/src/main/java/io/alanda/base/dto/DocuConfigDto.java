package io.alanda.base.dto;

import java.util.Arrays;

public class DocuConfigDto {

  private Long id;

  private DocuFolderDto sourceFolder;

  private long[] writeAccess;

  private long[] readAccess;

  private String type;

  private String subType;

  private String moduleName;

  private String moduleFolder;

  private String mappingName;

  private String displayName;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DocuFolderDto getSourceFolder() {
    return sourceFolder;
  }

  public void setSourceFolder(DocuFolderDto sourceFolder) {
    this.sourceFolder = sourceFolder;
  }

  public long[] getWriteAccess() {
    return writeAccess;
  }

  public void setWriteAccess(long[] writeAccess) {
    this.writeAccess = writeAccess;
  }

  public String getWriteAccessAsString() {
    String permString = "";
    if ((writeAccess == null) || (writeAccess.length == 0)) {
      permString = "13";
    } else {
      for (int i = 0; i < this.writeAccess.length; i++ ) {
        permString += Long.toString(writeAccess[i]);
        if (i < this.writeAccess.length - 1)
          permString += "|";
      }
    }
    return permString;
  }

  public long[] getReadAccess() {
    return readAccess;
  }

  public void setReadAccess(long[] readAccess) {
    this.readAccess = readAccess;
  }

  public String getReadAccessAsString() {
    String permString = "";
    if ((writeAccess == null) || (writeAccess.length == 0)) {
      permString = "13";
    } else {
      for (int i = 0; i < this.readAccess.length; i++ ) {
        permString += Long.toString(readAccess[i]);
        if (i < this.readAccess.length - 1)
          permString += "|";
      }
    }
    return permString;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSubType() {
    return subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
  }

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String module) {
    this.moduleName = module;
  }

  public String getModuleFolder() {
    return moduleFolder;
  }

  public void setModuleFolder(String moduleFolder) {
    this.moduleFolder = moduleFolder;
  }

  @Override
  public String toString() {
    return "DocuConfigDto [id=" +
      id +
      ", sourceFolder=" +
      sourceFolder +
      ", writeAccess=" +
      Arrays.toString(writeAccess) +
      ", readAccess=" +
      Arrays.toString(readAccess) +
      ", type=" +
      type +
      ", subType=" +
      subType +
      ", moduleName=" +
      moduleName +
      ", moduleFolder=" +
      moduleFolder +
      "]";
  }

  public String getMappingName() {
    return mappingName;
  }

  public void setMappingName(String mappingName) {
    this.mappingName = mappingName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

}
