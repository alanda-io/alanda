package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_MILESTONE")
public class Milestone extends AbstractAuditEntity {
  
  @Column(name = "IDNAME")
  String idName;
  
  @Column(name = "DESCRIPTION")
  String description;

  public Milestone() {
    super();
  }

  
  public String getIdName() {
    return idName;
  }

  
  public void setIdName(String idName) {
    this.idName = idName;
  }

  
  public String getDescription() {
    return description;
  }

  
  public void setDescription(String description) {
    this.description = description;
  }
  
}
