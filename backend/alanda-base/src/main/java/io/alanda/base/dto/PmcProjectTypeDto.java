package io.alanda.base.dto;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PmcProjectTypeDto {

  Long guid;

  String idName;

  String name;

  List<DocuConfigDto> docuConfigs;

  @JsonIgnore
  Collection<PmcProjectTypeDto> allowedChildTypes;

  String allowedTags;

  String allowedProcesses;

  String readRights;

  String writeRights;
  
  String createRights;

  String deleteRights;

  List<String> readRightGroups;

  List<String> writeRightGroups;

  List<String> createRightGroups;

  List<String> deleteRightGroups;

  List<String> allowedTagList;

  List<ProcessDefinitionDto> processDefinitions;

  String allowedSubtypes;

  List<String> allowedSubtypeList;

  String startProcess;

  Collection<MilestoneDto> allowedMilestones;

  String objectType;
  
  String additionalProperties;
  
  String configuration;
  
//  AuthorizationResult authorizationResult;
  
  Collection<PmcProjectPhaseDefinitionDto> phases;

  String detailsTemplate;

  String propertiesTemplate;

  String creationPropertiesTemplate;

  public PmcProjectTypeDto() {
    super();
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<DocuConfigDto> getDocuConfigs() {
    return docuConfigs;
  }

  public void setDocuConfigs(List<DocuConfigDto> docuConfigs) {
    this.docuConfigs = docuConfigs;
  }

  public String getAllowedTags() {
    return allowedTags;
  }

  public void setAllowedTags(String allowedTags) {
    this.allowedTags = allowedTags;
  }

  public Collection<PmcProjectTypeDto> getAllowedChildTypes() {
    return allowedChildTypes;
  }

  public void setAllowedChildTypes(Collection<PmcProjectTypeDto> allowedChildTypes) {
    this.allowedChildTypes = allowedChildTypes;
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

  public String getDeleteRights() {
    return deleteRights;
  }

  public void setDeleteRights(String deleteRights) {
    this.deleteRights = deleteRights;
  }

  public Collection<MilestoneDto> getAllowedMilestones() {
    return allowedMilestones;
  }

  public void setAllowedMilestones(Collection<MilestoneDto> allowedMilestones) {
    this.allowedMilestones = allowedMilestones;
  }

  public List<String> getReadRightGroups() {
    return readRightGroups;
  }

  public void setReadRightGroups(List<String> readRightGroups) {
    this.readRightGroups = readRightGroups;
  }

  public List<String> getWriteRightGroups() {
    return writeRightGroups;
  }

  public void setWriteRightGroups(List<String> writeRightGroups) {
    this.writeRightGroups = writeRightGroups;
  }

  public List<String> getDeleteRightGroups() {
    return deleteRightGroups;
  }

  public void setDeleteRightGroups(List<String> deleteRightGroups) {
    this.deleteRightGroups = deleteRightGroups;
  }

  public String getCreateRights() {
    return createRights;
  }

  public void setCreateRights(String createRights) {
    this.createRights = createRights;
  }

  public List<String> getCreateRightGroups() {
    return createRightGroups;
  }

  public void setCreateRightGroups(List<String> createRightGroups) {
    this.createRightGroups = createRightGroups;
  }
  
  public List<String> getAllowedTagList() {
    return allowedTagList;
  }

  public void setAllowedTagList(List<String> allowedTagList) {
    this.allowedTagList = allowedTagList;
  }

  public List<ProcessDefinitionDto> getProcessDefinitions() {
    return processDefinitions;
  }

  public void setProcessDefinitions(List<ProcessDefinitionDto> processDefinitions) {
    this.processDefinitions = processDefinitions;
  }

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public String getStartProcess() {
    return startProcess;
  }

  public void setStartProcess(String startProcess) {
    this.startProcess = startProcess;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public String getDetailsTemplate() {
    return detailsTemplate;
  }

  public void setDetailsTemplate(String detailsTemplate) {
    this.detailsTemplate = detailsTemplate;
  }

  public String getPropertiesTemplate() {
    return propertiesTemplate;
  }

  public void setPropertiesTemplate(String propertiesTemplate) {
    this.propertiesTemplate = propertiesTemplate;
  }

  public String getCreationPropertiesTemplate() {
    return creationPropertiesTemplate;
  }

  public void setCreationPropertiesTemplate(String creationPropertiesTemplate) {
    this.creationPropertiesTemplate = creationPropertiesTemplate;
  }

  @Override
  public String toString() {
    return "PmcProjectTypeDto [guid=" +
      guid +
      ", idName=" +
      idName +
      ", name=" +
      name +
      ", docuConfigs=" +
      docuConfigs +
      ", allowedTags=" +
      allowedTags +
        ", allowedSubtypes=" +
      allowedSubtypes +
      ", allowedProcesses=" +
      allowedProcesses +
      ", readRights=" +
      readRights +
      ", writeRights=" +
      writeRights +
      ", deleteRights=" +
      deleteRights +
      ", readRightGroups=" +
      readRightGroups +
      ", writeRightGroups=" +
      writeRightGroups +
      ", deleteRightGroups=" +
      deleteRightGroups +
      ", allowedTagList=" +
      allowedTagList +
      ", processDefinitions=" +
      processDefinitions +
      ", startProcess=" +
      startProcess +
      ", allowedMilestones=" +
      allowedMilestones +
      ", additionalProperties=" +
      additionalProperties +
      ", configuration=" +
      configuration +
      ", detailsTemplate=" +
      detailsTemplate +
      ", propertiesTemplate=" +
      propertiesTemplate +
      ", creationPropertiesTemplate=" +
      creationPropertiesTemplate +
      "]";
  }

  public String getAllowedSubtypes() {
    return allowedSubtypes;
  }

  public void setAllowedSubtypes(String allowedSubtypes) {
    this.allowedSubtypes = allowedSubtypes;
  }

  public List<String> getAllowedSubtypeList() {
    return allowedSubtypeList;
  }

  public void setAllowedSubtypeList(List<String> allowedSubtypeList) {
    this.allowedSubtypeList = allowedSubtypeList;
  }

  
  public String getAdditionalProperties() {
    return additionalProperties;
  }

  
  public void setAdditionalProperties(String additionalProperies) {
    this.additionalProperties = additionalProperies;
  }

  
  public String getConfiguration() {
    return configuration;
  }

  
  public void setConfiguration(String configuration) {
    this.configuration = configuration;
  }

//  
//  public AuthorizationResult getAuthorizationResult() {
//    return authorizationResult;
//  }
//
//  
//  public void setAuthorizationResult(AuthorizationResult authorizationResult) {
//    this.authorizationResult = authorizationResult;
//  }

  
  public Collection<PmcProjectPhaseDefinitionDto> getPhases() {
    return phases;
  }

  
  public void setPhases(Collection<PmcProjectPhaseDefinitionDto> phases) {
    this.phases = phases;
  }

  
}
