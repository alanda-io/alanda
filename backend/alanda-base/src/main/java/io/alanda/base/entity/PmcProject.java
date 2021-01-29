package io.alanda.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.type.PmcProjectResultStatus;
import io.alanda.base.type.PmcProjectState;
import io.alanda.base.type.ProcessRelation;

@Entity
@Table(name = "PMC_PROJECT")
public class PmcProject extends AbstractAuditEntity implements Serializable {

  private static final long serialVersionUID = -912125347206603549L;

  private static final Logger log = LoggerFactory.getLogger(PmcProject.class);

  @Column(name = "PROJECTID", unique = true)
  String projectId;

  @ManyToOne
  @JoinColumn(name = "REF_PMCPROJECTTYPE")
  PmcProjectType pmcProjectType;

  @Column(name = "SUBTYPE")
  String subtype;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "children")
  //@JoinTable(name = "PMC_PROJECT_DEPENDSON", joinColumns = @JoinColumn(name = "CHILD", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "PARENT", referencedColumnName = "GUID"))
  List<PmcProject> parents;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "PMC_PROJECT_DEPENDSON", joinColumns = @JoinColumn(name = "PARENT", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "CHILD", referencedColumnName = "GUID"))
  List<PmcProject> children;

  @OneToMany(mappedBy = "pmcProject")
  List<PmcProjectProcess> processes = new ArrayList<>();

  @Column(name = "CUSTOMERPROJECT")
  Long customerProjectId;

  @Column(name = "TAG")
  String tag;

  @Column(name = "OWNER")
  Long ownerId;

  @Column(name = "TITLE")
  String title;

  @Column(name = "DETAILS")
  String details;

  @Column(name = "GUSTATUS")
  String guStatus;

  @Column(name = "PROJECTCOMMENT")
  String comment;

  @Column(name = "RISK")
  Integer risk;

  @Column(name = "STATUS")
  @Enumerated(EnumType.STRING)
  PmcProjectState status;

  @Column(name = "DUEDATE")
  Date dueDate;

  @Column(name = "PRIORITY")
  Integer priority;

  @Column(name = "REF_OBJECTTYPE")
  String refObjectType;

  @Column(name = "REF_OBJECTID")
  Long refObjectId;

  @Column(name = "REF_OBJECTIDNAME")
  String refObjectIdName;

  @OneToMany(mappedBy = "pmcProject")
  Collection<PmcProjectPhase> phases;

  @Column(name = "RESULT_STATUS")
  @Enumerated(EnumType.STRING)
  PmcProjectResultStatus resultStatus;

  @Column(name = "RESULT_COMMENT")
  String resultComment;

  @Column(name = "HIGHLIGHT")
  boolean isHighlighted;

  public boolean isHighlighted() {
    return isHighlighted;
  }

  public void setHighlighted(boolean highlighted) {
    isHighlighted = highlighted;
  }

  public PmcProject() {
    super();
  }

  public PmcProject firstParent() {
    if (parents != null && !parents.isEmpty())
      return parents.iterator().next();
    return null;
  }

  public Long firstParentGuid() {
    PmcProject p = firstParent();
    return p != null ? p.getGuid() : null;
  }

  public List<PmcProject> getParentsRecursive() {
    return getParentsRecursive(this, null);
  }

  private List<PmcProject> getParentsRecursive(PmcProject p, Set<Long> alreadyPassed) {
    if (alreadyPassed == null) {
      alreadyPassed = new HashSet<>();
    }
    if (alreadyPassed.contains(p.getGuid()) || p.getParents() == null)
      return Collections.emptyList();
    else
      alreadyPassed.add(p.getGuid());

    List<PmcProject> parents = new ArrayList<>();
    for (PmcProject parent : p.getParents()) {
      parents.add(parent);
      parents.addAll(getParentsRecursive(parent, alreadyPassed));
    }
    return parents;
  }

  public List<PmcProject> getChildrenRecursive() {
    return getChildrenRecursive(this, null);
  }

  private List<PmcProject> getChildrenRecursive(PmcProject p, Set<Long> alreadyPassed) {
    if (alreadyPassed == null) {
      alreadyPassed = new HashSet<>();
    }
    if (alreadyPassed.contains(p.getGuid()) || p.getChildren() == null)
      return Collections.emptyList();
    else
      alreadyPassed.add(p.getGuid());

    List<PmcProject> children = new ArrayList<>();
    for (PmcProject child : p.getChildren()) {
      children.add(child);
      children.addAll(getChildrenRecursive(child, alreadyPassed));
    }
    return children;
  }

  //  @PrePersist
  //  void onCreate() throws Exception{
  //    if (Strings.isNullOrEmpty(this.projectId)) throw new Exception("projectId can not be null!");
  //  }

  /**
   * Get the main process by iterating through all processes and look for ProcessRelation.MAIN
   * 
   * @see ProcessRelation
   * @see #processes
   * @return process with ProcessRelation.MAIN or return null if {@link #processes} is null
   */
  public PmcProjectProcess mainProcess() {
    if (this.processes == null)
      return null;
    for (PmcProjectProcess process : processes) {
      if (process.getRelation() == ProcessRelation.MAIN)
        return process;
    }
    return null;
  }

  /**
   * Add new process to the project
   * 
   * @see PmcProjectProcess
   * @param process process added to {@link #processes}
   */
  public void addProcess(PmcProjectProcess process) {
    if (this.processes == null) {
      this.processes = new ArrayList<PmcProjectProcess>();

    }
    this.processes.add(process);
  }

  /**
   * Gets the id of the project
   * 
   * @return {@link #projectId}
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * Sets the id of the project
   * 
   * @param projectId id of the project
   */

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  /**
   * Gets the type of the project
   * 
   * @see PmcProjectType
   * @return {@link pmcProjectType}
   */
  public PmcProjectType getPmcProjectType() {
    return pmcProjectType;
  }

  /**
   * Sets the type of the project
   * 
   * @see PmcProjectType
   * @param pmcProjectType type of the project
   */
  public void setPmcProjectType(PmcProjectType pmcProjectType) {
    this.pmcProjectType = pmcProjectType;
  }

  /**
   * Gets the customer project id
   * 
   * @return {@link #customerProjectId}
   */
  public Long getCustomerProjectId() {
    return customerProjectId;
  }

  /**
   * Sets the customer project id
   * 
   * @param customerProjectId id of the customerProject
   */
  public void setCustomerProjectId(Long customerProjectId) {
    this.customerProjectId = customerProjectId;
  }

  /**
   * Gets the tag of the project
   * 
   * @return {@link #tag} if tag is not null else return <code>null</code>
   */
  public String[] getTag() {
    if (tag != null)
      return tag.split(",");
    else
      return null;
  }

  /**
   * Set the tag of the project
   * 
   * @param tag name of the tag
   */
  public void setTag(String[] tag) {
    this.tag = "";
    if (tag != null) {
      for (int i = 0; i < tag.length; i++ )
        this.tag += tag[i] + ",";
      if (tag.length > 0)
        this.tag = this.tag.substring(0, this.tag.length() - 1);
    }
  }

  /**
   * Compares the tag passed as a parameter with all tags within {@link #tag}
   * 
   * @param tagName name of the tag
   * @return <code>true</code> if {@link #tag} contains the tag passed as a parameter otherwise returns
   *         <code>false</code>.
   */
  public boolean hasTag(String tagName) {
    String[] tags = getTag();
    if (tags == null)
      return false;
    for (String tag : tags) {
      if (tag.equals(tagName))
        return true;
    }
    return false;
  }

  /**
   * Gets the name of the projectType
   * 
   * @see PmcProjectType
   * @return the name of the {@link #pmcProjectType} if set else returns <code>null</code>
   */
  public String getProjectTypeName() {
    if (pmcProjectType != null)
      return pmcProjectType.getName();
    else
      return null;
  }

  /**
   * Gets the id name of the projectType
   * 
   * @see PmcProjectType
   * @return the idName of the {@link #pmcProjectType} if set else returns <code>null</code>
   */
  public String getProjectTypeIdName() {
    if (pmcProjectType != null)
      return pmcProjectType.getIdName();
    else
      return null;
  }

  /**
   * Gets the id of the owner
   * 
   * @return {@link #ownerId}
   */
  public Long getOwnerId() {
    return ownerId;
  }

  /**
   * Sets the id of the owner
   * 
   * @param ownerId id of the owner
   */
  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * Gets the title of the project
   * 
   * @return {@link #title}
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of the project
   * 
   * @param title name of the project title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the details of the project
   * 
   * @return {@link #details}
   */
  public String getDetails() {
    return details;
  }

  /**
   * Sets the details for the project
   * 
   * @param details details of the project
   */
  public void setDetails(String details) {
    this.details = details;
  }

  /**
   * Gets the gu status
   * 
   * @return {@link #guStatus}
   */
  public String getGuStatus() {
    return guStatus;
  }

  /**
   * Sets the gu status for the project
   * 
   * @param guStatus gu status for the project
   */
  public void setGuStatus(String guStatus) {
    this.guStatus = guStatus;
  }

  /**
   * Gets the comment for the project
   * 
   * @return {@link #comment}
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the comment for the project
   * 
   * @param comment comment of the project
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Get the risk value of the project
   * 
   * @return {@link #risk}
   */
  public Integer getRisk() {
    return risk;
  }

  /**
   * Sets the risk value for the project
   * 
   * @param risk risk value of the project
   */
  public void setRisk(Integer risk) {
    this.risk = risk;
  }

  /**
   * Gets the status of the project
   * 
   * @see PmcProjectState
   * @return {@link #status}
   */
  public PmcProjectState getStatus() {
    return status;
  }

  /**
   * Sets the status for the project
   * 
   * @param status status of the project
   */
  public void setStatus(PmcProjectState status) {
    this.status = status;
  }

  /**
   * Gets the due date of the project
   * 
   * @return {@link #dueDate}
   */
  public Date getDueDate() {
    return dueDate;
  }

  /**
   * Sets the due date of the project
   * 
   * @param dueDate due date for the project
   */
  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  /**
   * Gets the priority of the project
   * 
   * @return {@link #priority}
   */
  public Integer getPriority() {
    return priority;
  }

  /**
   * Sets the priority of the project
   * 
   * @param priority priority for the project
   */
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  /**
   * Gets the RefObjectType
   * 
   * @return {@link #refObjectType}
   */
  public String getRefObjectType() {
    return refObjectType;
  }

  /**
   * Sets the RefObjectType for the project
   * 
   * @param refObjectType refObjectType for the project
   */
  public void setRefObjectType(String refObjectType) {
    this.refObjectType = refObjectType;
  }

  /**
   * Gets the id of the refObject
   * 
   * @return {@link #refObjectId}
   */
  public Long getRefObjectId() {
    return refObjectId;
  }

  /**
   * Sets the id of the refObject
   * 
   * @param refObjectId id for the refObject
   */
  public void setRefObjectId(Long refObjectId) {
    this.refObjectId = refObjectId;
  }

  /**
   * Gets all children projects within the pmcProject
   * 
   * @return {@link #children}
   */
  public Collection<PmcProject> getChildren() {
    return children;
  }

  /**
   * Sets the children projects for the pmcProject
   * 
   * @param children children projects for the pmcProject
   */
  public void setChildren(List<PmcProject> children) {
    this.children = children;
  }

  /**
   * Gets the processes of the pmcProject
   * 
   * @return {@link #processes}
   */
  public Collection<PmcProjectProcess> getProcesses() {
    return processes;
  }

  public void setProcesses(List<PmcProjectProcess> processes) {
    this.processes = processes;
  }

  public String getRefObjectIdName() {
    return refObjectIdName;
  }

  public void setRefObjectIdName(String refObjectIdName) {
    this.refObjectIdName = refObjectIdName;
  }

  public String getSubtype() {
    return subtype;
  }

  public void setSubtype(String subtype) {
    this.subtype = subtype;
  }

  public List<PmcProject> getParents() {
    return parents;
  }

  public void setParents(List<PmcProject> parents) {
    this.parents = parents;
  }

  public Collection<PmcProjectPhase> getPhases() {
    return phases;
  }

  public void setPhases(Collection<PmcProjectPhase> phases) {
    this.phases = phases;
  }

  public PmcProjectResultStatus getResultStatus() {
    return resultStatus;
  }

  public void setResultStatus(PmcProjectResultStatus resultStatus) {
    this.resultStatus = resultStatus;
  }

  public String getResultComment() {
    return resultComment;
  }

  public void setResultComment(String resultComment) {
    this.resultComment = resultComment;
  }

  @Override
  public String toString() {
    return "PmcProject [projectId=" +
      projectId +
      ", pmcProjectType=" +
      pmcProjectType.getIdName() +
      ", subtype=" +
      subtype +
      ", processes=" +
      processes +
      ", customerProjectId=" +
      customerProjectId +
      ", tag=" +
      tag +
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

      "]";
  }

  /**
   * Returns <code>true</code> if PmcProjectState is active, prepared or new
   * 
   * @return <code>true</code> if PmcProjectState is active, prepared or new otherwise <code>false</code>
   */
  public boolean isRunning() {
    return this.status == PmcProjectState.ACTIVE || this.status == PmcProjectState.PREPARED || this.status == PmcProjectState.NEW;
  }

  public String getHumanReadableId() {
    return this.projectId + " (guid=" + this.getGuid() + ")";
  }

  /**
   * Add new parent project.
   * 
   * @param parent parent pmcProject
   */
  public void addParent(PmcProject parent) {
    if (this.parents == null)
      this.parents = new ArrayList<>();
    if (parent.getChildren() == null)
      parent.setChildren(new ArrayList<>());
    this.parents.add(parent);
    parent.children.add(this);
  }

}
