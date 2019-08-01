package io.alanda.base.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.alanda.base.type.DocuConfigMappingType;

/**
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
@Entity
@Table(name = "PMC_DOCU_CONFIG_MAPPING")
@NamedQueries({
  @NamedQuery(name = "DocuConfigMapping.getByProjectTypeId", query = "Select m from DocuConfigMapping m where m.projectTypeId = :projectTypeId"),
  @NamedQuery(name = "DocuConfigMapping.getByRefObjectType", query = "Select m from DocuConfigMapping m where m.refObjectType = :refObjectType")})
public class DocuConfigMapping {

  @Id
  @Column(name = "ID")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "DOCU_CONFIG_ID")
  private DocuConfig docuConfig;

  @Enumerated(EnumType.STRING)
  @Column(name = "TYPE")
  private DocuConfigMappingType type;

  @Column(name = "RECON_TYPE_ID")
  private Long projectTypeId;

  @Column(name = "REF_OBJECT_TYPE")
  private String refObjectType;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "DOCU_CFG_MAPPING_ID")
  private List<DocuConfigProcessMapping> processMappings;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DocuConfig getDocuConfig() {
    return docuConfig;
  }

  public void setDocuConfig(DocuConfig docuConfig) {
    this.docuConfig = docuConfig;
  }

  public DocuConfigMappingType getType() {
    return type;
  }

  public void setType(DocuConfigMappingType type) {
    this.type = type;
  }

  public String getRefObjectType() {
    return refObjectType;
  }

  public void setRefObjectType(String refObjectType) {
    this.refObjectType = refObjectType;
  }

  public List<DocuConfigProcessMapping> getProcessMappings() {
    return processMappings;
  }

  public void setProcessMappings(List<DocuConfigProcessMapping> processMappings) {
    this.processMappings = processMappings;
  }

  public Long getProjectTypeId() {
    return projectTypeId;
  }

  public void setProjectTypeId(Long projectTypeId) {
    this.projectTypeId = projectTypeId;
  }

}
