package io.alanda.base.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.alanda.base.type.PmcProjectState;
import io.alanda.base.type.ProcessRelation;

// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "guid")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PmcProjectDto extends PmcProjectCompactDto {

//  Long version;

  PmcProjectTypeDto pmcProjectType;

  String subtype;

  Collection<PmcProjectDto> parents;

  Collection<PmcProjectDto> children;

  Collection<PmcProjectProcessDto> processes;

  Long customerProjectId;

  Long ownerId;

  String details;

  String guStatus;

  String comment;

  Integer risk;

  Long refObjectId;

  List<SimpleMilestoneDto> milestones;

  List<SimplePhaseDto> phases;

  List<PmcHistoryLogDto> history;

  String displayMetaInfo;

  public List<PmcHistoryLogDto> getHistory() {
    return history;
  }

  public void setHistory(List<PmcHistoryLogDto> history) {
    this.history = history;
  }

  public List<SimpleMilestoneDto> getMilestones() {
    return milestones;
  }

  public void setMilestones(List<SimpleMilestoneDto> milestones) {
    this.milestones = milestones;
  }

  Collection<PmcPropertyDto> properties;

  Map<String, Object> propertiesMap;

  Map<String, Object> milestonesMap;

  //  AuthorizationResult authorizationResult;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date createDate;

  Long createUser;

  String ownerName;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Vienna")
  Date updateDate;

  Long updateUser;

  Collection<Long> parentIds;

  Collection<Long> childrenIds;

  Map<String, Object> additionalInfo;

  String resultStatus;

  String resultComment;

  @JsonIgnore
  private Long createUserParameter;

  @JsonIgnore
  private String processPackageParameter;

  @JsonIgnore
  private String commentKey;

  @JsonIgnore
  String[] activePhases;

  String authBase;

  public PmcProjectDto() {
    super();
  }

  /**
   * Constructor to create pseudo object containing only guid
   * 
   * @param guid
   */
  public PmcProjectDto(Long guid) {
    super();
    this.guid = guid;
  }

  public boolean hasTag(String search) {
    if (tag == null)
      return false;
    for (String t : tag) {
      if (t.equals(search))
        return true;
    }
    return false;
  }

  public PmcProjectProcessDto mainProcess() {
    if (this.processes == null)
      return null;
    for (PmcProjectProcessDto process : processes) {
      if (process.getRelation().equals(ProcessRelation.MAIN.toString()))
        return process;
    }
    return null;
  }

  /**
   * Returns <code>true</code> if PmcProjectState is active, prepared or new
   *
   * @return <code>true</code> if PmcProjectState is active, prepared or new otherwise <code>false</code>
   */
  public boolean isRunning() {
    return PmcProjectState.ACTIVE.toString().equals(this.status) ||
      PmcProjectState.PREPARED.toString().equals(this.status) ||
      PmcProjectState.NEW.toString().equals(this.status);
  }

  public String getAuthBase() {
    return authBase;
  }

  public void setAuthBase(String authBase) {
    this.authBase = authBase;
  }

  public PmcProjectTypeDto getPmcProjectType() {
    return pmcProjectType;
  }

  public void setPmcProjectType(PmcProjectTypeDto pmcProjectType) {
    this.pmcProjectType = pmcProjectType;
  }

  public Long getCustomerProjectId() {
    return customerProjectId;
  }

  public void setCustomerProjectId(Long customerProjectId) {
    this.customerProjectId = customerProjectId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public String getGuStatus() {
    return guStatus;
  }

  public void setGuStatus(String guStatus) {
    this.guStatus = guStatus;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Integer getRisk() {
    return risk;
  }

  public void setRisk(Integer risk) {
    this.risk = risk;
  }

  public Long getRefObjectId() {
    return refObjectId;
  }

  public void setRefObjectId(Long refObjectId) {
    this.refObjectId = refObjectId;
  }

  public Collection<PmcProjectDto> getChildren() {
    return children;
  }

  public void setChildren(Collection<PmcProjectDto> children) {
    this.children = children;
  }

  public Collection<PmcProjectProcessDto> getProcesses() {
    return processes;
  }

  public void setProcesses(Collection<PmcProjectProcessDto> processes) {
    this.processes = processes;
  }

  public String getSubtype() {
    return subtype;
  }

  public void setSubtype(String subtype) {
    this.subtype = subtype;
  }

  public Collection<PmcPropertyDto> getProperties() {
    return properties;
  }

  public void setProperties(Collection<PmcPropertyDto> properties) {
    this.properties = properties;
  }

  public void addProperty(PmcPropertyDto prop) {
    if (properties == null) {
      properties = new ArrayList<>();
    }
    properties.add(prop);
  }

  public Collection<PmcProjectDto> getParents() {
    return parents;
  }

  public void setParents(Collection<PmcProjectDto> parents) {
    this.parents = parents;
  }

  public void addParent(PmcProjectDto p) {
    if (parents == null) {
      parents = new ArrayList<>();
    }
    parents.add(p);
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Long getCreateUser() {
    return createUser;
  }

  /**
   * set a project creator
   * only works ATFER the project has been created
   */
  public void setCreateUser(Long createUser) {
    this.createUser = createUser;
  }

  public String getOwnerName() {
    return ownerName;
  }

  /**
   * set a project owner
   * only works ATFER the project has been created
   * @param ownerName
   */
  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public Long getUpdateUser() {
    return updateUser;
  }

  public void setUpdateUser(Long updateUser) {
    this.updateUser = updateUser;
  }

  public String toShortString() {
    return projectId + " (guid=" + guid + ")";
  }

  @Override
  public String toString() {
    return "PmcProjectDto [guid=" +
      guid +
      ", version=" +
      version +
      ", projectId=" +
      projectId +
      ", pmcProjectType=" +
      pmcProjectType +
      ", subtype=" +
      subtype +
      ", processes=" +
      processes +
      ", customerProjectId=" +
      customerProjectId +
      ", tag=" +
      Arrays.toString(tag) +
      ", ownerId=" +
      ownerId +
      ", title=" +
      title +
      ", details=" +
      details +
      ", comment=" +
      comment +
      ", risk=" +
      risk +
      ", status=" +
      status +
      ", dueDate=" +
      dueDate +
      ", priority=" +
      priority +
      ", refObjectType=" +
      refObjectType +
      ", refObjectId=" +
      refObjectId +
      ", refObjectIdName=" +
      refObjectIdName +
      ", milestones=" +
      milestones +
      ", properties=" +
      properties +
      "]";
  }

  public Long getCreateUserParameter() {
    return createUserParameter;
  }

  /**
   * set a project owner WHEN creating project
   * @param createUserParameter
   */
  public void setCreateUserParameter(Long createUserParameter) {
    this.createUserParameter = createUserParameter;
  }

  public Map<String, Object> getPropertiesMap() {
    return propertiesMap;
  }

  public void setPropertiesMap(Map<String, Object> propertiesMap) {
    this.propertiesMap = propertiesMap;
  }

  public Map<String, Object> getMilestonesMap() {
    return milestonesMap;
  }

  public void setMilestonesMap(Map<String, Object> milestonesMap) {
    this.milestonesMap = milestonesMap;
  }

  public String getProcessPackageParameter() {
    return processPackageParameter;
  }

  public void setProcessPackageParameter(String processPackageParameter) {
    this.processPackageParameter = processPackageParameter;
  }

  public Collection<Long> getParentIds() {
    return parentIds;
  }

  public void setParentIds(Collection<Long> parentIds) {
    this.parentIds = parentIds;
  }

  public Collection<Long> getChildrenIds() {
    return childrenIds;
  }

  public void setChildrenIds(Collection<Long> childrenIds) {
    this.childrenIds = childrenIds;
  }

  public String getCommentKey() {
    return commentKey;
  }

  public void setCommentKey(String commentKey) {
    this.commentKey = commentKey;
  }

  public Map<String, Object> getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(Map<String, Object> additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public String getResultStatus() {
    return resultStatus;
  }

  public void setResultStatus(String resultStatus) {
    this.resultStatus = resultStatus;
  }

  public String getResultComment() {
    return resultComment;
  }

  public void setResultComment(String resultComment) {
    this.resultComment = resultComment;
  }

  public String[] getActivePhases() {
    return activePhases;
  }

  public void setActivePhases(String[] activePhases) {
    this.activePhases = activePhases;
  }

  public List<SimplePhaseDto> getPhases() {
    return phases;
  }

  public void setPhases(List<SimplePhaseDto> phases) {
    this.phases = phases;
  }

//  public Long getVersion() {
//    return version;
//  }
//
//  public void setVersion(Long version) {
//    this.version = version;
//  }

  public String getHumanReadableId() {
    return this.projectId + " (guid=" + this.getGuid() + ")";
  }

  public String getDisplayMetaInfo() {
    return displayMetaInfo;
  }

  public void setDisplayMetaInfo(String displayMetaInfo) {
    this.displayMetaInfo = displayMetaInfo;
  }
}
