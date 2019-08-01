/**
 * 
 */
package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author jlo
 */
@Entity
@Table(name = "REF_OBJECT_USER_GROUP_MAPPING")
public class RefObjectUserGroupMapping extends AbstractAuditEntity {

  @Column(name = "REF_OBJECT_TYPE")
  private String refObjectType;

  @Column(name = "REF_OBJECT_ID")
  private Long refObjectId;

  @Column(name = "ROLE_NAME")
  private String roleName;

  @Column(name = "USER_ID")
  private Long userId;

  @Column(name = "GROUP_ID")
  private Long groupId;


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

}
