/**
 * 
 */
package io.alanda.base.batch;

import java.io.Serializable;

/**
 * @author developer
 */
public class ExcelRoleConfig implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -4010936171891690078L;

  public enum RoleType {
      FULLNAME,
      USERNAME,
      GUID,
      CUSTOM
  }

  public static final String PREFIX = "role.";

  private String name;

  private RoleType type;

  private int col;

  /**
   * 
   */
  public ExcelRoleConfig() {
    // TODO Auto-generated constructor stub
  }

  /**
   * 
   */
  public ExcelRoleConfig config(int col, String config) {
    this.col = col;
    int lastDot = config.lastIndexOf(".");
    if (lastDot <= 0){
      this.type = RoleType.FULLNAME;
      this.name = config;
    }else{
      String sType = config.substring(lastDot + 1);
      this.type = RoleType.valueOf(sType);
      this.name = config.substring(0, lastDot);
    }
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCol() {
    return col;
  }

  public void setCol(int col) {
    this.col = col;
  }

  public RoleType getType() {
    return type;
  }

  public void setType(RoleType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "ExcelRoleConfig [" +
      (name != null ? "name=" + name + ", " : "") +
      (type != null ? "type=" + type + ", " : "") +
      "col=" +
      col +
      "]";
  }

}
