package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_PROPERTY_STORE")
@NamedQueries({
  @NamedQuery(name = "PmcProperty.getPmcProperty", query = "SELECT p FROM PmcProperty p " +
    "WHERE p.key = :key AND p.entityId = :entityId AND p.entityType = :entityType " +
    "AND p.pmcProject.guid = :pmcProjectGuid")
})
public class PmcProperty extends AbstractAuditEntity {
    
  @ManyToOne
  @JoinColumn(name = "REF_PMC_PROJECT")
  private PmcProject pmcProject;
  
  @Column(name = "REF_ENTITY_ID")
  private Long entityId;
  
  @Column(name = "REF_ENTITY_TYPE")
  private String entityType;
  
  @Column(name = "KEY")
  private String key;

  @Column(name = "VALUE")
  private String value;
  
  @Column(name = "VALUE_TYPE")
  private String valueType;

  public PmcProperty() {
    super();
  }
  
  public PmcProject getPmcProject() {
    return pmcProject;
  }

  
  public void setPmcProject(PmcProject pmcProject) {
    this.pmcProject = pmcProject;
  }

  
  public Long getEntityId() {
    return entityId;
  }

  
  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  
  public String getEntityType() {
    return entityType;
  }

  
  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  
  public String getKey() {
    return key;
  }

  
  public void setKey(String key) {
    this.key = key;
  }

  
  public String getValue() {
    return value;
  }

  
  public void setValue(String value) {
    this.value = value;
  }

  
  public String getValueType() {
    return valueType;
  }

  
  public void setValueType(String valueType) {
    this.valueType = valueType;
  }


  @Override
  public String toString() {
    String pmcProjectId = pmcProject != null ? pmcProject.getProjectId() : "null";
    return "PmcProperty [pmcProject=" +
       pmcProjectId +
      ", entityId=" +
      entityId +
      ", entityType=" +
      entityType +
      ", key=" +
      key +
      ", value=" +
      value +
      ", valueType=" +
      valueType +
      "]";
  }
}
