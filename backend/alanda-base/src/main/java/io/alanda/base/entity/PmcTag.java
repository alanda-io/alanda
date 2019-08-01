package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "PMC_TAG", uniqueConstraints = {@UniqueConstraint(columnNames = {"TEXT"})})
public class PmcTag {
  
  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_sequence")
  @SequenceGenerator(name = "entity_sequence", sequenceName = "SEQ_GUID_PK", allocationSize = 1)
  private Long id;

  @Column(name = "TEXT")
  private String text;
  
  public PmcTag() {
    // TODO Auto-generated constructor stub
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
  
  //  @Override
  //  public Long getGuid() {
  //    // TODO Auto-generated method stub
  //    return super.getGuid();
  //  }
  //  
  //  @Override
  //  public void setGuid(Long guid) {
  //    // TODO Auto-generated method stub
  //    super.setGuid(guid);
  //  }

  

}
