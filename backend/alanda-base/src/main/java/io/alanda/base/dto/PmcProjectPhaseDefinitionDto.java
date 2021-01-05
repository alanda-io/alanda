package io.alanda.base.dto;

import java.util.Collection;

public class PmcProjectPhaseDefinitionDto {
 
  Long guid;
  
  String idName;

  String displayName;

  Collection<String> allowedProcesses;
  
  Collection<String> prepareRights;
  
  Collection<String> writeRights;
  
  String phaseProcessKey;

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

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

  
  public Collection<String> getAllowedProcesses() {
    return allowedProcesses;
  }

  
  public void setAllowedProcesses(Collection<String> allowedProcesses) {
    this.allowedProcesses = allowedProcesses;
  }

  
  public Collection<String> getPrepareRights() {
    return prepareRights;
  }

  
  public void setPrepareRights(Collection<String> prepareRights) {
    this.prepareRights = prepareRights;
  }

  
  public Collection<String> getWriteRights() {
    return writeRights;
  }

  
  public void setWriteRights(Collection<String> writeRights) {
    this.writeRights = writeRights;
  }

  public String getPhaseProcessKey() {
      return phaseProcessKey;
  }

  public void setPhaseProcessKey(String phaseProcessKey) {
      this.phaseProcessKey = phaseProcessKey;
  }
  
}
