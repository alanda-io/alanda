package io.alanda.base.batch;

import java.io.Serializable;

public class BatchProjectProcessDto implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1531241042776425233L;

  public BatchProjectProcessDto() {
    super();
  }

  public BatchProjectProcessDto(String name, String comment) {
    super();
    this.name = name;
    this.comment = comment;
  }

  private String name;

  private String comment;



  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String toString() {
    return "process [" +
      (name != null ? "name=" + name + ", " : "") +
      (comment != null ? "comment=" + comment + ", " : "") +
      "]";
  }
}