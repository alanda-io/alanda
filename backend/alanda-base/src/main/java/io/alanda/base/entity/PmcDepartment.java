package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_DEPARTMENT")
public class PmcDepartment extends AbstractEntity {

  @Column(name = "IDNAME")
  private String idName;

  @Column(name = "NAME")
  private String name;

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "PmcDepartment{" + "idName='" + idName + '\'' + ", name='" + name + '\'' + "} " + super.toString();
  }
}
