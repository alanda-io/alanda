package io.alanda.identity.entity;

import java.io.Serializable;

/**
 * Table PMC_GROUP
 * 
 * @author jlo
 */
public class PmcGroup implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long guiId;

  private String groupName;

  /**
   * Column GUIID
   * 
   * @return the guiId
   */
  public Long getGuiId() {
    return guiId;
  }

  /**
   * @param guiId the guiId to set
   */
  public void setGuiId(Long guiId) {
    this.guiId = guiId;
  }

  /**
   * Column GROUPNAME
   * 
   * @return the groupName
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * @param groupName the groupName to set
   */
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

}
