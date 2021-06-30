/**
 * 
 */
package io.alanda.base.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import io.alanda.base.util.UserContext;

/**
 * @author jlo
 */
@Entity
@Table(name = "PMC_USER")
@NamedQueries({
@NamedQuery(name = "PmcUser.getByLoginName", query = "Select u from PmcUser u where u.loginName = :loginName")})
@NamedNativeQueries({
@NamedNativeQuery(name = "PmcUser.searchByGroup", query = "select distinct u.* FROM PMC_USER u, PMC_USER_GROUP ug where lower(u.firstname) ||' ' ||lower(u.surname)  like :search and u.guid=ug.REF_USER and ug.ref_group=:groupId and ug.select_contact=1", resultClass = PmcUser.class),
@NamedNativeQuery(name = "PmcUser.search", query = "select distinct u.* FROM PMC_USER u where lower(u.firstname) ||' ' ||lower(u.surname)  like :search", resultClass = PmcUser.class),
@NamedNativeQuery(name = "PmcUser.getByRole", query = "select u.* FROM PMC_USER u, PMC_USER_ROLE ur where u.guid=ur.REF_USER and ur.ref_role=:roleId", resultClass = PmcUser.class),
@NamedNativeQuery(name = "PmcUser.getByRoleInherited", query = "select u.* from " +
    "(select ug.ref_user, gr.ref_role from pmc_user_group ug inner join pmc_group_role gr on (ug.ref_group=gr.ref_group) union " +
    "select ref_user, ref_role from pmc_user_role) " +
    "ur inner join pmc_user u on (u.guid=ur.ref_user) where ur.ref_role=:roleId", resultClass = PmcUser.class)})
public class PmcUser implements Serializable {

  private static final long serialVersionUID = 4750557031797349278L;

  @Id
  private Long guid;

  @Column(name = "EXTERNALGUID")
  private Long externalGuid;

  @Version
  @Column(name = "VERSION")
  private Long version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "CREATED")
  private Date createDate;

  @Column(name = "CREATEUSER")
  private Long createUser;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "LASTUPDATE")
  private Date updateDate;

  @Column(name = "UPDATEUSER")
  private Long updateUser;

  private String loginName;

  private String firstName;

  private String surname;

  private String email;

  private String mobile;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "PMC_USER_GROUP", joinColumns = @JoinColumn(name = "REF_USER", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "REF_GROUP", referencedColumnName = "GUID"))
  private Set<PmcGroup> groupList = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "PMC_USER_ROLE", joinColumns = @JoinColumn(name = "REF_USER", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "REF_ROLE", referencedColumnName = "GUID"))
  private List<PmcRole> roleList;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "PMC_USER_PERMISSION", joinColumns = @JoinColumn(name = "REF_USER", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "REF_PERMISSION", referencedColumnName = "GUID"))
  private List<PmcPermission> permissionList;

  @Column(name = "LOCKED")
  private boolean locked;

  @Column(name = "PMC_DEPARTMENT")
  private Long pmcDepartment;

  @PrePersist
  void onCreate() {
    this.setCreateDate(new Date());
    this.setCreateUser(UserContext.getUser().getGuid());
  }

  @Column(name = "SOURCE")
  private String source;

  @Column(name = "COMPANY")
  private String company;

  @PreUpdate
  void onUpdate() {
    this.setUpdateDate(new Date());
    this.setUpdateUser(UserContext.getUser().getGuid());
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Set<PmcGroup> getGroupList() {
    return groupList;
  }

  public void setGroupList(Set<PmcGroup> groupList) {
    this.groupList = groupList;
  }

  public List<PmcPermission> getPermissionList() {
    return permissionList;
  }

  public void setPermissionList(List<PmcPermission> permissions) {
    this.permissionList = permissions;
  }

  public List<PmcRole> getRoleList() {
    return roleList;
  }

  public void setRoleList(List<PmcRole> roleList) {
    this.roleList = roleList;
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public String getDisplayName() {
    return this.getFirstName() + " " + this.getSurname();
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
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

  public void setCreateUser(Long createUser) {
    this.createUser = createUser;
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

  public Long getExternalGuid() {
    return externalGuid;
  }

  public void setExternalGuid(Long externalGuid) {
    this.externalGuid = externalGuid;
  }

  public Long getPmcDepartment() {
    return pmcDepartment;
  }

  public void setPmcDepartment(Long pmcDepartment) {
    this.pmcDepartment = pmcDepartment;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  @Override
  public String toString() {
    return "PmcUser [guid=" +
      guid +
      ", externalGuid=" +
      externalGuid +
      ", version=" +
      version +
      ", createDate=" +
      createDate +
      ", createUser=" +
      createUser +
      ", updateDate=" +
      updateDate +
      ", updateUser=" +
      updateUser +
      ", loginName=" +
      loginName +
      ", firstName=" +
      firstName +
      ", surname=" +
      surname +
      ", email=" +
      email +
      ", mobile=" +
      mobile +
      ", groupList=" +
      groupList +
      ", roleList=" +
      roleList +
      ", permissionList=" +
      permissionList +
      ", locked=" +
      locked +
      ", pmcDepartment=" +
      pmcDepartment +
      ", source=" +
      source +
      ", company=" +
      company +
      "]";
  }

}
