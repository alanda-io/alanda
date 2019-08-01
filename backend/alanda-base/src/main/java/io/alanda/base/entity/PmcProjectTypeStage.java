package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_PROJECTTYPE_STAGE")
public class PmcProjectTypeStage extends AbstractEntity {
  
  @Column(name = "IDNAME")  
  String idName;
    
  @Column(name = "DISPLAYNAME")  
  String displayName;
    
  @Column(name = "STARTPROCESS")
  String startProcess;

  @Column(name = "ALLOWEDPROCESSES")
  String allowedProcesses;
  
  @Column(name = "READRIGHTS")
  String readRights;
  
  @Column(name = "WRITERIGHTS")
  String writeRights;
  
  @Column(name = "CREATERIGHTS")
  String createRights;
  
  @Column(name = "DELETERIGHTS")
  String deleteRights;
  
  @Column(name = "PMC_PROJECTTYPE")
  PmcProjectType pmcProjectType;

  
  public String getIdName() {
    return idName;
  }

  
  public void setIdName(String idName) {
    this.idName = idName;
  }

  
  public String getDisplayName() {
    return displayName;
  }

  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  
  public String getStartProcess() {
    return startProcess;
  }

  
  public void setStartProcess(String startProcess) {
    this.startProcess = startProcess;
  }

  
  public String getAllowedProcesses() {
    return allowedProcesses;
  }

  
  public void setAllowedProcesses(String allowedProcesses) {
    this.allowedProcesses = allowedProcesses;
  }

  
  public String getReadRights() {
    return readRights;
  }

  
  public void setReadRights(String readRights) {
    this.readRights = readRights;
  }

  
  public String getWriteRights() {
    return writeRights;
  }

  
  public void setWriteRights(String writeRights) {
    this.writeRights = writeRights;
  }

  
  public String getCreateRights() {
    return createRights;
  }

  
  public void setCreateRights(String createRights) {
    this.createRights = createRights;
  }

  
  public String getDeleteRights() {
    return deleteRights;
  }

  
  public void setDeleteRights(String deleteRights) {
    this.deleteRights = deleteRights;
  }


  @Override
  public String toString() {
    return "PmcProjectTypeStage [idName=" +
      idName +
      ", displayName=" +
      displayName +
      ", startProcess=" +
      startProcess +
      ", allowedProcesses=" +
      allowedProcesses +
      ", readRights=" +
      readRights +
      ", writeRights=" +
      writeRights +
      ", createRights=" +
      createRights +
      ", deleteRights=" +
      deleteRights +
      ", pmcProjectType=" +
      pmcProjectType.getIdName() +
      "]";
  }

  public PmcProjectType getPmcProjectType() {
    return pmcProjectType;
  }


  
  public void setPmcProjectType(PmcProjectType pmcProjectType) {
    this.pmcProjectType = pmcProjectType;
  }
  
}
