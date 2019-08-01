package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
@Entity
@Table(name = "PMC_DOCU_CONFIG")
@NamedQueries({@NamedQuery(name = "DocuConfig.getByMappingName", query = "Select c from DocuConfig c where c.mappingName = :mappingName")})
public class DocuConfig {

  @Id
  @Column(name = "ID")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "SOURCE_FOLDER_ID")
  private DocuFolder sourceFolder;

  @Column(name = "WRITE_ACCESS")
  private String writeAccess;

  @Column(name = "READ_ACCESS")
  private String readAccess;

  @Column(name = "TYPE")
  private String type;

  @Column(name = "SUBTYPE")
  private String subType;

  @Column(name = "MODULE_NAME")
  private String moduleName;

  @Column(name = "MODULE_FOLDER")
  private String moduleFolder;

  @Column(name = "MAPPING_NAME_")
  private String mappingName;

  @Column(name = "DISPLAY_NAME_")
  private String displayName;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DocuFolder getSourceFolder() {
    return sourceFolder;
  }

  public void setSourceFolder(DocuFolder sourceFolder) {
    this.sourceFolder = sourceFolder;
  }

  public String getWriteAccess() {
    return writeAccess;
  }

  public void setWriteAccess(String writeAccess) {
    this.writeAccess = writeAccess;
  }

  public String getReadAccess() {
    return readAccess;
  }

  public void setReadAccess(String readAccess) {
    this.readAccess = readAccess;
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

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getModuleFolder() {
    return moduleFolder;
  }

  public void setModuleFolder(String moduleFolder) {
    this.moduleFolder = moduleFolder;
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
