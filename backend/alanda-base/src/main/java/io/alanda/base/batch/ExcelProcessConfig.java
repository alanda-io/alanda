package io.alanda.base.batch;

import java.io.Serializable;

public class ExcelProcessConfig implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = -3632219346117793860L;

  public static final String PREFIX = "process.";

  private Integer colStart;

  private Integer colComment;

  private String name;

  public ExcelProcessConfig() {
    // TODO Auto-generated constructor stub
  }

  /**
   * 
   */
  public ExcelProcessConfig config(int col, String config) {
    //this.col = col;

    int lastDot = config.lastIndexOf(".");
    if (lastDot <= 0)
      throw new IllegalArgumentException("Column #" + col + ": " + config + " invalid process spec.");
    String sType = config.substring(lastDot + 1);
    this.name = config.substring(0, lastDot);
    if (sType.equals("start"))
      this.colStart = col;
    else if (sType.equals("comment"))
      this.colComment = col;
    else
      throw new IllegalArgumentException("Column #" + col + ": " + config + " invalid process spec.");
    return this;
  }

  public Integer getColStart() {
    return colStart;
  }

  public void setColStart(Integer colStart) {
    this.colStart = colStart;
  }

  public Integer getColComment() {
    return colComment;
  }

  public void setColComment(Integer colComment) {
    this.colComment = colComment;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "ExcelProcessConfig [" +
      (colStart != null ? "colStart=" + colStart + ", " : "") +
      (colComment != null ? "colComment=" + colComment + ", " : "") +
      (name != null ? "name=" + name : "") +
      "]";
  }

  public void merge(ExcelProcessConfig c) {
    if (c.getColComment() != null)
      this.setColComment(c.getColComment());
    if (c.getColStart() != null)
      this.setColStart(c.getColStart());

  }

}
