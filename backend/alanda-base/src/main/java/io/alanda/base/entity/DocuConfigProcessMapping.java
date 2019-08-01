package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
@Entity
@Table(name = "PMC_DOCU_CONFIG_PROC_MAPPING")
public class DocuConfigProcessMapping {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "PROC_DEF_KEY")
  private String processDefKey;

  @ManyToOne
  @JoinColumn(name = "DOCU_CFG_MAPPING_ID")
  private DocuConfigMapping docuConfigMapping;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProcessDefKey() {
    return processDefKey;
  }

  public void setProcessDefKey(String processDefKey) {
    this.processDefKey = processDefKey;
  }

  public DocuConfigMapping getDocuConfigMapping() {
    return docuConfigMapping;
  }

  public void setDocuConfigMapping(DocuConfigMapping docuConfigMapping) {
    this.docuConfigMapping = docuConfigMapping;
  }
}
