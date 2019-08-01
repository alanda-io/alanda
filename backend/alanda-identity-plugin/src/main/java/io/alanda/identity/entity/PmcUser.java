package io.alanda.identity.entity;

import java.io.Serializable;

/**
 * Table PMC_USER
 * 
 * @author jlo
 */
public class PmcUser implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long guid;

  private String loginName;

  private String firstName;

  private String surname;

  private String email;

  private String mobile;

  /**
   * Column LOGINNAME
   * 
   * @return the loginName
   */
  public String getLoginName() {
    return loginName;
  }

  /**
   * @param loginName the loginName to set
   */
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  /**
   * Column SURNAME
   * 
   * @return the surname
   */
  public String getSurname() {
    return surname;
  }

  /**
   * @param surname the surname to set
   */
  public void setSurname(String surname) {
    this.surname = surname;
  }

  /**
   * Column GUID
   * 
   * @return the guid
   */
  public Long getGuid() {
    return guid;
  }

  /**
   * @param guid the guid to set
   */
  public void setGuid(Long guid) {
    this.guid = guid;
  }

  /**
   * Column FIRSTNAME
   * 
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @param firstName firstName name to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Column EMAIL
   * 
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Column MOBIL
   * 
   * @return the mobile
   */
  public String getMobile() {
    return mobile;
  }

  /**
   * @param mobile the mobile to set
   */
  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

}
