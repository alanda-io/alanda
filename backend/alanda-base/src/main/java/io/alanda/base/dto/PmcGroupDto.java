/**
 * 
 */
package io.alanda.base.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jlo
 */
public class PmcGroupDto {

  private Long guid;

  private String groupName;

  private String longName;

  private String email;

  private String phone;

  private String groupSource;

  private boolean active;

  private int version;

  private List<PmcPermissionDto> permissions;

  private List<PmcRoleDto> roles;

  public List<String> findRolesWithPrefix(String prefix) {
    List<String> retVal = new ArrayList<>();
    if (roles == null)
      return retVal;
    for (PmcRoleDto r : roles) {
      if (r.getName().startsWith(prefix))
        retVal.add(r.getName());
    }
    return retVal;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getLongName() {
    return longName;
  }

  public void setLongName(String longName) {
    this.longName = longName;
  }

  public String getGroupSource() {
    return groupSource;
  }

  public void setGroupSource(String groupSource) {
    this.groupSource = groupSource;
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public List<PmcPermissionDto> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<PmcPermissionDto> permissions) {
    this.permissions = permissions;
  }

  public List<PmcRoleDto> getRoles() {
    return roles;
  }

  public void setRoles(List<PmcRoleDto> roles) {
    this.roles = roles;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

}
