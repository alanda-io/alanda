/**
 * 
 */
package io.alanda.base.dto;

import java.io.Serializable;

/**
 * @author jlo
 */

public class InternalContactDto implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -7972033378718103878L;
  
  private long guid;

  private String roleName;

  private String userName;

  private String firstName;

  private String surName;

  private String email;

  private String fullName;

  /**
   * 
   */
  public InternalContactDto() {
    // TODO Auto-generated constructor stub
  }

  /**
   * 
   */
  public InternalContactDto(PmcUserDto user, String roleName) {
    this.roleName = roleName;
    this.firstName = user.getFirstName();
    this.surName = user.getSurname();
    this.fullName = user.getDisplayName();
    this.email = user.getEmail();
    this.guid = user.getGuid();
    this.userName = user.getLoginName();
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getSurName() {
    return surName;
  }

  public void setSurName(String surName) {
    this.surName = surName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public long getGuid() {
    return guid;
  }

  public void setGuid(long guid) {
    this.guid = guid;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

}
