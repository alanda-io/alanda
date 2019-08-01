/**
 * 
 */
package io.alanda.base.batch;

import java.io.Serializable;

import io.alanda.base.service.PmcPropertyService.PmcPropertyType;

/**
 * @author developer
 */
public class ExcelPropertyConfig implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -8124606280234095782L;

  public static final String PREFIX = "property.";

  private PmcPropertyType type;

  private String name;

  private int col;

  /**
   * 
   */
  public ExcelPropertyConfig() {
    // TODO Auto-generated constructor stub
  }

  /**
   * 
   */
  public ExcelPropertyConfig config(int col, String config) {
    this.col = col;

    int lastDot = config.lastIndexOf(".");
    if (lastDot <= 0)
      throw new IllegalArgumentException("Column #" + col + ": " + config + " has no type specified");
    String sType = config.substring(lastDot + 1);
    this.type = PmcPropertyType.valueOf(sType);
    this.name = config.substring(0, lastDot);
    return this;
  }

  public PmcPropertyType getType() {
    return type;
  }

  public void setType(PmcPropertyType type) {
    this.type = type;
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

}
