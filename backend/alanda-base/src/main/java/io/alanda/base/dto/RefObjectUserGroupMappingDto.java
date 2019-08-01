/**
 * 
 */
package io.alanda.base.dto;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author jlo
 */
@Entity
@Table(name = "REF_OBJECT_USER_GROUP_MAPPING")
public class RefObjectUserGroupMappingDto {

  private Long guid;

  private String refObjectType;

  private Long refObjectId;

  private String roleName;

  private Long userId;

  private Long groupId;

  /**
   * 
   */
  public RefObjectUserGroupMappingDto() {
    // TODO Auto-generated constructor stub
  }

  public String getRefObjectType() {
    return refObjectType;
  }

  public void setRefObjectType(String refObjectType) {
    this.refObjectType = refObjectType;
  }

  public Long getRefObjectId() {
    return refObjectId;
  }

  public void setRefObjectId(Long refObjectId) {
    this.refObjectId = refObjectId;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getGroupId() {
    return groupId;
  }

  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

}
