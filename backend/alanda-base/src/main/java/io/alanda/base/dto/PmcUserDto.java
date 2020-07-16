/** */
package io.alanda.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

/** @author jlo */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PmcUserDto implements AuthenticationInfo, AuthorizationInfo, Cloneable {

  private static final long serialVersionUID = -269573544265667131L;

  private Long guid;

  private Long externalGuid;

  private String source;

  private String company;

  private String loginName;

  private String firstName;

  private String surname;

  private String email;

  private String mobile;

  @JsonIgnore private List<PmcGroupDto> groupList;

  private Collection<String> groups;

  private Set<String> roles;

  private Set<String> permissions;

  private boolean locked;

  private String displayName;

  @JsonInclude(value = Include.NON_NULL)
  private String password;

  @JsonInclude(value = Include.NON_NULL)
  private String password2;

  @JsonInclude(value = Include.NON_NULL)
  private String oldPassword;

  private Boolean sso;

  private Long pmcDepartment;

  private String runAs;

  @Override
  public PmcUserDto clone() {
    try {
      return (PmcUserDto) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public Long getExternalGuid() {
    return externalGuid;
  }

  public void setExternalGuid(Long externalGuid) {
    this.externalGuid = externalGuid;
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

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public List<PmcGroupDto> getGroupList() {
    return groupList;
  }

  public void setGroupList(List<PmcGroupDto> groupList) {
    this.groupList = groupList;
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public Collection<String> getGroups() {
    return groups;
  }

  public void setGroups(Collection<String> groups) {
    this.groups = groups;
  }

  @Override
  public String toString() {
    return "PmcUserDto [guid="
        + guid
        + ", externalGuid="
        + externalGuid
        + ", source="
        + source
        + ", company="
        + company
        + ", loginName="
        + loginName
        + ", firstName="
        + firstName
        + ", surname="
        + surname
        + ", email="
        + email
        + ", mobile="
        + mobile
        + ", groupList="
        + groupList
        + ", groups="
        + groups
        + ", roles="
        + roles
        + ", permissions="
        + permissions
        + ", locked="
        + locked
        + ", displayName="
        + displayName
        + ", password="
        + password
        + ", password2="
        + password2
        + ", oldPassword="
        + oldPassword
        + ", sso="
        + sso
        + ", pmcDepartment="
        + pmcDepartment
        + ", runAs="
        + runAs
        + "]";
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public boolean isAdmin() {
    if (this.groupList != null) {
      for (PmcGroupDto g : this.groupList) {
        if ("admin".equals(g.getGroupName())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isInGroup(PmcGroupDto groupDto) {
    return isInGroup(groupDto.getGuid());
  }

  public boolean isInGroup(Long groupId) {
    for (PmcGroupDto g : this.groupList) {
      if (g.getGuid().equals(groupId)) return true;
    }
    return false;
  }

  public boolean isInGroup(String groupName) {
    for (PmcGroupDto g : this.groupList) {
      if (g.getGroupName().equals(groupName)) return true;
    }
    return false;
  }

  public PmcGroupDto getGroup(String groupName) {
    for (PmcGroupDto g : this.groupList) {
      if (g.getGroupName().equals(groupName)) return g;
    }
    return null;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword2() {
    return password2;
  }

  public void setPassword2(String password2) {
    this.password2 = password2;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }

  public void setPermissions(Set<String> permissions) {
    this.permissions = permissions;
  }

  @JsonIgnore
  @Override
  public PrincipalCollection getPrincipals() {
    return new SimplePrincipalCollection(this.loginName, "JPA");
  }

  @JsonIgnore
  @Override
  public Object getCredentials() {
    return this.password;
  }

  @Override
  public Set<String> getRoles() {
    return roles;
  }

  @Override
  public Set<String> getStringPermissions() {
    return permissions;
  }

  public void setStringPermissions(Set<String> permissions) {
    this.permissions = permissions;
  }

  @Override
  public Set<Permission> getObjectPermissions() {
    return new HashSet<>();
  }

  public Boolean getSso() {
    return sso;
  }

  public void setSso(Boolean sso) {
    this.sso = sso;
  }

  public Long getPmcDepartment() {
    return pmcDepartment;
  }

  public void setPmcDepartment(Long pmcDepartment) {
    this.pmcDepartment = pmcDepartment;
  }

  public String getRunAs() {
    return runAs;
  }

  public void setRunAs(String runAs) {
    this.runAs = runAs;
  }
}
